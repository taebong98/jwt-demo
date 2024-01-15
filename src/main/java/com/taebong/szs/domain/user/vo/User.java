package com.taebong.szs.domain.user.vo;

import com.taebong.szs.controller.dto.UserResponseDto;
import com.taebong.szs.domain.deduction.vo.Deduction;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.List;

@Getter
@Builder
@Entity(name = "szs_user")
@NoArgsConstructor
@AllArgsConstructor
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_test_id")
    private Long id;

    @Column(unique = true, nullable = false)
    private String userId;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private String name;

    @Column(unique = true, nullable = false)
    private String regNo;

    public UserResponseDto toUserResponseDto() {
        return UserResponseDto.builder()
                .userId(userId)
                .name(name)
                .password(password)
                .regNo(regNo)
                .build();
    }
}