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
public class ScrapDataResponseDto {
    @JsonProperty("jsonList")
    private JsonListResponseDto jsonListResponseDto;

    @JsonProperty("appVer")
    private String appVer;

    @JsonProperty("errMsg")
    private String errMsg;

    @JsonProperty("company")
    private String company;

    @JsonProperty("svcCd")
    private String svcCd;

    @JsonProperty("hostNm")
    private String hostNm;

    @JsonProperty("workerResDt")
    private String workerResDt;

    @JsonProperty("workerReqDt")
    private String workerReqDt;
}
