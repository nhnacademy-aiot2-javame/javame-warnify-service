package com.nhnacademy.javamewarnifyservice.service.impl;

import com.nhnacademy.javamewarnifyservice.adaptor.CompanyAdaptor;
import com.nhnacademy.javamewarnifyservice.dto.CompanyResponse;
import com.nhnacademy.javamewarnifyservice.service.WarnifyService;
import lombok.extern.slf4j.Slf4j;
import net.nurigo.sdk.message.model.Message;
import net.nurigo.sdk.message.request.SingleMessageSendingRequest;
import net.nurigo.sdk.message.response.SingleMessageSentResponse;
import net.nurigo.sdk.message.service.DefaultMessageService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.lang.reflect.Field;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

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
    Message message;

    @Mock
    SingleMessageSentResponse messageSentResponse;

    @Mock
    CompanyAdaptor companyAdaptor;

    @InjectMocks
    SmsService smsService;

    @BeforeEach
    void setUp() throws Exception{
        Field setApiKey = SmsService.class.getDeclaredField("apiKey");
        setApiKey.setAccessible(true);
        setApiKey.set(smsService, apiKey);

        Field setApiSecretKey = SmsService.class.getDeclaredField("apiSecretKey");
        setApiSecretKey.setAccessible(true);
        // "apiSecretKey" -> apiSecretKey 로 변환 해야함
        setApiSecretKey.set(smsService, apiSecretKey);
    }

    @Test
    void sendAlarm() {
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

        Mockito.when(companyAdaptor.getCompanyByDomain(Mockito.anyString())).thenReturn(companyResponseResponseEntity);
        Mockito.when(messageService.sendOne(Mockito.any())).thenReturn(messageSentResponse);

        String result = smsService.sendAlarm("nhnacademy","무언가가 잘못 됬어요!");
        log.error("result : {}", result);
        Assertions.assertNotNull(result);
        Assertions.assertEquals("정상 접수(이통사로 접수 예정) ",result);

    }
}