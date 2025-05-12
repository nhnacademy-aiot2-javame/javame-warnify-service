package com.nhnacademy.javamewarnifyservice.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * TransService에서 응답 정보 받는 DTO.
 */
@Getter
@RequiredArgsConstructor
public class WarnifyRequest {

    /**
     * TransService에서 넘어오는 회사명.
     */
    @NotNull(message = "companyDomain 정보는 필수 입니다.")
    private final String companyDomain;

    /**
     * TransService에서 넘어오는 경고정보.
     */
    @NotNull(message = "경고 정보는 필수 입니다.")
    private final String warnInfo;
}
