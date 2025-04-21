package com.nhnacademy.javamewarnifyservice.adaptor;

import org.springframework.cloud.openfeign.FeignClient;

@FeignClient(name = "MEMBER-API")
public interface MemberAdaptor {

    // Company 불러오는 API 작성 필요

}