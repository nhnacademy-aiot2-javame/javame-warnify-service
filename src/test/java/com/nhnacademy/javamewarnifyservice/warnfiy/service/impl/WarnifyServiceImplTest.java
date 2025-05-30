package com.nhnacademy.javamewarnifyservice.warnfiy.service.impl;

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
}