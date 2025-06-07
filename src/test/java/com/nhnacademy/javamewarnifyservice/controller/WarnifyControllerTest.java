package com.nhnacademy.javamewarnifyservice.controller;

import com.nhnacademy.javamewarnifyservice.warnfiy.dto.WarnifyResponse;
import com.nhnacademy.javamewarnifyservice.warnfiy.service.WarnifyService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.content;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.jsonPath;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(WarnifyController.class)
@ActiveProfiles("test")
class WarnifyControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private WarnifyService warnifyService;

    @Test
    @DisplayName("경고 목록 페이지로 조회")
    void getWarnifyListSuccess() throws Exception {
        // given
        List<WarnifyResponse> responses = List.of(
                new WarnifyResponse(1L, "javame.com","100번 포트 이상 발생", LocalDateTime.of(2025,1,2,5,12), "1"),
                new WarnifyResponse(2L, "javame.com","200번 포트 이상 발생", LocalDateTime.of(2025,2,4,2,22), "0")
        );
        Page<WarnifyResponse> warnfiyPage = new PageImpl<>(responses);

        Mockito.when(warnifyService.getWarnifyList(Mockito.anyString(), Mockito.any(Pageable.class)))
                .thenReturn(warnfiyPage);

        // when & then
        mockMvc.perform(get("/warnify/list/javame.com")
                        .param("page", "0"))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.content.length()").value(2))
                .andExpect(MockMvcResultMatchers.jsonPath("$.content[0].warnInfo").value("100번 포트 이상 발생"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.content[1].warnInfo").value("200번 포트 이상 발생"))
                .andDo(print());
    }

    @Test
    @DisplayName("경고 해결 처리 성공")
    void getWarnifyList() throws Exception {
        // given
        Mockito.when(warnifyService.resolveWarn(Mockito.anyLong(), Mockito.anyString())).thenReturn("경고에 대한 문제해결이 되었습니다.");

        // when & then
        mockMvc.perform(put("/warnify/resolve/1")
                        .param("resolve", "true"))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("result").value("경고에 대한 문제해결이 되었습니다."))
                .andDo(print());
    }

}