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
public class SalaryResponseDto {
    @JsonProperty("소득내역")
    private String incomeType;

    @JsonProperty("총지급액")
    private String totalAmount;

    @JsonProperty("업무시작일")
    private String workStartDate;

    @JsonProperty("기업명")
    private String companyName;

    @JsonProperty("이름")
    private String name;

    @JsonProperty("지급일")
    private String paymentDate;

    @JsonProperty("업무종료일")
    private String workEndDate;

    @JsonProperty("주민등록번호")
    private String residentRegistrationNumber;

    @JsonProperty("소득구분")
    private String incomeCategory;

    @JsonProperty("사업자등록번호")
    private String businessRegistrationNumber;
}
