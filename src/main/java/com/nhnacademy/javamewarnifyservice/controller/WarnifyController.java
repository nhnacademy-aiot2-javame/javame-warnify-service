package com.nhnacademy.javamewarnifyservice.controller;

import com.nhnacademy.javamewarnifyservice.dto.WarnifyRequest;
import com.nhnacademy.javamewarnifyservice.service.WarnifyService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping(value = "/warnify")
public class WarnifyController {

    /**
     * WarnifyService 호출.
     */
    private final Map<String, WarnifyService> warnifyServiceMap;

    public WarnifyController(List<WarnifyService> serviceList) {
        warnifyServiceMap = new HashMap<>();
        for (WarnifyService service : serviceList) {
            warnifyServiceMap.put(service.getType(), service);
        }
    }

    @PostMapping("/{type}")
    public ResponseEntity<String> sendAlarm(@PathVariable("type") String type, @Validated @RequestBody WarnifyRequest warnifyRequest) {
        String result = warnifyServiceMap.get(type).sendAlarm(warnifyRequest.getCompanyDomain(), warnifyRequest.getWarnInfo());
        if (result == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No service found for type: " + type);
        }
        return ResponseEntity.ok(result);
    }

    public Map<String, WarnifyService> getWarnifyServiceMap() {
        return warnifyServiceMap;
    }

}
