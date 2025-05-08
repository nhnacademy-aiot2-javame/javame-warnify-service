package com.nhnacademy.javamewarnifyservice.controller;

import com.nhnacademy.javamewarnifyservice.adaptor.MemberApiAdaptor;
import com.nhnacademy.javamewarnifyservice.dto.MemberResponse;
import com.nhnacademy.javamewarnifyservice.dto.WarnifyRequest;
import com.nhnacademy.javamewarnifyservice.service.SendWarnifyService;
import com.nhnacademy.javamewarnifyservice.warnfiy.service.WarnifyService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;


import java.util.List;

import static org.mockito.Mockito.*;

@WebMvcTest(controllers = SendWarnifyService.class)
class WarnifySendControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean(name = "emailService")
    private SendWarnifyService emailService;

    @MockitoBean(name = "smsService")
    private SendWarnifyService smsService;

    @MockitoBean(name = "doorayService")
    private SendWarnifyService doorayService;

    @MockitoBean
    private WarnifyService warnifyService;

    @MockitoBean
    private MemberApiAdaptor memberApiAdaptor;

    private WarnifySendController controller;

    WarnifyRequest request;

    @BeforeEach
    void setUp() {
        when(emailService.getType()).thenReturn("email");
        when(smsService.getType()).thenReturn("sms");
        when(doorayService.getType()).thenReturn("dooray");

        controller = new WarnifySendController(List.of(emailService, smsService, doorayService), memberApiAdaptor, warnifyService);
        request = new WarnifyRequest("nhnacademy", "경고 정보");
    }

    @Test
    void sendEmailTest() {
        MemberResponse memberResponse = new MemberResponse();
        List<MemberResponse> memberResponseList = List.of(memberResponse);
        ResponseEntity<List<MemberResponse>> responseEntity = new ResponseEntity<>(memberResponseList, HttpStatus.OK);

        when(memberApiAdaptor.getMemberResponseList()).thenReturn(responseEntity);
        when(emailService.sendAlarm(anyString(), anyString())).thenReturn("Email 알람 전송됨");

        ResponseEntity<String> response = controller.sendAlarm("email", request);

        Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
        Assertions.assertEquals("Email 알람 전송됨", response.getBody());

        verify(emailService).sendAlarm("nhnacademy", "경고 정보");
    }

    @Test
    void sendSmsTest() {
        MemberResponse memberResponse = new MemberResponse();
        List<MemberResponse> memberResponseList = List.of(memberResponse);
        ResponseEntity<List<MemberResponse>> responseEntity = new ResponseEntity<>(memberResponseList, HttpStatus.OK);

        when(memberApiAdaptor.getMemberResponseList()).thenReturn(responseEntity);
        when(smsService.sendAlarm(anyString(), anyString())).thenReturn("SMS 성공");

        ResponseEntity<String> response = controller.sendAlarm("sms", request);

        Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
        Assertions.assertEquals("SMS 성공", response.getBody());

        verify(smsService).sendAlarm("nhnacademy", "경고 정보");
    }

    @Test
    void sendDoorayTest() {
        MemberResponse memberResponse = new MemberResponse();
        List<MemberResponse> memberResponseList = List.of(memberResponse);
        ResponseEntity<List<MemberResponse>> responseEntity = new ResponseEntity<>(memberResponseList, HttpStatus.OK);

        when(memberApiAdaptor.getMemberResponseList()).thenReturn(responseEntity);
        when(smsService.sendAlarm(anyString(), anyString())).thenReturn("두레이 전송 성공");

        ResponseEntity<String> response = controller.sendAlarm("sms", request);

        Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
        Assertions.assertEquals("두레이 전송 성공", response.getBody());

        verify(smsService).sendAlarm("nhnacademy", "경고 정보");
    }

}
