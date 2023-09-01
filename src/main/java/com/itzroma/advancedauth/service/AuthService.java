package com.itzroma.advancedauth.service;

import com.itzroma.advancedauth.model.User;

public interface AuthService {
    User signUp(User user);

    boolean verifyEmailVerificationToken(String token);

    String signIn(String email, String password);
}
