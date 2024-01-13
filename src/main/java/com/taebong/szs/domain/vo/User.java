package com.taebong.szs.domain.vo;

import com.taebong.szs.controller.dto.UserResponseDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Getter
@Builder
@Entity(name = "szs_user")
@NoArgsConstructor
@AllArgsConstructor
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
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
