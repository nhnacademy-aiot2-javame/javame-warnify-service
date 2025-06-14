package com.nhnacademy.javamewarnifyservice.adaptor;

import com.nhnacademy.javamewarnifyservice.dto.CompanyResponse;
import com.nhnacademy.javamewarnifyservice.dto.MemberResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@FeignClient(name = "MEMBER-API")
public interface MemberApiAdaptor {

    @GetMapping("/companies/{companyDomain}")
    ResponseEntity<CompanyResponse> getCompanyByDomain(@PathVariable String companyDomain);

    @GetMapping("/members")
    ResponseEntity<List<MemberResponse>> getMemberResponseList();

}
