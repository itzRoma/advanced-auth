package com.itzroma.advancedauth.service.impl;

import com.itzroma.advancedauth.exception.EVTConfirmedException;
import com.itzroma.advancedauth.exception.EVTExpiredException;
import com.itzroma.advancedauth.exception.EntityNotFoundException;
import com.itzroma.advancedauth.model.EmailVerificationToken;
import com.itzroma.advancedauth.model.User;
import com.itzroma.advancedauth.repository.EmailVerificationTokenRepository;
import com.itzroma.advancedauth.service.EmailVerificationTokenService;
import com.itzroma.advancedauth.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class DefaultEmailVerificationTokenService implements EmailVerificationTokenService {
    private final EmailVerificationTokenRepository emailVerificationTokenRepository;
    private final UserService userService;

    @Override
    public EmailVerificationToken findByToken(String token) {
        return emailVerificationTokenRepository.findByToken(token).orElseThrow(
                () -> new EntityNotFoundException("Invalid email verification token")
        );
    }

    @Override
    public EmailVerificationToken generateEmailVerificationToken(User user) {
        return emailVerificationTokenRepository.save(
                new EmailVerificationToken(UUID.randomUUID().toString(), user)
        );
    }

    @Override
    @Transactional
    public boolean validateEmailVerificationToken(String token) {
        EmailVerificationToken emailVerificationToken = findByToken(token);

        if (emailVerificationToken.getConfirmedAt() != null) {
            throw new EVTConfirmedException("Email is already confirmed");
        }
        if (emailVerificationToken.getExpiresAt().isBefore(LocalDateTime.now())) {
            throw new EVTExpiredException("Email verification token is expired");
        }

        emailVerificationTokenRepository.confirm(token, LocalDateTime.now());
        userService.enable(emailVerificationToken.getUser());
        return true;
    }
}
