package com.nhnacademy.javamewarnifyservice.adaptor;

import com.nhnacademy.javamewarnifyservice.dto.CompanyResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "MEMBER-API")
public interface CompanyAdaptor {

    @GetMapping("/companies/{companyDomain}")
    ResponseEntity<CompanyResponse> getCompanyByDomain(@PathVariable String companyDomain);

}
