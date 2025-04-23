package com.nhnacademy.javamewarnifyservice.controller;

import com.nhnacademy.javamewarnifyservice.service.WarnifyService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

@WebMvcTest(controllers = WarnifyController.class)
@ExtendWith(MockitoExtension.class)
class WarnifyControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    @Qualifier("emailService")
    private WarnifyService emailService;

    @Test
    void sendEmail() throws Exception{

        Mockito.when(emailService.sendAlarm(Mockito.anyString(), Mockito.anyString())).thenReturn("이메일 발송 성공");
        mockMvc
                .perform(post("/api/v1/warnify/email")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(
                                """
                                        {
                                            "companyDomain": "nhnacademy",
                                            "warnInfo": "프래픽 너무 많음!"
                                        }
                                        """
                        ))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().string("이메일 발송 성공"))
                .andDo(MockMvcResultHandlers.print());

    }

}