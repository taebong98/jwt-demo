package com.taebong.szs.domain.user.vo;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum DeductionCategory {
    INSURANCE("보험료"),
    EDUCATION("교육비"),
    DONATION("기부금"),
    MEDICAL("의료비"),
    RETIREMENT_PENSION("퇴직연금");

    private final String value;
}
