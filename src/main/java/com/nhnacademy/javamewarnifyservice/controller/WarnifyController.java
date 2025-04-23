package com.nhnacademy.javamewarnifyservice.controller;

import com.nhnacademy.javamewarnifyservice.dto.WarnifyRequest;
import com.nhnacademy.javamewarnifyservice.service.WarnifyService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping(value = "/api/v1/warnify")
public class WarnifyController {

    /**
     *  WarnifyService 호출.
     */
    private final WarnifyService emailService;

    public WarnifyController(@Qualifier("emailService")WarnifyService emailService) {
        this.emailService = emailService;
    }

    @PostMapping("/email")
    public ResponseEntity<String> sendEmail(@RequestBody WarnifyRequest warnifyRequest) {
        String result = emailService.sendAlarm(warnifyRequest.getCompanyDomain(), warnifyRequest.getWarnInfo());
        return ResponseEntity.ok(result);
    }

}
