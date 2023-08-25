package com.itzroma.advancedauth.service.impl;

import com.itzroma.advancedauth.email.event.EmailVerificationEvent;
import com.itzroma.advancedauth.model.EmailVerificationToken;
import com.itzroma.advancedauth.model.User;
import com.itzroma.advancedauth.service.AuthService;
import com.itzroma.advancedauth.service.EmailVerificationTokenService;
import com.itzroma.advancedauth.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class DefaultAuthService implements AuthService {
    private final UserService userService;
    private final EmailVerificationTokenService emailVerificationTokenService;
    private final ApplicationEventPublisher publisher;

    @Value("${app.path}")
    private String appPath;

    @Override
    @Transactional
    public User signUp(User user) {
        User savedUser = userService.save(user);
        EmailVerificationToken emailVerificationToken =
                emailVerificationTokenService.generateEmailVerificationToken(user);
        sendEmailVerificationToken(savedUser, emailVerificationToken);
        return savedUser;
    }

    private void sendEmailVerificationToken(User forUser, EmailVerificationToken token) {
        String verificationLink = appPath + "/auth";
        publisher.publishEvent(new EmailVerificationEvent(forUser, verificationLink, token));
    }

    @Override
    public boolean verifyEmailVerificationToken(String token) {
        return emailVerificationTokenService.validateEmailVerificationToken(token);
    }
}
