package com.taebong.szs.controller.dto.scrap;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.List;

@ToString
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class JsonListResponseDto {
    @JsonProperty("급여")
    private List<SalaryResponseDto> salaryResponseDtoList;

    @JsonProperty("산출세액")
    private String taxAmount;

    @JsonProperty("소득공제")
    private List<DeductionResponseDto> deductionResponseDtoList;
}
