package com.taebong.szs.domain.user.vo;

import com.taebong.szs.controller.dto.DeductResponseDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Builder
@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class Deduction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String deductionAmount;

    @Enumerated(EnumType.STRING)
    private DeductionCategory deductionCategory;

    private String totalPayment;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    public DeductResponseDto toDeductResponseDto() {
        return DeductResponseDto.builder()
                .id(id)
                .deductionAmount(deductionAmount)
                .deductionCategory(deductionCategory)
                .totalPayment(totalPayment)
                .userId(user.getUserId())
                .build();
    }
}
