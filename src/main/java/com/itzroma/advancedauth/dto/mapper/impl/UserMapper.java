package com.itzroma.advancedauth.dto.mapper.impl;

import com.itzroma.advancedauth.dto.mapper.RequestMapper;
import com.itzroma.advancedauth.dto.mapper.ResponseMapper;
import com.itzroma.advancedauth.dto.request.UserRequestDto;
import com.itzroma.advancedauth.dto.response.UserResponseDto;
import com.itzroma.advancedauth.model.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class UserMapper implements RequestMapper<User, UserRequestDto>,
        ResponseMapper<User, UserResponseDto> {
    @Override
    public User toEntity(UserRequestDto dto) {
        return new User(dto.email(), dto.password());
    }

    @Override
    public UserResponseDto toDto(User user) {
        List<String> roles = user.getRoles().stream()
                .map(role -> role.getRoleName().name())
                .toList();
        return new UserResponseDto(user.getId(), user.getEmail(), roles);
    }
}
