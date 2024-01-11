package com.taebong.szs.domain;

import com.taebong.szs.common.exception.ForbiddenException;
import com.taebong.szs.domain.repository.UserRepository;
import com.taebong.szs.domain.vo.AllowedUsers;
import com.taebong.szs.domain.vo.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final AllowedUsers allowedUsers;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public void signup(User user) {
        log.info("start signup(). userId: {}", user.getUserId());

        if (!isValidUser(user.getName(), user.getRegNo())) {
            throw new ForbiddenException("회원가입 할 수 없는 사용자");
        }

        User encodedUser = getEncodedUser(user);
        userRepository.save(encodedUser);
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
