package com.itzroma.advancedauth.exception;

public class EntityNotFoundException extends RuntimeException {
    public EntityNotFoundException(String message) {
        super(message);
    }

    public static EntityNotFoundException withId(Class<?> entityClass, Long id) {
        return new EntityNotFoundException("%s with id '%d' not found".formatted(entityClass.getSimpleName(), id));
    }

    public static EntityNotFoundException withField(Class<?> entityClass, String fieldName, String fieldValue) {
        return new EntityNotFoundException("%s with %s '%s' not found".formatted(
                entityClass.getSimpleName(), fieldName, fieldValue
        ));
    }
}
