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
public class ScrapRequestDto {
    @JsonProperty("name")
    private String name;

    @JsonProperty("regNo")
    private String regNo;
}
