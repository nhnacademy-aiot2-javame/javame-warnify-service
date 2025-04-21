package com.nhnacademy.javamewarnifyservice.service;

public interface WarnifyService {

    String sendEmail(String companyDomain, String warnInfo);

    String sendSMS(String phoneNumber, String info);

}
