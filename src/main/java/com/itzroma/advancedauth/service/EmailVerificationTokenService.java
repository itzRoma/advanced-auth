package com.itzroma.advancedauth.service;

import com.itzroma.advancedauth.model.EmailVerificationToken;
import com.itzroma.advancedauth.model.User;

public interface EmailVerificationTokenService {
    EmailVerificationToken findByToken(String token);

    EmailVerificationToken generateEmailVerificationToken(User user);

    boolean validateEmailVerificationToken(String token);
}
