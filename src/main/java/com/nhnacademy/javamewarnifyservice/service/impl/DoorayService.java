package com.nhnacademy.javamewarnifyservice.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nhnacademy.javamewarnifyservice.adaptor.CompanyAdaptor;
import com.nhnacademy.javamewarnifyservice.dto.CompanyResponse;
import com.nhnacademy.javamewarnifyservice.service.WarnifyService;
import lombok.Getter;
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
public class DoorayService implements WarnifyService {

    /**
     * MemberAPI -> CompanyController 사용. CompanyAdaptor 호출.
     */
    private final CompanyAdaptor companyAdaptor;

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
        CompanyResponse companyResponse = getCompanyResponse(companyDomain, companyAdaptor);
        String doorayId = getDoorayMemberId(companyResponse.getCompanyEmail());

        String result = sendIndividualMsg(doorayId, warnInfo);

        return result.equalsIgnoreCase("true") ? "두레이 발신 성공" : "두레이 발신 실패";
    }

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
            throw new RuntimeException(e);
        }
    }

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
            throw new RuntimeException(e);
        }
    }

    private RestClient setUpRestClient() {
        return restClientBuilder
                .defaultHeader("Authorization", "dooray-api " + apiKey)
                .defaultHeader("Content-Type", "application/json")
                .build();
    }
}
