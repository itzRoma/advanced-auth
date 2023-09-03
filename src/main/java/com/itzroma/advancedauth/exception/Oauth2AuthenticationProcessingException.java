package com.itzroma.advancedauth.exception;

import org.springframework.security.core.AuthenticationException;

public class Oauth2AuthenticationProcessingException extends AuthenticationException {
    public Oauth2AuthenticationProcessingException(String message) {
        super(message);
    }
}
