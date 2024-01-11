package com.taebong.szs.controller.dto;

import com.taebong.szs.domain.vo.User;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class UserSignupDto {
    @NotBlank(message = "사용자 아이디는 필수 입력 항목입니다.")
    @Schema(description = "아이디", defaultValue = "hong12")
    private String userId;

    @NotBlank(message = "비밀번호는 필수 입력 항목입니다.")
    @Schema(description = "비밀번호", defaultValue = "123456")
    private String password;

    @NotBlank(message = "이름은 필수 입력 항목입니다.")
    @Schema(description = "이름", defaultValue = "홍길동")
    private String name;

    @Pattern(regexp = "^(?:[0-9]{2}(?:0[1-9]|1[0-2])(?:0[1-9]|[1,2][0-9]|3[0,1]))-[1-4][0-9]{6}$")
    @NotBlank(message = "주민등록번호는 필수 입력 항목입니다.")
    @Schema(description = "주민등록번호", defaultValue = "860824-1655068")
    private String regNo;

    public User toUser() {
        return User.builder()
                .userId(userId)
                .password(password)
                .name(name)
                .regNo(regNo)
                .build();
    }
}
