package com.itzroma.advancedauth.service.impl;

import com.itzroma.advancedauth.email.event.EmailVerificationEvent;
import com.itzroma.advancedauth.exception.BadCredentialsException;
import com.itzroma.advancedauth.model.EmailVerificationToken;
import com.itzroma.advancedauth.model.User;
import com.itzroma.advancedauth.security.AuthProvider;
import com.itzroma.advancedauth.security.AuthUserDetails;
import com.itzroma.advancedauth.security.JwtProvider;
import com.itzroma.advancedauth.service.AuthService;
import com.itzroma.advancedauth.service.EmailVerificationTokenService;
import com.itzroma.advancedauth.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class DefaultAuthService implements AuthService {
    private final UserService userService;
    private final EmailVerificationTokenService emailVerificationTokenService;
    private final ApplicationEventPublisher publisher;
    private final AuthenticationManager authenticationManager;
    private final JwtProvider jwtProvider;

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

    @Override
    public String signIn(String email, String password) {
        User user = userService.findByEmail(email);

        if (user.getAuthProvider() != AuthProvider.LOCAL) {
            throw new BadCredentialsException("This user registered with " + user.getAuthProvider() + " provider");
        }

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(user.getEmail(), password)
        );
        return jwtProvider.generateAccessToken((AuthUserDetails) authentication.getPrincipal());
    }
}
