package com.taebong.szs.controller.dto.scrap;

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
    private String status;
    private ScrapDataResponseDto data;
    private JsonNode errors;
}
