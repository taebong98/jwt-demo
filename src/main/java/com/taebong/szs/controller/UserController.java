package com.taebong.szs.controller;

import com.taebong.szs.controller.dto.UserSignupDto;
import com.taebong.szs.domain.UserService;
import com.taebong.szs.domain.vo.User;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
@RequestMapping("/szs")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @PostMapping("/signup")
    public void postUser(@RequestBody @Valid UserSignupDto userSignupDto) {
        User user = userSignupDto.toUser();
        userService.signup(user);
    }
}
