package com.nhnacademy.javamewarnifyservice.controller;

import com.nhnacademy.javamewarnifyservice.advice.exception.WarnifySendFailException;
import com.nhnacademy.javamewarnifyservice.dto.WarnifyRequest;
import com.nhnacademy.javamewarnifyservice.service.SendWarnifyService;
import com.nhnacademy.javamewarnifyservice.warnfiy.service.WarnifyService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
     * warnifyService.
     */
    private final WarnifyService warnifyService;

    /**
     * 생성자, warnifyServiceMap에는 Warnify를 구현하는 Service가 들어감
     * list에는 두레이, 문자, 이메일 서비스가 들어감.
     * @param serviceList WarnifyService를 구현하는 Service의 리스트.
     * @param warnifyService warnifyService
     */
    public WarnifySendController(
            List<SendWarnifyService> serviceList,
            WarnifyService warnifyService
    ) {
        this.warnifyService = warnifyService;

        warnifyServiceMap = new HashMap<>();
        for (SendWarnifyService service : serviceList) {
            warnifyServiceMap.put(service.getType(), service);
        }
    }

    /**
     * type에 맞는 메세지 전송
     * ex) type=sms 이면 문자메세지 발송.
     * @param type 메세지 보낼 종류
     * @param warnifyRequest 회사정보, 경고정보가 있는 DTO
     * @return 전송 성공여부
     */
    @PostMapping("/{type}")
    public ResponseEntity<String> sendAlarm(@PathVariable("type") String type, @Validated @RequestBody WarnifyRequest warnifyRequest) {
        boolean result = warnifyServiceMap.get(type).sendAlarm(warnifyRequest.getCompanyDomain(), warnifyRequest.getWarnInfo());
        warnifyService.registerWarnfiy(warnifyRequest.getCompanyDomain(), warnifyRequest.getWarnInfo());
        if (result){
            return ResponseEntity.ok("%s 전송 성공".formatted(type));
        }
        throw new WarnifySendFailException("메세지 전송 실패 하였습니다.");
    }

    /**
     * 전송할 수 있는 모든 서비스를 전송합니다.
     * @param warnifyRequest 회사정보, 경고정보가 있는 DTO
     * @return 전송 성공여부
     */
    @PostMapping("/all")
    public ResponseEntity<String> sendAlarmAll(@Validated @RequestBody WarnifyRequest warnifyRequest){
        int count =0;
        for(Map.Entry<String, SendWarnifyService> entry : warnifyServiceMap.entrySet()){
            boolean result = entry.getValue().sendAlarm(warnifyRequest.getCompanyDomain(), warnifyRequest.getWarnInfo());
            if(result){
                count++;
            }
        }
        warnifyService.registerWarnfiy(warnifyRequest.getCompanyDomain(), warnifyRequest.getWarnInfo());
        if(count==0){
            throw new WarnifySendFailException("메세지 전송 실패 하였습니다.");
        }
        return ResponseEntity.ok("%d개의 메시지 전송 성공".formatted(count));
    }

}
