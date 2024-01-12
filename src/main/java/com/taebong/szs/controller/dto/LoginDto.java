package com.taebong.szs.controller.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LoginDto {
    @Schema(description = "아이디", defaultValue = "hong12")
    private String userId;

    @Schema(description = "비밀번호", defaultValue = "123456")
    private String password;
}
