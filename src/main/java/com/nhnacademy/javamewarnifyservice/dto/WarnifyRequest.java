package com.nhnacademy.javamewarnifyservice.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class WarnifyRequest {

    /**
     * TransService에서 넘어오는 회사명.
     */
    private final String companyDomain;

    /**
     * TransService에서 넘어오는 경고정보.
     */
    private final String warnInfo;
}
