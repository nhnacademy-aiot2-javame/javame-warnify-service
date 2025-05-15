package com.nhnacademy.javamewarnifyservice.controller;

import com.nhnacademy.javamewarnifyservice.warnfiy.dto.WarnifyResponse;
import com.nhnacademy.javamewarnifyservice.warnfiy.service.WarnifyService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/warnify")
@RequiredArgsConstructor
public class WarnifyController {

    /**
     * warnifyService 호출.
     */
    private final WarnifyService warnifyService;

    @GetMapping
    public ResponseEntity<WarnifyResponse> getWarnifyList() {

        return null;
    }

}
