package com.itzroma.advancedauth.resource;

import com.itzroma.advancedauth.dto.mapper.RequestMapper;
import com.itzroma.advancedauth.dto.mapper.ResponseMapper;
import com.itzroma.advancedauth.dto.request.SignInRequestDto;
import com.itzroma.advancedauth.dto.request.SignUpRequestDto;
import com.itzroma.advancedauth.dto.response.SignInResponseDto;
import com.itzroma.advancedauth.dto.response.UserResponseDto;
import com.itzroma.advancedauth.model.User;
import com.itzroma.advancedauth.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthResource {
    private final AuthService authService;
    private final RequestMapper<User, SignUpRequestDto> requestMapper;
    private final ResponseMapper<User, UserResponseDto> responseMapper;

    @PostMapping("/sign-up")
    public ResponseEntity<UserResponseDto> signUp(@RequestBody SignUpRequestDto dto) {
        User user = authService.signUp(requestMapper.toEntity(dto));
        return new ResponseEntity<>(responseMapper.toDto(user), HttpStatus.CREATED);
    }

    @GetMapping("/verify")
    public ResponseEntity<String> verify(@RequestParam("token") String token) {
        if (authService.verifyEmailVerificationToken(token)) {
            return ResponseEntity.ok("User is verified. You can close this tab");
        }

        return new ResponseEntity<>(
                "Cannot verify user, invalid email verification link. Try to request new one",
                HttpStatus.BAD_REQUEST
        );
    }

    @PostMapping("/sign-in")
    public ResponseEntity<SignInResponseDto> signIn(@RequestBody SignInRequestDto dto) {
        String accessToken = authService.signIn(dto.email(), dto.password());
        return ResponseEntity.ok(new SignInResponseDto(accessToken));
    }
}
