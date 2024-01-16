package com.taebong.szs.controller.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RefundResponseDto {
    @JsonProperty("이름")
    private String name;

    @JsonProperty("결정세액")
    private String decidedTaxAmount;

    @JsonProperty("퇴직연금세액공제")
    private String retirementPensionTaxCredit;
}
