package com.itzroma.advancedauth.dto.mapper.impl;

import com.itzroma.advancedauth.dto.mapper.RequestMapper;
import com.itzroma.advancedauth.dto.request.SignUpRequestDto;
import com.itzroma.advancedauth.model.User;
import org.springframework.stereotype.Component;

@Component
public class UserSignUpMapper implements RequestMapper<User, SignUpRequestDto> {
    @Override
    public User toEntity(SignUpRequestDto dto) {
        return new User(dto.email(), dto.password());
    }
}
