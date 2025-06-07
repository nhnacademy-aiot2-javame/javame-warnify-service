package com.nhnacademy.javamewarnifyservice.warnfiy.service.impl;

import com.nhnacademy.javamewarnifyservice.advice.exception.WarnifyNotFoundException;
import com.nhnacademy.javamewarnifyservice.config.KSTTime;
import com.nhnacademy.javamewarnifyservice.warnfiy.domain.Warnify;
import com.nhnacademy.javamewarnifyservice.warnfiy.dto.WarnifyResponse;
import com.nhnacademy.javamewarnifyservice.warnfiy.repository.WarnifyRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.lang.reflect.Field;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;


@ExtendWith(MockitoExtension.class)
class WarnifyServiceImplTest {

    @Mock
    WarnifyRepository warnifyRepository;

    @InjectMocks
    WarnifyServiceImpl warnifyService;

    Warnify warnify;
    @BeforeEach
    void setUp() {
        warnify = new Warnify(
                "경고 입니다!",
                KSTTime.kstTimeNow(),
                "javame.com"
        );
    }

    @Test
    @DisplayName("경고 내역 저장")
    void registerWarnfiy() throws Exception{
        Mockito.when(warnifyRepository.save(Mockito.any())).thenReturn(warnify);
        Field field = warnify.getClass().getDeclaredField("warnifyId");
        field.setAccessible(true);
        field.set(warnify, 1L);

        WarnifyResponse warnifyResponse = warnifyService.registerWarnfiy("javame.com", "경고 입니다!");

        Assertions.assertNotNull(warnifyResponse);
        Assertions.assertAll(
                ()->Assertions.assertEquals("javame.com", warnifyResponse.getCompanyDomain()),
                ()->Assertions.assertEquals("경고 입니다!", warnifyResponse.getWarnInfo()),
                ()->Assertions.assertNotNull(warnifyResponse.getWarnDate())
        );
    }

    @Test
    @DisplayName("경고 목록 가지고 오기")
    void getWarnifyList() {
//        List<Warnify> warnifyList = List.of(warnify);
//        Page<Warnify> warnifyList = new PageImpl<>(warnify);
        Mockito.when(warnifyRepository.findByCompanyDomain("javame.com", Pageable.ofSize(1))).thenReturn(new PageImpl<>(List.of(warnify)));

        Page<WarnifyResponse> warnifyResponseList = warnifyService.getWarnifyList("javame.com", Pageable.ofSize(1));

        Assertions.assertNotNull(warnifyResponseList);

        Assertions.assertAll(
                ()->Assertions.assertEquals("javame.com", warnifyResponseList.getContent().getFirst().getCompanyDomain()),
                ()->Assertions.assertEquals("경고 입니다!", warnifyResponseList.getContent().getFirst().getWarnInfo()),
                ()->Assertions.assertNotNull(warnifyResponseList.getContent().getFirst().getWarnDate())
        );
    }

    @Test
    @DisplayName("resolve 값이 null이 왔을때")
    void resolveWarnIsNull(){
        String resolve = null;

        Assertions.assertThrows(
                IllegalArgumentException.class,()-> warnifyService.resolveWarn(1L, resolve)
        );
    }

    @Test
    @DisplayName("resolve 값이 true, false가 아닐때")
    void resolveWarnNotBoolean(){
        String resolve = "TTrue";

        Assertions.assertThrows(
                IllegalArgumentException.class,()-> warnifyService.resolveWarn(1L, resolve)
        );
    }

    @Test
    @DisplayName("경고id를 찾지 못했을때")
    void resolveWarnNotFoundWarnify(){
        String resolve = "true";

        Mockito.when(warnifyRepository.findByWarnifyId(Mockito.anyLong())).thenReturn(Optional.empty());

        Assertions.assertThrows(
                WarnifyNotFoundException.class,()-> warnifyService.resolveWarn(1L, resolve)
        );
    }

    @Test
    @DisplayName("resolve가 true일때")
    void resolveWarnTrue(){
        String resolve = "true";

        Mockito.when(warnifyRepository.findByWarnifyId(Mockito.anyLong())).thenReturn(Optional.of(warnify));

        String result = warnifyService.resolveWarn(1L, resolve);

        Assertions.assertEquals("경고에 대한 문제해결이 되었습니다.",result);
    }

    @Test
    @DisplayName("resolve가 false일때")
    void resolveWarnFalse(){
        String resolve = "false";

        Mockito.when(warnifyRepository.findByWarnifyId(Mockito.anyLong())).thenReturn(Optional.of(warnify));

        String result = warnifyService.resolveWarn(1L, resolve);

        Assertions.assertEquals("경고에 대한 문제해결이 안됬습니다.",result);
    }

}