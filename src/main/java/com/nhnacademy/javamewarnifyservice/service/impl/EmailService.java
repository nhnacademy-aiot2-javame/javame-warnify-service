package com.nhnacademy.javamewarnifyservice.service.impl;

import com.nhnacademy.javamewarnifyservice.adaptor.CompanyAdaptor;
import com.nhnacademy.javamewarnifyservice.advice.exception.CompanyNotFoundException;
import com.nhnacademy.javamewarnifyservice.dto.CompanyResponse;
import com.nhnacademy.javamewarnifyservice.service.WarnifyService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Properties;

@Slf4j
@Service("emailService")
@RequiredArgsConstructor
public class EmailService implements WarnifyService {

    /**
     * MemberAPI - CompanyController 사용.
     * CompanyAdaptor 호출.
     */
    private final CompanyAdaptor companyAdaptor;

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

    @Override
    public String sendAlarm(String companyDomain, String warnInfo) {

        // 알림 받는 이메일
        String receiveEmail = getCompanyResponse(companyDomain).getCompanyEmail();

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
        } catch (AddressException e) {
            log.error("AddressException error : {}", e.toString());
            return "이메일 전송 실패";
        } catch (MessagingException e) {
            log.error("MessagingException error : {}", e.toString());
            return "이메일 전송 실패";
        }

    }

    private CompanyResponse getCompanyResponse(String companyDomain) {
        ResponseEntity<CompanyResponse> companyResponseResponseEntity = companyAdaptor.getCompanyByDomain(companyDomain);

        if (!companyResponseResponseEntity.getStatusCode().is2xxSuccessful()) {
            throw new CompanyNotFoundException("회사를 찾기에 실패했습니다.");
        }

        return companyResponseResponseEntity.getBody();
    }

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
