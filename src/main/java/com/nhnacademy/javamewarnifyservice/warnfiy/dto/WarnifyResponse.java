package com.nhnacademy.javamewarnifyservice.warnfiy.dto;

import lombok.Value;

import java.time.LocalDateTime;

/**
 * Warnify 응답 DTO.
 */
@Value
public class WarnifyResponse {

    /**
     * 경고 번호.
     */
    private final Long warnifyId;

    /**
     * 등록할 회사 도메인.
     */
    private final String companyDomain;

    /**
     * 등록할 경고 정보.
     */
    private final String warnInfo;

    /**
     * 경고 발생일자.
     */
    private final LocalDateTime warnDate;

    /**
     * 경고 해결 여부.
     */
    private final String resolve;

}
