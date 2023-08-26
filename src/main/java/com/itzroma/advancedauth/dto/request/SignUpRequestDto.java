package com.itzroma.advancedauth.dto.request;

public record SignUpRequestDto(
        String firstName,
        String lastName,
        String email,
        String password
) {
}
