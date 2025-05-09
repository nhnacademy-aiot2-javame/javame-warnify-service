package com.nhnacademy.javamewarnifyservice.controller;

import com.nhnacademy.javamewarnifyservice.adaptor.MemberApiAdaptor;
import com.nhnacademy.javamewarnifyservice.dto.MemberResponse;
import com.nhnacademy.javamewarnifyservice.service.SendWarnifyService;
import com.nhnacademy.javamewarnifyservice.warnfiy.service.WarnifyService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import org.mockito.ArgumentCaptor;
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
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;


import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

@ActiveProfiles("dev")
@WebMvcTest(controllers = {WarnifySendController.class})
class WarnifySendControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private WarnifyService warnifyService;

    @MockitoBean
    private MemberApiAdaptor memberApiAdaptor;

    @MockitoBean("emailService")
    private SendWarnifyService sendWarnifyService;

    @MockitoBean
    SseEmitter sseEmitter;

    @Autowired
    private WarnifySendController controller;

    @Test
    @DisplayName("프론트랑 연결")
    void streamEventTest() throws Exception{
        mockMvc.perform(MockMvcRequestBuilders.get("/warnify/send/event")
                        .param("companyDomain","javame.com")
                        .param("memberNo","1")
                        .accept(MediaType.TEXT_EVENT_STREAM_VALUE))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andDo(print());
    }

    @Test
    @DisplayName("MemberResponse List 테스트")
    void getMemberResponseListTest() throws Exception{
        Method getMemberResponseList = controller.getClass().getDeclaredMethod("getMemberResponseList");
        getMemberResponseList.setAccessible(true);

        MemberResponse memberResponse = new MemberResponse(1L, "javame@live.com","javame.com","ADMIN");
        List<MemberResponse> memberResponseList = List.of(memberResponse);
        ResponseEntity<List<MemberResponse>> entity = ResponseEntity.ok(memberResponseList);

        when(memberApiAdaptor.getMemberResponseList()).thenReturn(entity);

        List<MemberResponse> getList = (List<MemberResponse>) getMemberResponseList.invoke(controller);
        Assertions.assertNotNull(getList);
        Assertions.assertAll(
                ()->Assertions.assertEquals(1L, getList.getFirst().getMemberNo()),
                ()->Assertions.assertEquals("javame.com", getList.getFirst().getCompanyDomain()),
                ()->Assertions.assertEquals("ADMIN", getList.getFirst().getRoleId())
        );

    }

    @Test
    @DisplayName("MemberResponse List 테스트 실패")
    void getMemberResponseListFailTest() throws Exception{
        Map<String, SendWarnifyService> warnifyServiceMap = new HashMap<>();
        warnifyServiceMap.put("email",sendWarnifyService);
        Field map = controller.getClass().getDeclaredField("warnifyServiceMap");
        map.setAccessible(true);
        map.set(controller, warnifyServiceMap);
        ResponseEntity<List<MemberResponse>> entity = ResponseEntity.status(HttpStatus.NOT_FOUND).build();

        when(sendWarnifyService.getType()).thenReturn("emailService");
        when(sendWarnifyService.sendAlarm(Mockito.anyString(),Mockito.anyString())).thenReturn("result");
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
                .andExpect(MockMvcResultMatchers.status().isNotFound())
                .andExpect(MockMvcResultMatchers.jsonPath("$.statusCode").value(404))
                .andExpect(MockMvcResultMatchers.jsonPath("$.error").value("Not Found"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("멤버리스트를 불러올 수 없습니다."));

        verify(warnifyService, times(1)).registerWarnfiy("javame.com", "이메일 발신!!");

    }

    @Test
    @DisplayName("onCompletion")
    void sseEmitterOnCompletion() throws Exception {
        ArgumentCaptor<Runnable> completionCaptor = ArgumentCaptor.forClass(Runnable.class);
        doNothing().when(sseEmitter).onCompletion(completionCaptor.capture());

        Field controllerMap = controller.getClass().getDeclaredField("sseEmitterMap");
        controllerMap.setAccessible(true);
        @SuppressWarnings("unchecked")
        Map<String, SseEmitter> sseEmitterMap = (Map<String, SseEmitter>) controllerMap.get(controller);
        sseEmitterMap.put("javame.com", sseEmitter);

        Method sseEmitterMapManageMethod = controller.getClass().getDeclaredMethod("sseEmitterMapManage", SseEmitter.class, String.class);
        sseEmitterMapManageMethod.setAccessible(true);
        sseEmitterMapManageMethod.invoke(controller, sseEmitter, "javame.com");

        Runnable completionCallback = completionCaptor.getValue();
        completionCallback.run();

        verify(sseEmitter, times(1)).onCompletion(Mockito.any());
        Assertions.assertFalse(sseEmitterMap.containsKey("javame.com"));
    }

    @Test
    @DisplayName("onTimeout")
    void sseEmitterOnTimeout() throws Exception {
        ArgumentCaptor<Runnable> onTimeoutCaptor = ArgumentCaptor.forClass(Runnable.class);
        doNothing().when(sseEmitter).onTimeout(onTimeoutCaptor.capture());

        Field controllerMap = controller.getClass().getDeclaredField("sseEmitterMap");
        controllerMap.setAccessible(true);
        @SuppressWarnings("unchecked")
        Map<String, SseEmitter> sseEmitterMap = (Map<String, SseEmitter>) controllerMap.get(controller);
        sseEmitterMap.put("javame.com", sseEmitter);

        Method sseEmitterMapManageMethod = controller.getClass().getDeclaredMethod("sseEmitterMapManage", SseEmitter.class, String.class);
        sseEmitterMapManageMethod.setAccessible(true);
        sseEmitterMapManageMethod.invoke(controller, sseEmitter, "javame.com");

        Runnable completionCallback = onTimeoutCaptor.getValue();
        completionCallback.run();

        verify(sseEmitter, times(1)).onTimeout(Mockito.any());
        Assertions.assertFalse(sseEmitterMap.containsKey("javame.com"));
    }

    @Test
    @DisplayName("onTimeout")
    void sseEmitterOnError() throws Exception {
        @SuppressWarnings("unchecked")
        ArgumentCaptor<Consumer<Throwable>> errorCaptor = ArgumentCaptor.forClass(Consumer.class);
        doNothing().when(sseEmitter).onError(errorCaptor.capture());

        Field sseEmitterMapField = controller.getClass().getDeclaredField("sseEmitterMap");
        sseEmitterMapField.setAccessible(true);
        @SuppressWarnings("unchecked")
        Map<String, SseEmitter> sseEmitterMap = (Map<String, SseEmitter>) sseEmitterMapField.get(controller);
        sseEmitterMap.put("javame.com", sseEmitter);

        Method sseEmitterMapManageMethod = controller.getClass().getDeclaredMethod("sseEmitterMapManage", SseEmitter.class, String.class);
        sseEmitterMapManageMethod.setAccessible(true);
        sseEmitterMapManageMethod.invoke(controller, sseEmitter, "javame.com");

        Consumer<Throwable> errorCallback = errorCaptor.getValue();
        errorCallback.accept(new RuntimeException("Test SSE 에러"));

        verify(sseEmitter, times(1)).onError(Mockito.any());
        Assertions.assertFalse(sseEmitterMap.containsKey("javame.com"));
    }

}
