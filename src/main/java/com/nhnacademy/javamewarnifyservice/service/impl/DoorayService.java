package com.nhnacademy.javamewarnifyservice.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nhnacademy.javamewarnifyservice.adaptor.MemberApiAdaptor;
import com.nhnacademy.javamewarnifyservice.dto.CompanyResponse;
import com.nhnacademy.javamewarnifyservice.service.SendWarnifyService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

/**
 * Dooray 메시지 발송.
 */
@Slf4j
@Service("doorayService")
@RequiredArgsConstructor
public class DoorayService implements SendWarnifyService {

    /**
     * MemberAPI -> CompanyController 사용. MemberApiAdaptor 호출.
     */
    private final MemberApiAdaptor memberApiAdaptor;

    /**
     * RestClient Builder 호출.
     */
    private final RestClient.Builder restClientBuilder;

    /**
     * 두레이 API KEY.
     */
    @Value("${security.dooray.apiKey}")
    private String apiKey;

    /**
     * 서비스의 종류를 나타내는 필드값입니다.
     */
    private static final String TYPE = "dooray";



    @Override
    public String getType() {
        return TYPE;
    }

    @Override
    public String sendAlarm(String companyDomain, String warnInfo) {
        CompanyResponse companyResponse = getCompanyResponse(companyDomain, memberApiAdaptor);
        String doorayId = getDoorayMemberId(companyResponse.getCompanyEmail());

        String result = sendIndividualMsg(doorayId, warnInfo);

        return result.equalsIgnoreCase("true") ? "두레이 발신 성공" : "두레이 발신 실패";
    }

    /**
     * Company email을 두레이에서 조회.
     * 두레이에서 멤버ID 가져오기.
     * @param email Company Eamil
     * @return Dooray Member ID
     */
    private String getDoorayMemberId(String email) {
        String doorayURL = "https://api.dooray.com/common/v1/members?externalEmailAddresses=%s".formatted(email);

        RestClient restClient = setUpRestClient();
        log.info("doorayURL : {}",doorayURL);
        String result = restClient.get()
                .uri(doorayURL)
                .retrieve()
                .body(String.class);

        log.info("result : {}", result);

        ObjectMapper objectMapper = new ObjectMapper();
        try {
            JsonNode node = objectMapper.readTree(result);
            return node.path("result").get(0).path("id").asText();
        } catch (JsonProcessingException e) {
            return "JSON 형식을 확인 해보세요";
        }
    }

    /**
     * 1:1 개인 두레이 메신져 발송하는 메서드.
     * @param id 두레이 ID
     * @param warnInfo 경고 정보
     * @return 발신 성공했으면 true
     */
    private String sendIndividualMsg(String id, String warnInfo) {
        String doorayURL = "https://api.dooray.com/messenger/v1/channels/direct-send";
        String body =
                """
                {
                    "text": "%s",
                    "organizationMemberId": "%s"
                }
                """.formatted(warnInfo, id);

        RestClient restClient = setUpRestClient();
        String result = restClient.post()
                .uri(doorayURL)
                .body(body)
                .retrieve()
                .body(String.class);

        ObjectMapper objectMapper = new ObjectMapper();
        try {
            JsonNode node = objectMapper.readTree(result);
            return node.path("header").path("isSuccessful").asText();
        } catch (JsonProcessingException e) {
            return "JSON 형식을 확인 해보세요";
        }
    }

    /**
     * 두레이 API 사용하기위한 헤더 셋팅.
     * @return 셋팅된 RestClient
     */
    private RestClient setUpRestClient() {
        return restClientBuilder
                .defaultHeader("Authorization", "dooray-api " + apiKey)
                .defaultHeader("Content-Type", "application/json")
                .build();
    }

}
