package com.taebong.szs.domain.user.vo;

import com.taebong.szs.controller.dto.UserResponseDto;
import com.taebong.szs.controller.dto.UserScrapResponseDto;
import lombok.*;

import javax.persistence.*;
import java.util.List;
import java.util.stream.Collectors;

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

    @Setter
    public String taxAmount;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<Deduction> deductionList;

    public UserResponseDto toUserResponseDto() {
        return UserResponseDto.builder()
                .userId(userId)
                .name(name)
                .password(password)
                .regNo(regNo)
                .build();
    }

    public UserScrapResponseDto toScarpUserResponseDto() {
        return UserScrapResponseDto.builder()
                .userId(userId)
                .name(name)
                .taxAmount(taxAmount)
                .deductionList(deductionList.stream().map(Deduction::toDeductResponseDto).collect(Collectors.toList()))
                .build();
    }
}