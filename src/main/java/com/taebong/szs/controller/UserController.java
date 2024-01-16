package com.taebong.szs.controller;

import com.taebong.szs.controller.dto.*;
import com.taebong.szs.domain.UserService;
import com.taebong.szs.domain.user.vo.User;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.web.bind.annotation.*;

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

    @Operation(summary = "토큰으로 사용자 조회")
    @GetMapping("/me")
    public UserResponseDto getUser(@RequestHeader HttpHeaders headers) {
        String token = headers.getFirst("Authorization");
        User userInfo = userService.getUserInJwtToken(token);
        return userInfo.toUserResponseDto();
    }

    @Operation(summary = "가입한 유저의 정보를 스크랩 하고, 해당 정보를 데이터베이스에 저장")
    @PostMapping("/scrap")
    public UserScrapResponseDto scrap(@RequestHeader HttpHeaders headers) {
        User user = userService.getAndSaveScrapInfo(headers.getFirst("Authorization"));
        return user.toScarpUserResponseDto();
    }

    @Operation(summary = "유저의 스크랩 정보를 바탕으로 유저의 결정세액과 퇴직연금세액공제금액을 계산")
    @GetMapping("/refund")
    public RefundResponseDto refund(@RequestHeader HttpHeaders headers) {
        return userService.calculateRefund(headers.getFirst("Authorization"));
    }
}
