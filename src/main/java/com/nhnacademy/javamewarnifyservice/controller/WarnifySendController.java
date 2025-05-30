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
     * SSE에서 사용될 MAP.
     */
    private final Map<String, SseEmitter> sseEmitterMap;

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
        sseEmitterMap = new ConcurrentHashMap<>();
        this.memberApiAdaptor = memberApiAdaptor;
        this.warnifyService = warnifyService;

        warnifyServiceMap = new HashMap<>();
        for (SendWarnifyService service : serviceList) {
            warnifyServiceMap.put(service.getType(), service);
        }
    }

    /**
     * Front-End와 SSE 연결.
     * POST에서 메세지창 뜨게 해주는 역할.
     * @param companyDomain 회사 도메인
     * @param memberNo 멤버 넘버
     * @return SseEmitter
     */
    @GetMapping(value = "/event", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter streamEvent(@RequestParam String companyDomain, @RequestParam String memberNo) {
        String clientId = companyDomain + memberNo;
        SseEmitter emitter = new SseEmitter(60 * 60 * 1000L);
        sseEmitterMapManage(emitter, clientId);
        sseEmitterMap.put(clientId, emitter);
        return emitter;
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
        List<MemberResponse> memberResponseList = getMemberResponseList();
        for (MemberResponse memberResponse : memberResponseList) {
            String clientId = warnifyRequest.getCompanyDomain() + memberResponse.getMemberNo();
            SseEmitter emitter = sseEmitterMap.get(clientId);
            if (Objects.nonNull(emitter)) {
                try {
                    emitter.send(SseEmitter.event()
                            .name("WARN")
                            .data(warnifyRequest.getWarnInfo()));
                } catch (IOException e) {
                    log.error("SSE 전송 에러", e);
                    sseEmitterMap.remove(warnifyRequest.getCompanyDomain());
                }
            }
        }

        return ResponseEntity.ok(result);
    }

    /**
     * SseEmitter 연결종료, 타임아웃, 에러발생 시 Map에 있는 SseEmitter 삭제 하여 관리.
     * @param emitter 해당 SseEmitter
     * @param clientId 관리 받을 회사 도메인
     */
    private void sseEmitterMapManage(SseEmitter emitter, String clientId) {
        emitter.onCompletion(() -> {
            log.info("SSE 연결 종료 : {}", clientId);
            sseEmitterMap.remove(clientId);
        });

        emitter.onTimeout(() -> {
            log.warn("SSE 타임아웃 : {}", clientId);
            sseEmitterMap.remove(clientId);
        });

        emitter.onError(e -> {
            log.error("SSE 에러 : {}", clientId);
            log.error("에러 내용 : {}", e.toString());
            sseEmitterMap.remove(clientId);
        });
    }

    /**
     * SSE연결할 멤버 리스트 불러오기.
     * @return MemberResponse 반환.
     */
    private List<MemberResponse> getMemberResponseList() {
        ResponseEntity<List<MemberResponse>> responseEntity = memberApiAdaptor.getMemberResponseList();
        if (!responseEntity.getStatusCode().is2xxSuccessful()) {
            throw new MemberListNotFound("멤버리스트를 불러올 수 없습니다.");
        }
        return responseEntity.getBody();
    }

}
