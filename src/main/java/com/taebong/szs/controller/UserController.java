package com.taebong.szs.controller;

import com.taebong.szs.controller.dto.LoginDto;
import com.taebong.szs.controller.dto.TokenResponseDto;
import com.taebong.szs.controller.dto.UserSignupDto;
import com.taebong.szs.domain.UserService;
import com.taebong.szs.domain.vo.User;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@Tag(name = "사용자", description = "사용자 관련 API")
@RestController
@RequestMapping("/szs")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @Operation(summary = "회원가입")
    @PostMapping("/signup")
    public String postUser(@RequestBody @Valid UserSignupDto userSignupDto) {
        User user = userSignupDto.toUser();
        userService.signup(user);
        return "OK";
    }

    @Operation(summary = "로그인")
    @PostMapping("/login")
    public TokenResponseDto login(@RequestBody LoginDto loginDto) {
        String accessToken = userService.login(loginDto);
        return TokenResponseDto.builder().token(accessToken).build();
    }
}
