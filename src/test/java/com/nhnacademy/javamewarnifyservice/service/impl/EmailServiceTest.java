package com.nhnacademy.javamewarnifyservice.service.impl;

import com.nhnacademy.javamewarnifyservice.adaptor.MemberApiAdaptor;
import com.nhnacademy.javamewarnifyservice.dto.CompanyResponse;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;

import javax.mail.Transport;
import javax.mail.internet.MimeMessage;
import java.lang.reflect.Field;
import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;

@Slf4j
@ActiveProfiles("test")
@ExtendWith(MockitoExtension.class)
class EmailServiceTest {

    @Mock
    MemberApiAdaptor memberApiAdaptor;

    @InjectMocks
    EmailService emailService;

    Field sendPassword;
    @BeforeEach
    void setUp(){
        try {
            Field sendEmail = EmailService.class.getDeclaredField("senderEmail");
            sendEmail.setAccessible(true);
            sendEmail.set(emailService, "nhnacademy@naver.com");

            sendPassword = EmailService.class.getDeclaredField("senderPassword");
            sendPassword.setAccessible(true);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }

    @Test
    @DisplayName("이메일 전송 성공")
    void sendEmailSuccessTest() throws Exception{
        sendPassword.set(emailService, "password");
        CompanyResponse companyResponse = new CompanyResponse(
                "nhnacademy",
                "nhn",
                "fhqht303@naver.com",
                "010-1111-2222",
                "김해시 내외동로",
                LocalDateTime.of(2020,12,5,5,30),
                true
        );
        ResponseEntity<CompanyResponse> companyResponseResponseEntity
                = new ResponseEntity<>(companyResponse, HttpStatus.OK);
        Mockito.when(memberApiAdaptor.getCompanyByDomain(Mockito.anyString())).thenReturn(companyResponseResponseEntity);

        // static 메서드를 가짜로 만들어줌, EmailService에서 Transport.send라는 static함수가 있는데 가짜로 만들어줌, send가 발송역활 이지만 발송 안되게 해줌.
        try(MockedStatic<Transport> mockedStatic = Mockito.mockStatic((Transport.class))){

            boolean result = emailService.sendAlarm("nhnacademy","경고!");

            Assertions.assertTrue(result);

            mockedStatic.verify(() -> Transport.send(any(MimeMessage.class)), times(1));
        }
        
    }

    @Test
    @DisplayName("이메일 전송 실패")
    void sendEmailFailTest() throws Exception{
        sendPassword.set(emailService, "wrongPassword");
        CompanyResponse companyResponse = new CompanyResponse(
                "nhnacademy",
                "nhn",
                "fhqht303@naver.com",
                "010-1111-2222",
                "김해시 내외동로",
                LocalDateTime.of(2020,12,5,5,30),
                true
        );
        ResponseEntity<CompanyResponse> companyResponseResponseEntity = new ResponseEntity<>(companyResponse, HttpStatus.OK);
        Mockito.when(memberApiAdaptor.getCompanyByDomain(Mockito.anyString())).thenReturn(companyResponseResponseEntity);

        boolean result = emailService.sendAlarm("nhnacademy", "경고 알림!!");
        log.info("result : {}", result);

        Assertions.assertFalse(result);
    }

}
