package com.nhnacademy.javamewarnifyservice.service.impl;

import com.nhnacademy.javamewarnifyservice.service.WarnifyService;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
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
@Service
public class WarnifyServiceImpl implements WarnifyService {

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
    public String sendEmail(String email, String info) {

        // 구글껄로 되어있음.
        Properties properties = new Properties();
        properties.put("mail.smtp.host", "smtp.gmail.com");
        properties.put("mail.smtp.port", 465);
        properties.put("mail.smtp.auth", "true");
        properties.put("mail.smtp.ssl.enable", "true");
        properties.put("mail.smtp.ssl.trust", "smtp.gmail.com");
        properties.put("mail.smtp.ssl.protocols","TLSv1.2");

        String revEmail = "fhqht303@naver.com";

        Session session = Session.getDefaultInstance(properties, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(senderEmail, senderPassword);
            }
        });

        String encoding = "UTF-8";

        try {
            MimeMessage message = new MimeMessage(session);
            message.setFrom(new InternetAddress(senderEmail));
            message.addRecipients(Message.RecipientType.TO, revEmail);
            message.setSubject("제목", encoding);
            message.setText("제목입니다.",encoding);
            Transport.send(message);
            return "성공";
        } catch (AddressException e) {
            log.error("AddressException error : {}", e.toString());
            return "실패";
        } catch (MessagingException e) {
            log.error("MessagingException error : {}", e.toString());
            return "실패";
        }

    }

    @Override
    public String sendSMS(String phoneNumber, String info) {
        return "";
    }

}
