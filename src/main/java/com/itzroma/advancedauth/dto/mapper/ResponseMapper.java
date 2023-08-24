package com.itzroma.advancedauth.dto.mapper;

public interface ResponseMapper<E, D> {
    D toDto(E entity);
}
