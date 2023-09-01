package com.itzroma.advancedauth.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
public class Oauth2AuthenticationProcessingException extends RuntimeException {
    public Oauth2AuthenticationProcessingException(String message) {
        super(message);
    }
}
