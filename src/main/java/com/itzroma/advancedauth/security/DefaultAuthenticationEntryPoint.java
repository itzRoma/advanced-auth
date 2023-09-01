package com.itzroma.advancedauth.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.itzroma.advancedauth.dto.response.ExceptionResponseDto;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.OutputStream;

@Component
public class DefaultAuthenticationEntryPoint implements AuthenticationEntryPoint {
    @Override
    public void commence(HttpServletRequest request,
                         HttpServletResponse response,
                         AuthenticationException authException) throws IOException, ServletException {
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setStatus(HttpStatus.UNAUTHORIZED.value());
        OutputStream outputStream = response.getOutputStream();
        new ObjectMapper().writeValue(outputStream, new ExceptionResponseDto(
                HttpStatus.UNAUTHORIZED, authException.getLocalizedMessage()
        ));
        outputStream.flush();
    }
}
