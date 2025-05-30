package com.nhnacademy.javamewarnifyservice.controller;

import com.nhnacademy.javamewarnifyservice.adaptor.MemberApiAdaptor;
import com.nhnacademy.javamewarnifyservice.advice.exception.MemberListNotFound;
import com.nhnacademy.javamewarnifyservice.dto.MemberResponse;
import com.nhnacademy.javamewarnifyservice.dto.WarnifyRequest;
import com.nhnacademy.javamewarnifyservice.service.SendWarnifyService;
import com.nhnacademy.javamewarnifyservice.warnfiy.service.WarnifyService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

/**
 * TransService에서 경고 정보를 받고
 * 이메일, 문자, 두레이 발신 하는 컨트롤러.
 */
@Slf4j
@RestController
@RequestMapping(value = "/warnify/send")
public class WarnifySendController {

    /**
     * WarnifyService 호출.
     */
    private final Map<String, SendWarnifyService> warnifyServiceMap;

    /**
     * MemberAdaptor.
     */
    private final MemberApiAdaptor memberApiAdaptor;

    /**
     * warnifyService.
     */
    private final WarnifyService warnifyService;

    /**
     * 생성자, warnifyServiceMap에는 Warnify를 구현하는 Service가 들어감
     * list에는 두레이, 문자, 이메일 서비스가 들어감.
     * @param serviceList WarnifyService를 구현하는 Service의 리스트.
     * @param memberApiAdaptor memberApiAdaptor
     * @param warnifyService warnifyService
     */
    public WarnifySendController(
            List<SendWarnifyService> serviceList,
            MemberApiAdaptor memberApiAdaptor,
            WarnifyService warnifyService
    ) {
        this.memberApiAdaptor = memberApiAdaptor;
        this.warnifyService = warnifyService;

        warnifyServiceMap = new HashMap<>();
        for (SendWarnifyService service : serviceList) {
            warnifyServiceMap.put(service.getType(), service);
        }
    }

    /**
     * type에 맞는 메세지 전송
     * ex) type=sms 이면 문자메세지 발송.
     * SSE로 Front-End에 메세지창 발생.
     * @param type 메세지 보낼 종류
     * @param warnifyRequest 회사정보, 경고정보가 있는 DTO
     * @return 전송 성공여부
     */
    @PostMapping("/{type}")
    public ResponseEntity<String> sendAlarm(@PathVariable("type") String type, @Validated @RequestBody WarnifyRequest warnifyRequest) {
        String result = warnifyServiceMap.get(type).sendAlarm(warnifyRequest.getCompanyDomain(), warnifyRequest.getWarnInfo());
        warnifyService.registerWarnfiy(warnifyRequest.getCompanyDomain(), warnifyRequest.getWarnInfo());
        return ResponseEntity.ok(result);
    }

}
