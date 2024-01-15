package com.taebong.szs.controller.dto.scrapapidto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@ToString
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ScrapResponseDto {
    @JsonProperty("status")
    private String status;

    @JsonProperty("data")
    private ScrapDataResponseDto data;

    @JsonProperty("errors")
    private JsonNode errors;
}
