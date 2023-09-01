package com.itzroma.advancedauth.dto.request;

public record SignInRequestDto(
        String email,
        String password
) {
}
