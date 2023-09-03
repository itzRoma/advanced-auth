package com.itzroma.advancedauth.service.impl;

import com.itzroma.advancedauth.exception.BadCredentialsException;
import com.itzroma.advancedauth.exception.BadRequestException;
import com.itzroma.advancedauth.exception.EmailTakenException;
import com.itzroma.advancedauth.model.Role;
import com.itzroma.advancedauth.model.User;
import com.itzroma.advancedauth.repository.UserRepository;
import com.itzroma.advancedauth.security.AuthProvider;
import com.itzroma.advancedauth.service.RoleService;
import com.itzroma.advancedauth.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class DefaultUserService implements UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final RoleService roleService;

    @Override
    public User save(User user) {
        Optional<User> userOptional = userRepository.findByEmail(user.getEmail());
        if (userOptional.isPresent()) {
            if (userOptional.get().getAuthProvider() != AuthProvider.LOCAL) {
                throw new BadRequestException(
                        "This user registered with " + userOptional.get().getAuthProvider() + " provider"
                );
            }
            throw new EmailTakenException();
        }
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.getRoles().add(roleService.findByRoleName(Role.RoleName.USER));
        return userRepository.save(user);
    }

    @Override
    public void enable(User user) {
        userRepository.enable(user.getEmail());
    }

    @Override
    public User findByEmail(String email) {
        return userRepository.findByEmail(email).orElseThrow(BadCredentialsException::new);
    }
}
