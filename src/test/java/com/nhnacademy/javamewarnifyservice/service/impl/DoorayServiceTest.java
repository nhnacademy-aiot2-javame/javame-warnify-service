package com.nhnacademy.javamewarnifyservice.service.impl;

import com.nhnacademy.javamewarnifyservice.adaptor.MemberApiAdaptor;
import com.nhnacademy.javamewarnifyservice.config.KSTTime;
import com.nhnacademy.javamewarnifyservice.dto.CompanyResponse;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.client.AutoConfigureMockRestServiceServer;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.test.web.client.match.MockRestRequestMatchers;
import org.springframework.test.web.client.response.MockRestResponseCreators;

import java.lang.reflect.Method;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;

@Slf4j
@SpringBootTest
@AutoConfigureMockRestServiceServer
@ActiveProfiles("test")
class DoorayServiceTest {

    @MockitoBean
    MemberApiAdaptor memberApiAdaptor;

    @Autowired
    DoorayService doorayService;

    @Autowired
    MockRestServiceServer serviceServer;

    @Test
    void sendAlarm() {
        CompanyResponse companyResponse = new CompanyResponse(
                "nhnacademy",
                "엔에이엔",
                "nhnacademy@naver.com",
                "010-1111-1111",
                "김해시 어딘가",
                KSTTime.kstTimeNow(),
                true
        );

        Mockito.when(memberApiAdaptor.getCompanyByDomain(Mockito.anyString())).thenReturn(new ResponseEntity<>(companyResponse,HttpStatus.OK));

        // 멤버 조회 테스트
        String email = "nhnacademy@naver.com";
        String doorayURL = "https://api.dooray.com/common/v1/members?externalEmailAddresses=%s".formatted(email);

        String result =
                """
                        {
                            "header": {
                                "resultCode": 0,
                                "resultMessage": "",
                                "isSuccessful": true
                            },
                            "result": [
                                {
                                    "id": "1",
                                    "userCode": "user1",
                                    "name": "john",
                                    "externalEmailAddress": "user1@mail.com"
                                }
                            ],
                            "totalCount": 1
                        }
                """;

        serviceServer
                .expect(requestTo(doorayURL))
                .andExpect(MockRestRequestMatchers.method(HttpMethod.GET))
                .andRespond(MockRestResponseCreators.withSuccess(result, MediaType.APPLICATION_JSON));

        // 메세지 전송 테스트
        String doorayURL1 = "https://api.dooray.com/messenger/v1/channels/direct-send";
        String result1 = """
                {
                    "header": {
                        "resultCode": 0,
                        "resultMessage": "",
                        "isSuccessful": true
                    },
                    "result": null
                }
                """;

        serviceServer
                .expect(requestTo(doorayURL1))
                .andExpect(MockRestRequestMatchers.method(HttpMethod.POST))
                .andRespond(MockRestResponseCreators.withSuccess(result1, MediaType.APPLICATION_JSON));

        boolean sendResult = doorayService.sendAlarm("nhnacademy", "warninfo");

        assertEquals(true, sendResult);
    }

    @Test
    void getDoorayMemberIdTest() throws Exception{
        String email = "fhqht303@naver.com";
        String doorayURL = "https://api.dooray.com/common/v1/members?externalEmailAddresses=%s".formatted(email);

        String result =
                """
                        {
                            "header": {
                                "resultCode": 0,
                                "resultMessage": "",
                                "isSuccessful": true
                            },
                            "result": [
                                {
                                    "id": "1",
                                    "userCode": "user1",
                                    "name": "john",
                                    "externalEmailAddress": "user1@mail.com"
                                }
                            ],
                            "totalCount": 1
                        }
                """;

        serviceServer
                .expect(requestTo(doorayURL))
                .andExpect(MockRestRequestMatchers.method(HttpMethod.GET))
                .andRespond(MockRestResponseCreators.withSuccess(result, MediaType.APPLICATION_JSON));

        Method methoda = doorayService.getClass().getDeclaredMethod("getDoorayMemberId", String.class);
        methoda.setAccessible(true);
        String id = (String) methoda.invoke(doorayService, "fhqht303@naver.com");

        log.info("id : {}",id);

        assertNotNull(id);
        assertEquals("1",id);
    }

    @Test
    void sendIndividualMsgTest() throws Exception{
        String doorayURL = "https://api.dooray.com/messenger/v1/channels/direct-send";
        String result = """
                {
                    "header": {
                        "resultCode": 0,
                        "resultMessage": "",
                        "isSuccessful": true
                    },
                    "result": null
                }
                """;

        serviceServer
                .expect(requestTo(doorayURL))
                .andExpect(MockRestRequestMatchers.method(HttpMethod.POST))
                .andRespond(MockRestResponseCreators.withSuccess(result, MediaType.APPLICATION_JSON));

        Method method = doorayService.getClass().getDeclaredMethod("sendIndividualMsg", String.class, String.class);
        method.setAccessible(true);

        String id = (String) method.invoke(doorayService, "3884802321735904763", "Hello World");

        log.info("id : {}", id);
        assertNotNull(id);
        assertEquals("true", id);
    }

    @Test
    @DisplayName("getDoorayMemberId - ObjectMapper 에러")
    void getDoorayMemberIdErrorTest() throws Exception{
        String email = "fhqht303@naver.com";
        String doorayURL = "https://api.dooray.com/common/v1/members?externalEmailAddresses=%s".formatted(email);

        String result =
                """
                        {
                            "header": {{
                                "resultCode": 0,
                                "resultMessage": "",
                                "isSuccessful": true
                            },
                            "result": [
                                {
                                    "id": "1",
                                    "userCode": "user1",
                                    "name": "john",
                                    "externalEmailAddress": "user1@mail.com"
                                }
                            ],
                            "totalCount": 1
                        }
                """;

        serviceServer
                .expect(requestTo(doorayURL))
                .andExpect(MockRestRequestMatchers.method(HttpMethod.GET))
                .andRespond(MockRestResponseCreators.withSuccess(result, MediaType.APPLICATION_JSON));

        Method methoda = doorayService.getClass().getDeclaredMethod("getDoorayMemberId", String.class);
        methoda.setAccessible(true);
        String id = (String) methoda.invoke(doorayService, "fhqht303@naver.com");

        log.info("id : {}",id);

        assertNotNull(id);
        assertEquals("JSON 형식을 확인 해보세요",id);
    }

    @Test
    @DisplayName("sendIndividualMsg - ObjectMapper 에러")
    void sendIndividualMsgErrorTest() throws Exception{
        String doorayURL = "https://api.dooray.com/messenger/v1/channels/direct-send";
        String result = """
                {
                    "header": {{
                        "resultCode": 0,
                        "resultMessage": "",
                        "isSuccessful": true
                    }},
                    {"result": null}
                }
                """;

        serviceServer
                .expect(requestTo(doorayURL))
                .andExpect(MockRestRequestMatchers.method(HttpMethod.POST))
                .andRespond(MockRestResponseCreators.withSuccess(result, MediaType.APPLICATION_JSON));

        Method method = doorayService.getClass().getDeclaredMethod("sendIndividualMsg", String.class, String.class);
        method.setAccessible(true);

        String id = (String) method.invoke(doorayService, "3884802321735904763", "Hello World");

        log.info("id : {}", id);
        assertNotNull(id);
        assertEquals("JSON 형식을 확인 해보세요",id);
    }

}
