package com.taebong.szs.domain;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.taebong.szs.common.exception.DataNotFoundException;
import com.taebong.szs.common.exception.ForbiddenException;
import com.taebong.szs.common.exception.LoginException;
import com.taebong.szs.common.jwt.JwtTokenProvider;
import com.taebong.szs.common.util.CryptUtils;
import com.taebong.szs.controller.dto.LoginDto;
import com.taebong.szs.controller.dto.ScrapRequestDto;
import com.taebong.szs.controller.dto.scrap.ScrapResponseDto;
import com.taebong.szs.domain.deduction.DeductionService;
import com.taebong.szs.domain.user.repository.UserRepository;
import com.taebong.szs.domain.user.vo.AllowedUsers;
import com.taebong.szs.domain.user.vo.User;
import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.Map;
import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserService {
    @Value("${szs.scrap}")
    private String scrapEndPoint;

    private final UserRepository userRepository;
    private final AllowedUsers allowedUsers;
    private final JwtTokenProvider jwtTokenProvider;
    private final CryptUtils cryptUtils;
    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;
    private final DeductionService deductionService;

    @Transactional
    public void signup(User user) {
        log.info("start signup(). userId: {}", user.getUserId());

        if (!isValidUser(user.getName(), user.getRegNo())) {
            throw new ForbiddenException("회원가입 할 수 없는 사용자");
        }

        User encodedUser = getEncodedUser(user);
        userRepository.save(encodedUser);
    }

    @Transactional
    public String login(LoginDto loginDto) {
        log.info("login() ID: {}, PASSWORD: {}", loginDto.getUserId(), loginDto.getPassword());

        Optional<User> optionalUser = userRepository.findByUserId(loginDto.getUserId());
        User foundUser = optionalUser.orElseThrow(() -> {
            throw new DataNotFoundException("조회된 사용자가 없습니다.");
        });

        if (!cryptUtils.matches(loginDto.getPassword(), foundUser.getPassword())) {
            throw new LoginException("비밀번호 일치하지 않음");
        }

        return jwtTokenProvider.createToken(foundUser);
    }

    @Transactional(readOnly = true)
    public User getUserInJwtToken(String token) {
        log.info("getUserInJwtToken.");

        Claims claims = jwtTokenProvider.getClaims(token);
        return User.builder()
                .name((String) claims.get("name"))
                .userId((String) claims.get("userId"))
                .password(cryptUtils.decrypt((String) claims.get("password")))
                .regNo(cryptUtils.decrypt((String) claims.get("regNo")))
                .build();
    }

    @Transactional
    public void getUserScrap(String token) {
        log.info("getUserScrap.");

        User user = getUserInJwtToken(token);

        String requestBody = toRequestBody(user.getName(), user.getRegNo());
        log.info("requestBody: {}", requestBody);

        ScrapResponseDto response = getScrapResponseFromSzsApi(requestBody);
        log.info("Success API call. response: {}", response);

        deductionService.getDeduction(response.getData().getJsonListResponseDto().getDeductionResponseDtoList(), user);
    }

    private User getEncodedUser(User user) {
        String encodedPassword = cryptUtils.encrypt(user.getPassword());
        String encodedRegNo = cryptUtils.encrypt(user.getRegNo());

        checkEncoded(user.getPassword(), encodedPassword);
        checkEncoded(user.getRegNo(), encodedRegNo);

        return User.builder()
                .userId(user.getUserId())
                .name(user.getName())
                .password(encodedPassword)
                .regNo(encodedRegNo)
                .build();
    }

    private boolean isValidUser(String name, String regNo) {
        log.info("isValidUser().");
        Map<String, String> allowedUserMap = allowedUsers.getAllowedUserMap();
        return allowedUserMap.containsKey(regNo) && allowedUserMap.get(regNo).equals(name);
    }

    private void checkEncoded(String str, String encode) {
        if (str.equals(encode)) {
            throw new RuntimeException("암호화 수행되지 않음");
        }
    }

    private String toRequestBody(String name, String regNo) {
        log.info("dtoToJson().");
        ScrapRequestDto scrapRequestDto = ScrapRequestDto.builder()
                .name(name)
                .regNo(regNo)
                .build();

        try {
            return objectMapper.writeValueAsString(scrapRequestDto);
        } catch (JsonProcessingException e) {
            throw new RestClientException("마샬링 실패");
        }
    }

    private ScrapResponseDto getScrapResponseFromSzsApi(String requestBody) {
        log.info("start API Call: getScrapResponseFromSzsApi().");
        ResponseEntity<ScrapResponseDto> responseEntity = restTemplate.exchange(
                scrapEndPoint,
                HttpMethod.POST,
                new HttpEntity<>(requestBody),
                ScrapResponseDto.class
        );

        if (!responseEntity.getStatusCode().is2xxSuccessful()) {
            throw new RuntimeException("스크랩 URL 에러 발생");
        }

        return responseEntity.getBody();
    }
}
