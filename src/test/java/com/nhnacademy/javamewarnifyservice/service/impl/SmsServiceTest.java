package com.nhnacademy.javamewarnifyservice.service.impl;

import com.nhnacademy.javamewarnifyservice.adaptor.MemberApiAdaptor;
import com.nhnacademy.javamewarnifyservice.advice.exception.CompanyNotFoundException;
import com.nhnacademy.javamewarnifyservice.dto.CompanyResponse;
import lombok.extern.slf4j.Slf4j;
import net.nurigo.sdk.message.exception.NurigoUnknownException;
import net.nurigo.sdk.message.response.SingleMessageSentResponse;
import net.nurigo.sdk.message.service.DefaultMessageService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;

import java.lang.reflect.Field;
import java.time.LocalDateTime;

@Slf4j
@SpringBootTest
@ActiveProfiles("dev")
class SmsServiceTest {

    @Value("${security.sms.apiKey}")
    private String apiKey;

    @Value("${security.sms.apiSecretKey}")
    private String apiSecretKey;

    @Mock
    DefaultMessageService messageService;

    @Mock
    SingleMessageSentResponse messageSentResponse;

    @Mock
    MemberApiAdaptor memberApiAdaptor;

    @Mock
    SingleMessageSentResponse response;

    @InjectMocks
    SmsService smsService;

    @BeforeEach
    void setUp() throws Exception{
        Field setApiKey = SmsService.class.getDeclaredField("apiKey");
        setApiKey.setAccessible(true);
        setApiKey.set(smsService, apiKey);
    }

    @Test
    @DisplayName("sms 발신 성공")
    void sendAlarm() throws Exception{
        Field setApiSecretKey = SmsService.class.getDeclaredField("apiSecretKey");
        setApiSecretKey.setAccessible(true);
        // "apiSecretKey" -> apiSecretKey 로 변환 해야함
        setApiSecretKey.set(smsService, apiSecretKey);

        CompanyResponse companyResponse = new CompanyResponse(
                "nhnacademy",
                "nhn",
                "abcdefghijk@naver.com",
                "010-0000-000012",
                "김해시 내외동로",
                LocalDateTime.of(2020,12,5,5,30),
                true
        );
        ResponseEntity<CompanyResponse> companyResponseResponseEntity
                = new ResponseEntity<>(companyResponse, HttpStatus.OK);

        Mockito.when(memberApiAdaptor.getCompanyByDomain(Mockito.anyString())).thenReturn(companyResponseResponseEntity);
        Mockito.when(messageService.sendOne(Mockito.any())).thenReturn(messageSentResponse);
        Mockito.when(response.getStatusMessage()).thenReturn("정상 접수(이통사로 접수 예정) ");

        String result = smsService.sendAlarm("nhnacademy","무언가가 잘못 됬어요!");
        log.error("result : {}", result);
        Assertions.assertNotNull(result);
        Assertions.assertEquals("정상 접수(이통사로 접수 예정) ",result);

    }

    @Test
    @DisplayName("sms 발신 실패")
    void sendSmsFail() throws Exception{
        Field setApiSecretKey = SmsService.class.getDeclaredField("apiSecretKey");
        setApiSecretKey.setAccessible(true);
        // "apiSecretKey" -> apiSecretKey 로 변환 해야함
        setApiSecretKey.set(smsService, "mistake");

        CompanyResponse companyResponse = new CompanyResponse(
                "nhnacademy",
                "nhn",
                "abcdefghijk@naver.com",
                "010-0000-000012",
                "김해시 내외동로",
                LocalDateTime.of(2020,12,5,5,30),
                true
        );

        ResponseEntity<CompanyResponse> companyResponseResponseEntity
                = new ResponseEntity<>(companyResponse, HttpStatus.OK);

        Mockito.when(memberApiAdaptor.getCompanyByDomain(Mockito.anyString())).thenReturn(companyResponseResponseEntity);
        Mockito.when(messageService.sendOne(Mockito.any())).thenReturn(messageSentResponse);

        Assertions.assertThrows(
                NurigoUnknownException.class, ()->smsService.sendAlarm("nhnacademy","무언가가 잘못 됬어요!")
        );
    }

    @Test
    @DisplayName("회사 찾기 실패")
    void getCompanyResponseFailTest() throws Exception{
        Field setApiSecretKey = SmsService.class.getDeclaredField("apiSecretKey");
        setApiSecretKey.setAccessible(true);
        // "apiSecretKey" -> apiSecretKey 로 변환 해야함
        setApiSecretKey.set(smsService, apiSecretKey);
        CompanyResponse companyResponse = new CompanyResponse(
                "nhnacademy",
                "nhn",
                "abcdefghijk@naver.com",
                "010-0000-000012",
                "김해시 내외동로",
                LocalDateTime.of(2020,12,5,5,30),
                true
        );
        ResponseEntity<CompanyResponse> companyResponseResponseEntity
                = new ResponseEntity<>(companyResponse, HttpStatus.NOT_FOUND);

        Mockito.when(memberApiAdaptor.getCompanyByDomain(Mockito.anyString())).thenReturn(companyResponseResponseEntity);

        Assertions.assertThrows(CompanyNotFoundException.class, ()->{
            smsService.sendAlarm("nhnacademy","무언가가 잘못 됬어요!");
        });
    }
}