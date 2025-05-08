package com.nhnacademy.javamewarnifyservice.advice.exception;

public class MemberListNotFound extends RuntimeException {
    public MemberListNotFound(String message) {
        super(message);
    }
}
