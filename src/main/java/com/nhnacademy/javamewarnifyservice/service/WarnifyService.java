package com.nhnacademy.javamewarnifyservice.service;

public interface WarnifyService {

    String sendEmail(String email, String info);

    String sendSMS(String phoneNumber, String info);

}
