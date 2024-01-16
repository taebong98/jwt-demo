package com.taebong.szs.controller.dto;

import com.taebong.szs.domain.user.vo.DeductionCategory;
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
    private DeductionCategory deductionCategory;
    private String totalPayment;
    private String userId;
}
