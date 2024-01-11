package com.taebong.szs.domain;

import com.taebong.szs.common.exception.ForbiddenException;
import com.taebong.szs.domain.repository.UserRepository;
import com.taebong.szs.domain.vo.AllowedUsers;
import com.taebong.szs.domain.vo.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final AllowedUsers allowedUsers;

    @Transactional
    public void signup(User user) {
        log.info("start signup(). userId: {}", user.getUserId());

        if (!isValidUser(user.getName(), user.getRegNo())) {
            throw new ForbiddenException("회원가입 할 수 없는 사용자");
        }

        userRepository.save(user);
    }

    private boolean isValidUser(String name, String regNo) {
        log.info("isValidUser().");
        Map<String, String> allowedUserMap = allowedUsers.getAllowedUserMap();
        return allowedUserMap.containsKey(regNo) && allowedUserMap.get(regNo).equals(name);
    }
}
