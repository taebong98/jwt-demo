package com.taebong.szs.controller.dto.scrapapidto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@ToString
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class DeductionResponseDto {
    @JsonProperty("금액")
    private String amount;

    @JsonProperty("소득구분")
    private String incomeCategory;

    @JsonProperty("총납임금액")
    private String totalPayment;
}
