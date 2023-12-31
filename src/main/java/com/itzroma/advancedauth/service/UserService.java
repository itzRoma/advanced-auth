package com.itzroma.advancedauth.service;

import com.itzroma.advancedauth.model.User;

public interface UserService {
    User save(User user);

    void enable(User user);

    User findByEmail(String email);
}
