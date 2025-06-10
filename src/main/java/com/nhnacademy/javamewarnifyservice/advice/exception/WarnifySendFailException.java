package com.nhnacademy.javamewarnifyservice.advice.exception;

public class WarnifySendFailException extends RuntimeException{
    public WarnifySendFailException(String message){
        super(message);
    }
}
