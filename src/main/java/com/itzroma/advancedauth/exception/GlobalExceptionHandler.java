package com.itzroma.advancedauth.exception;

import com.itzroma.advancedauth.dto.response.ExceptionResponseDto;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {
    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ExceptionResponseDto> badCredentials(BadCredentialsException exception) {
        return createResponse(HttpStatus.BAD_REQUEST, exception.getLocalizedMessage());
    }

    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<ExceptionResponseDto> badRequest(BadRequestException exception) {
        return createResponse(HttpStatus.BAD_REQUEST, exception.getLocalizedMessage());
    }

    @ExceptionHandler(EmailTakenException.class)
    public ResponseEntity<ExceptionResponseDto> emailTaken(EmailTakenException exception) {
        return createResponse(HttpStatus.BAD_REQUEST, exception.getLocalizedMessage());
    }

    @ExceptionHandler(EntityExistsException.class)
    public ResponseEntity<ExceptionResponseDto> entityExists(EntityExistsException exception) {
        return createResponse(HttpStatus.BAD_REQUEST, exception.getLocalizedMessage());
    }

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<ExceptionResponseDto> entityNotFound(EntityNotFoundException exception) {
        return createResponse(HttpStatus.NOT_FOUND, exception.getLocalizedMessage());
    }

    private ResponseEntity<ExceptionResponseDto> createResponse(HttpStatus httpStatus, String message) {
        return new ResponseEntity<>(new ExceptionResponseDto(httpStatus, message), httpStatus);
    }
}
