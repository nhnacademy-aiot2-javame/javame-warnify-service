package com.nhnacademy.javamewarnifyservice.service.impl;

import com.nhnacademy.javamewarnifyservice.adaptor.MemberApiAdaptor;
import com.nhnacademy.javamewarnifyservice.service.SendWarnifyService;
import lombok.extern.slf4j.Slf4j;
import net.nurigo.sdk.NurigoApp;
import net.nurigo.sdk.message.model.Message;
import net.nurigo.sdk.message.request.SingleMessageSendingRequest;
import net.nurigo.sdk.message.response.SingleMessageSentResponse;
import net.nurigo.sdk.message.service.DefaultMessageService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Slf4j
@Service("smsService")
public class SmsServiceSend implements SendWarnifyService {

    /**
     * MemberAPI - CompanyController 사용.
     * MemberApiAdaptor 호출.
     */
    private final MemberApiAdaptor memberApiAdaptor;

    /**
     * 누리고 API KEY.
     */
    @Value("${security.sms.apiKey}")
    private String apiKey;

    /**
     * 누리고 API Secret KEY.
     */
    @Value("${security.sms.apiSecretKey}")
    private String apiSecretKey;

    public SmsServiceSend(MemberApiAdaptor memberApiAdaptor) {
        this.memberApiAdaptor = memberApiAdaptor;
    }

    /**
     * 서비스의 종류를 나타내는 필드값입니다.
     */
    private static final String TYPE = "sms";

    @Override
    public String getType() {
        return TYPE;
    }


    @Override
    public String sendAlarm(String companyDomain, String warnInfo) {
        log.info("sms");
        DefaultMessageService messageService = NurigoApp.INSTANCE.initialize(apiKey, apiSecretKey,"https://api.coolsms.co.kr");
        Message message = new Message();
        message.setFrom("010-2681-1995");
        String receivePhoneNumber = getCompanyResponse(companyDomain, memberApiAdaptor).getCompanyMobile();
        message.setTo(receivePhoneNumber);
        message.setText("%s가 위험합니다! 확인하세요".formatted(warnInfo));

        SingleMessageSentResponse response
                = messageService.sendOne(new SingleMessageSendingRequest(message));
        log.info("message info : {}", response);

        //statusMessage=정상 접수(이통사로 접수 예정)
        return response.getStatusMessage();
    }

}
