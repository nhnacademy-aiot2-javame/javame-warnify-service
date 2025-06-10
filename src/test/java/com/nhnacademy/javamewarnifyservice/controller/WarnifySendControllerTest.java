package com.nhnacademy.javamewarnifyservice.controller;

import com.nhnacademy.javamewarnifyservice.adaptor.MemberApiAdaptor;
import com.nhnacademy.javamewarnifyservice.dto.MemberResponse;
import com.nhnacademy.javamewarnifyservice.service.SendWarnifyService;
import com.nhnacademy.javamewarnifyservice.warnfiy.service.WarnifyService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;


import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = {WarnifySendController.class})
@ActiveProfiles("test")
class WarnifySendControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private WarnifyService warnifyService;

    @MockitoBean
    private MemberApiAdaptor memberApiAdaptor;

    @MockitoBean("emailService")
    private SendWarnifyService sendWarnifyService;

    @Autowired
    private WarnifySendController controller;

    @Test
    @DisplayName("MemberResponse List")
    void getMemberResponseListFailTest() throws Exception{
        Map<String, SendWarnifyService> warnifyServiceMap = new HashMap<>();
        warnifyServiceMap.put("email",sendWarnifyService);
        Field map = controller.getClass().getDeclaredField("warnifyServiceMap");
        map.setAccessible(true);
        map.set(controller, warnifyServiceMap);
        ResponseEntity<List<MemberResponse>> entity = ResponseEntity.status(HttpStatus.NOT_FOUND).build();

        when(sendWarnifyService.getType()).thenReturn("emailService");
        when(sendWarnifyService.sendAlarm(Mockito.anyString(),Mockito.anyString())).thenReturn(true);
        when(memberApiAdaptor.getMemberResponseList()).thenReturn(entity);

        mockMvc.
                perform(MockMvcRequestBuilders.post("/warnify/send/email")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(
"""
{
  "companyDomain": "javame.com",
  "warnInfo": "이메일 발신!!"
}
"""))
                .andExpect(status().isOk());

        verify(warnifyService, times(1)).registerWarnfiy("javame.com", "이메일 발신!!");

    }

    @Test
    @DisplayName("모든 메신저 발송")
    void allMessageSend() throws Exception {
        when(sendWarnifyService.sendAlarm(anyString(),anyString())).thenReturn(true);
        mockMvc
                .perform(MockMvcRequestBuilders.post("/warnify/send/all")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(
                                """
                                {
                                  "companyDomain": "javame.com",
                                  "warnInfo": "이메일 발신!!"
                                }
                                """
                        )).andExpect(status().isOk());
        verify(warnifyService, times(1)).registerWarnfiy("javame.com", "이메일 발신!!");

    }

    @Test
    @DisplayName("메세지 전송 실패")
    void sendMessageFail() throws Exception{
        Map<String, SendWarnifyService> warnifyServiceMap = new HashMap<>();
        warnifyServiceMap.put("email",sendWarnifyService);
        Field map = controller.getClass().getDeclaredField("warnifyServiceMap");
        map.setAccessible(true);
        map.set(controller, warnifyServiceMap);

        Mockito.when(sendWarnifyService.sendAlarm(Mockito.anyString(), Mockito.anyString())).thenReturn(false);

        mockMvc.
                perform(MockMvcRequestBuilders.post("/warnify/send/email")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(
                                """
                                {
                                  "companyDomain": "javame.com",
                                  "warnInfo": "이메일 발신!!"
                                }
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.statusCode").value("400"))
                .andExpect(jsonPath("$.error").value("Not Found"))
                .andExpect(jsonPath("$.message").value("메세지 전송 실패 하였습니다."));

    }

    @Test
    @DisplayName("전체 전송 과정중 메세지 전체 실패")
    void sendMessageAllFail() throws Exception{
        when(sendWarnifyService.sendAlarm(anyString(),anyString())).thenReturn(false);
        mockMvc
                .perform(MockMvcRequestBuilders.post("/warnify/send/all")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(
                                """
                                {
                                  "companyDomain": "javame.com",
                                  "warnInfo": "이메일 발신!!"
                                }
                                """
                        ))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.statusCode").value("400"))
                .andExpect(jsonPath("$.error").value("Not Found"))
                .andExpect(jsonPath("$.message").value("메세지 전송 실패 하였습니다."));
    }

}
