package com.nhnacademy.javamewarnifyservice.controller;

import com.nhnacademy.javamewarnifyservice.warnfiy.dto.WarnifyResponse;
import com.nhnacademy.javamewarnifyservice.warnfiy.service.WarnifyService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
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

    @PostMapping
    public ResponseEntity<WarnifyResponse> registerWarnify() {
        return null;
    }

}
