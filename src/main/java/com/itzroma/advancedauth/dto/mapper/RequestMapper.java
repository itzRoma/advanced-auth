package com.itzroma.advancedauth.dto.mapper;

public interface RequestMapper<E, D> {
    E toEntity(D dto);
}
