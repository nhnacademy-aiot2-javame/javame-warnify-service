package com.nhnacademy.javamewarnifyservice.service;

import com.nhnacademy.javamewarnifyservice.adaptor.MemberApiAdaptor;
import com.nhnacademy.javamewarnifyservice.advice.exception.CompanyNotFoundException;
import com.nhnacademy.javamewarnifyservice.dto.CompanyResponse;
import org.springframework.http.ResponseEntity;

public interface SendWarnifyService {

    String sendAlarm(String companyDomain, String warnInfo);

    String getType();

    default CompanyResponse getCompanyResponse(String companyDomain, MemberApiAdaptor memberApiAdaptor) {
        ResponseEntity<CompanyResponse> companyResponseResponseEntity = memberApiAdaptor.getCompanyByDomain(companyDomain);

        if (!companyResponseResponseEntity.getStatusCode().is2xxSuccessful()) {
            throw new CompanyNotFoundException("회사를 찾기에 실패했습니다.");
        }

        return companyResponseResponseEntity.getBody();
    }

}
