package com.nhnacademy.javamewarnifyservice.advice.exception;

public class WarnifyNotFoundException extends RuntimeException {
    public WarnifyNotFoundException(String message) {
        super(message);
    }
}
