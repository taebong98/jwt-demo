package com.taebong.szs.controller.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DeductResponseDto {
    private Long id;
    private String deductionAmount;
    private String incomeCategory;
    private String totalPayment;
    private String userId;
}
