package com.nhnacademy.javamewarnifyservice.service.impl;

import com.nhnacademy.javamewarnifyservice.adaptor.MemberApiAdaptor;
import com.nhnacademy.javamewarnifyservice.service.SendWarnifyService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Properties;

@Slf4j
@Service("emailService")
@RequiredArgsConstructor
public class EmailServiceSend implements SendWarnifyService {

    /**
     * MemberAPI - CompanyController 사용.
     * MemberApiAdaptor 호출.
     */
    private final MemberApiAdaptor memberApiAdaptor;

    /**
     * 전송용 이메일 아이디.
     */
    @Value("${security.email.id}")
    private String senderEmail;

    /**
     * 전송용 이메일 비밀번호.
     */
    @Value("${security.email.pwd}")
    private String senderPassword;

    /**
     * 서비스의 종류를 나타내는 필드값입니다.
     */
    private static final String TYPE = "email";

    @Override
    public String getType() {
        return TYPE;
    }


    @Override
    public String sendAlarm(String companyDomain, String warnInfo) {
        // 알림 받는 이메일
        String receiveEmail = getCompanyResponse(companyDomain, memberApiAdaptor).getCompanyEmail();

        // 이메일 제목
        String subject = "%s 경고 입니다. 확인하세요!!".formatted(warnInfo);

        // 이메일 내용
        String content = "%s 확인 하셔야 합니다.".formatted(warnInfo);

        String encoding = "UTF-8";
        try {
            Session session = mailSetting();
            MimeMessage message = new MimeMessage(session);
            message.setFrom(new InternetAddress(senderEmail));
            message.addRecipients(Message.RecipientType.TO, receiveEmail);
            message.setSubject(subject, encoding);
            message.setText(content,encoding);
            Transport.send(message);
            return "이메일 발송 성공";
        } catch (MessagingException e) {
            return "이메일 전송 실패";
        }

    }

    /**
     * 메일 전송 가능하도록 셋팅 해주는 곳.
     * @return Session 셋팅한 정보
     */
    private Session mailSetting() {

        // 구글 이메일 사용, 사용할수 있도록 셋팅
        Properties properties = new Properties();
        properties.put("mail.smtp.host", "smtp.gmail.com");
        properties.put("mail.smtp.port", 465);
        properties.put("mail.smtp.auth", "true");
        properties.put("mail.smtp.ssl.enable", "true");
        properties.put("mail.smtp.ssl.trust", "smtp.gmail.com");
        properties.put("mail.smtp.ssl.protocols","TLSv1.2");

        // 보내는 이메일,비밀번호 session에 입력
        return Session.getDefaultInstance(properties, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(senderEmail, senderPassword);
            }
        });

    }

}
