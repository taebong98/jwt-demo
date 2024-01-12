package com.taebong.szs.domain;

import com.taebong.szs.common.exception.DataNotFoundException;
import com.taebong.szs.common.exception.ForbiddenException;
import com.taebong.szs.common.exception.LoginException;
import com.taebong.szs.common.jwt.JwtTokenProvider;
import com.taebong.szs.controller.dto.LoginDto;
import com.taebong.szs.domain.repository.UserRepository;
import com.taebong.szs.domain.vo.AllowedUsers;
import com.taebong.szs.domain.vo.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;
import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final AllowedUsers allowedUsers;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;

    @Transactional
    public void signup(User user) {
        log.info("start signup(). userId: {}", user.getUserId());

        if (!isValidUser(user.getName(), user.getRegNo())) {
            throw new ForbiddenException("회원가입 할 수 없는 사용자");
        }

        User encodedUser = getEncodedUser(user);
        userRepository.save(encodedUser);
    }

    public String login(LoginDto loginDto) {
        log.info("login() ID: {}, PASSWORD: {}", loginDto.getUserId(), loginDto.getPassword());

        Optional<User> optionalUser = userRepository.findByUserId(loginDto.getUserId());
        User foundUser = optionalUser.orElseThrow(() -> {
            throw new DataNotFoundException("조회된 사용자가 없습니다.");
        });

        if (!passwordEncoder.matches(loginDto.getPassword(), foundUser.getPassword())) {
            throw new LoginException("비밀번호 일치하지 않음");
        };

        return jwtTokenProvider.createToken(foundUser);
    }

    private User getEncodedUser(User user) {
        String encodedPassword = passwordEncoder.encode(user.getPassword());
        String encodedRegNo = passwordEncoder.encode(user.getRegNo());
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
}
