package com.itzroma.advancedauth.dto.mapper.impl;

import com.itzroma.advancedauth.dto.mapper.ResponseMapper;
import com.itzroma.advancedauth.dto.response.UserResponseDto;
import com.itzroma.advancedauth.model.User;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class UserMapper implements ResponseMapper<User, UserResponseDto> {
    @Override
    public UserResponseDto toDto(User user) {
        List<String> roles = user.getRoles().stream()
                .map(role -> role.getRoleName().name())
                .toList();
        return new UserResponseDto(user.getId(), user.getEmail(), roles);
    }
}
