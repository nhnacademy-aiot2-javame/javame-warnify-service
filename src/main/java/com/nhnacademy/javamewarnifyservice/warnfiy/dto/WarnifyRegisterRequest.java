package com.nhnacademy.javamewarnifyservice.warnfiy.dto;

import lombok.Value;

@Value
public class WarnifyRegisterRequest {

    /**
     * 등록할 회사 도메인.
     */
    private final String companyDomain;

    /**
     * 등록할 경고 정보.
     */
    private final String warnInfo;

}
