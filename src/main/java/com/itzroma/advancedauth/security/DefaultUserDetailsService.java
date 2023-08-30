package com.itzroma.advancedauth.security;

import com.itzroma.advancedauth.model.User;
import com.itzroma.advancedauth.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DefaultUserDetailsService implements UserDetailsService {
    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByEmailWithRoles(username).orElseThrow(
                () -> new UsernameNotFoundException("User with email '%s' not found".formatted(username))
        );

//        if (user.getAuthProvider() != AuthProvider.LOCAL) {
//            throw new IllegalStateException("This user registered with " + user.getAuthProvider() + " provider");
//        }

        List<SimpleGrantedAuthority> authorities = user.getRoles().stream()
                .map(role -> new SimpleGrantedAuthority(role.getRoleName().name()))
                .toList();
        return AuthUserDetails.builder()
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .authorities(authorities)
                .password(user.getPassword())
                .email(user.getEmail())
                .enabled(user.getEnabled())
                .imageUrl(user.getImageUrl())
                .authProvider(user.getAuthProvider())
                .build();
    }
}
