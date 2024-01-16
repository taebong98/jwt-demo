package com.taebong.szs.domain;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.taebong.szs.common.exception.DataNotFoundException;
import com.taebong.szs.common.exception.ForbiddenException;
import com.taebong.szs.common.exception.LoginException;
import com.taebong.szs.common.jwt.JwtTokenProvider;
import com.taebong.szs.common.util.CryptUtils;
import com.taebong.szs.common.util.SpecialTaxCreditAmountUtil;
import com.taebong.szs.controller.dto.LoginDto;
import com.taebong.szs.controller.dto.RefundResponseDto;
import com.taebong.szs.controller.dto.ScrapRequestDto;
import com.taebong.szs.controller.dto.scrapapidto.DeductionResponseDto;
import com.taebong.szs.controller.dto.scrapapidto.ScrapResponseDto;
import com.taebong.szs.domain.user.repository.DeductionRepository;
import com.taebong.szs.domain.user.vo.Deduction;
import com.taebong.szs.domain.user.repository.UserRepository;
import com.taebong.szs.domain.user.vo.AllowedUsers;
import com.taebong.szs.domain.user.vo.DeductionCategory;
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

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserService {
    @Value("${szs.scrap}")
    private String scrapEndPoint;

    private final UserRepository userRepository;
    private final DeductionRepository deductionRepository;
    private final AllowedUsers allowedUsers;
    private final JwtTokenProvider jwtTokenProvider;
    private final CryptUtils cryptUtils;
    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;
    private final SpecialTaxCreditAmountUtil specialTaxCreditAmountUtil;

    @Transactional
    public void signup(User user) {
        log.info("start signup(). userId: {}", user.getUserId());

        if (!isAllowedUser(user.getName(), user.getRegNo())) {
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
    public User getAndSaveScrapInfo(String token) {
        log.info("getUserScrap.");

        Claims claims = jwtTokenProvider.getClaims(token);
        User foundUser = getUserByUserId((String) claims.get("userId"));

        String requestBody = objectToRequestBody(foundUser.getName(), cryptUtils.decrypt(foundUser.getRegNo()));
        log.info("requestBody: {}", requestBody);

        ScrapResponseDto response = getScrapUserFromSzsApi(requestBody);
        log.info("Success API call. response: {}", response);

        saveScrapInfo(foundUser, response);
        return foundUser;
    }

    @Transactional(readOnly = true)
    public RefundResponseDto calculateRefund(String token) {
        Claims claims = jwtTokenProvider.getClaims(token);
        User foundUser = getUserByUserId((String) claims.get("userId"));
        String decidedTaxAmount = calculateDecidedTaxAmount(foundUser);
        String retirementDeductionAmount = getRetirementDeductionAmount(foundUser).toString();

        return RefundResponseDto.builder()
                .name(foundUser.getName())
                .decidedTaxAmount(decidedTaxAmount)
                .retirementPensionTaxCredit(retirementDeductionAmount)
                .build();
    }

    // 결정세액
    private String calculateDecidedTaxAmount(User user) {
        log.info("decidedTaxAmount. userId: {}", user.getUserId());

        // 산출세액
        BigDecimal taxAmount = stringToBigDecimal(user.getTaxAmount());
        log.info("산출세액: {}", taxAmount);

        // 근로소득 공제금액
        BigDecimal eitc = taxAmount.multiply(BigDecimal.valueOf(0.55));
        log.info("근로소득공제금액: {}", eitc);

        // 특별세액 또는 표준세액
        BigDecimal specialOrStandardCreditAmount = getSpecialOrStandardCreditAmount(user);
        log.info("특별세액 또는 표준세액: {}", specialOrStandardCreditAmount);

        // 퇴직연금세액공제금액
        BigDecimal retirementDeductionAmount = getRetirementDeductionAmount(user);
        log.info("퇴직연금세액공제금액: {}", retirementDeductionAmount);

        BigDecimal decidedTax = taxAmount.subtract(eitc).subtract(specialOrStandardCreditAmount).subtract(retirementDeductionAmount);
        if (decidedTax.compareTo(BigDecimal.ZERO) < 0) {
            return "0";
        }
        return decidedTax.toString();
    }

    private BigDecimal getSpecialOrStandardCreditAmount(User user) {
        log.info("getSpecialOrStandardCreditAmount. userId: {}", user.getUserId());
        BigDecimal special = BigDecimal.ZERO;
        List<Deduction> deductionList = user.getDeductionList();
        for (Deduction deduction : deductionList) {
            if (deduction.getDeductionCategory() == DeductionCategory.DONATION) {
                BigDecimal donation = specialTaxCreditAmountUtil.donation(stringToBigDecimal(deduction.getDeductionAmount()));
                special = special.add(donation);
            }
            else if (deduction.getDeductionCategory() == DeductionCategory.INSURANCE) {
                BigDecimal donation = specialTaxCreditAmountUtil.insure(stringToBigDecimal(deduction.getDeductionAmount()));
                special = special.add(donation);
            }
            else if (deduction.getDeductionCategory() == DeductionCategory.EDUCATION) {
                BigDecimal donation = specialTaxCreditAmountUtil.education(stringToBigDecimal(deduction.getDeductionAmount()));
                special = special.add(donation);
            }
            else if (deduction.getDeductionCategory() == DeductionCategory.MEDICAL) {
                BigDecimal donation = specialTaxCreditAmountUtil.medical(stringToBigDecimal(deduction.getDeductionAmount()), stringToBigDecimal(user.getTotalSalary()));
                special = special.add(donation);
            }
        }
        log.info("특별세액공제금: {}", special);

        if (special.compareTo(BigDecimal.valueOf(130000)) < 0) {
            log.info("특별세액공제금이 13만원보다 적음 -> 표준세액공제금으로 계산. {}", 130000);
            return BigDecimal.valueOf(130000);
        }

        return BigDecimal.valueOf(130000);
    }

    private BigDecimal getRetirementDeductionAmount(User user) {
        BigDecimal retirementDeductionAmount =  BigDecimal.ZERO;
        List<Deduction> deductionList = user.getDeductionList();
        for (Deduction deduction : deductionList) {
            if (deduction.getDeductionCategory() == DeductionCategory.RETIREMENT_PENSION) {
                retirementDeductionAmount = stringToBigDecimal(deduction.getTotalPayment()).multiply(BigDecimal.valueOf(0.15));
            }
        }
        return retirementDeductionAmount;
    }

    private User getUserByUserId(String userId) {
        log.info("getUserByUserId. userId: {}", userId);

        Optional<User> optionalUser = userRepository.findByUserId(userId);
        return optionalUser.orElseThrow(() -> {
            throw new DataNotFoundException("조회된 사용자 없음");
        });
    }

    private void saveScrapInfo(User foundUser, ScrapResponseDto response) {
        log.info("saveScrapInfo. userId: {}", foundUser.getUserId());

        String taxAmount = response.getData().getJsonListResponseDto().getTaxAmount();
        String totalSalary = response.getData().getJsonListResponseDto().getSalaryResponseDtoList().get(0).getTotalAmount();
        foundUser.setTaxAmount(taxAmount);
        foundUser.setTotalSalary(totalSalary);
        saveDeductibleAmount(response.getData().getJsonListResponseDto().getDeductionResponseDtoList(), foundUser);
    }

    private void saveDeductibleAmount(List<DeductionResponseDto> deductionResponseDto, User user) {
        log.info("getDeduction. userId: {}" , user.getUserId());

        List<Deduction> deductions = deductionResponseDto.stream()
                .map(responseDto -> {
                    DeductionCategory category = Arrays.stream(DeductionCategory.values())
                            .filter(c -> c.getValue().equals(responseDto.getIncomeCategory()))
                            .findFirst()
                            .orElseThrow(() -> new IllegalArgumentException("일치하는 공제 항목이 없음"));

                    return Deduction.builder()
                            .deductionAmount(responseDto.getAmount())
                            .totalPayment(responseDto.getTotalPayment())
                            .deductionCategory(category)
                            .user(user)
                            .build();
                })
                .collect(Collectors.toList());

        deductionRepository.saveAll(deductions);
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

    private boolean isAllowedUser(String name, String regNo) {
        log.info("isValidUser().");
        Map<String, String> allowedUserMap = allowedUsers.getAllowedUserMap();
        return allowedUserMap.containsKey(regNo) && allowedUserMap.get(regNo).equals(name);
    }

    private void checkEncoded(String str, String encode) {
        if (str.equals(encode)) {
            throw new RuntimeException("암호화 수행되지 않음");
        }
    }

    private String objectToRequestBody(String name, String regNo) {
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

    private ScrapResponseDto getScrapUserFromSzsApi(String requestBody) {
        log.info("start API Call: getScrapResponseFromSzsApi(). requestBody: {}", requestBody);
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

    private BigDecimal stringToBigDecimal(String numberString) {
        String cleanNumberString = numberString.replaceAll("[^\\d.]", "");
        return new BigDecimal(cleanNumberString);
    }
}
