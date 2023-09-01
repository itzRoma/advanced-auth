package com.itzroma.advancedauth.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import org.springframework.http.HttpStatus;

import java.util.Date;

public record ExceptionResponseDto(
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "MM-dd-yyyy HH:mm:ss")
        Date timestamp,
        int statusCode,
        String reasonPhrase,
        String message
) {
    public ExceptionResponseDto(HttpStatus httpStatus, String message) {
        this(new Date(System.currentTimeMillis()), httpStatus.value(), httpStatus.getReasonPhrase(), message);
    }
}
