package com.itzroma.advancedauth.resource;

import com.itzroma.advancedauth.dto.mapper.RequestMapper;
import com.itzroma.advancedauth.dto.mapper.ResponseMapper;
import com.itzroma.advancedauth.dto.request.UserRequestDto;
import com.itzroma.advancedauth.dto.response.UserResponseDto;
import com.itzroma.advancedauth.model.User;
import com.itzroma.advancedauth.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("users")
@RequiredArgsConstructor
public class UserResource {
    private final UserService userService;
    private final RequestMapper<User, UserRequestDto> requestMapper;
    private final ResponseMapper<User, UserResponseDto> responseMapper;

    @PostMapping
    public ResponseEntity<UserResponseDto> save(@RequestBody UserRequestDto dto) {
        User saved = userService.save(requestMapper.toEntity(dto));
        return new ResponseEntity<>(responseMapper.toDto(saved), HttpStatus.CREATED);
    }
}
