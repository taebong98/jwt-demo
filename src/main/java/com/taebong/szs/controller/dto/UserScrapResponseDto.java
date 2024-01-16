package com.taebong.szs.controller.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserScrapResponseDto {
    private String userId;
    private String name;
    private String taxAmount;
    private String totalSalary;
    private List<DeductResponseDto> deductionList;
}
