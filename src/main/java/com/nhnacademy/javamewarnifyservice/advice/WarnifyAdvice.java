package com.nhnacademy.javamewarnifyservice.advice;

import com.nhnacademy.javamewarnifyservice.JavameWarnifyServiceApplication;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.mail.AuthenticationFailedException;
import java.time.LocalDateTime;

@RestControllerAdvice(basePackageClasses = JavameWarnifyServiceApplication.class)
public class WarnifyAdvice {

    @ExceptionHandler(AuthenticationFailedException.class)
    public ResponseEntity<ErrorResponse> emailAuthenticationFail(AuthenticationFailedException authenticationFailedException) {
        ErrorResponse errorResponse = new ErrorResponse(
                LocalDateTime.now(),
                HttpStatus.UNAUTHORIZED.value(),
                HttpStatus.UNAUTHORIZED.getReasonPhrase(),
                authenticationFailedException.getMessage()
        );
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorResponse);
    }

}
