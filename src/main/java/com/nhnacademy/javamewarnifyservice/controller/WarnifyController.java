package com.nhnacademy.javamewarnifyservice.controller;

import com.nhnacademy.javamewarnifyservice.service.impl.WarnifyServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/api/v1/warnify")
@RequiredArgsConstructor
public class WarnifyController {

    /**
     *  WarnifyService 호출.
     */
    private final WarnifyServiceImpl warnifyService;

    @PostMapping
    public ResponseEntity<String> sendEmail(String companyDomain, String warnInfo) {
        String result = warnifyService.sendEmail(companyDomain, warnInfo);
        return ResponseEntity.ok(result);
    }

}
