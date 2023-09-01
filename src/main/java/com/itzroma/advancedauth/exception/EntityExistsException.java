package com.itzroma.advancedauth.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class EntityExistsException extends RuntimeException {
    public EntityExistsException(String message) {
        super(message);
    }

    public static EntityExistsException withField(Class<?> entityClass, String fieldName, String fieldValue) {
        return new EntityExistsException("%s with %s '%s' already exists".formatted(
                entityClass.getSimpleName(), fieldName, fieldValue
        ));
    }
}
