package com.itzroma.advancedauth.config;

import com.itzroma.advancedauth.model.User;
import com.itzroma.advancedauth.repository.UserRepository;
import com.itzroma.advancedauth.security.AuthProvider;
import com.itzroma.advancedauth.security.AuthUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserRequest;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserService;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.SecurityFilterChain;

import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class WebSecurityConfig {
    private final UserRepository userRepository;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authManager(UserDetailsService userDetailsService) {
        DaoAuthenticationProvider daoProvider = new DaoAuthenticationProvider();
        daoProvider.setUserDetailsService(userDetailsService);
        daoProvider.setPasswordEncoder(passwordEncoder());
        return new ProviderManager(daoProvider);
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                .csrf(AbstractHttpConfigurer::disable)
                .formLogin(Customizer.withDefaults())
                .oauth2Login(config -> config
                        .userInfoEndpoint(userInfoConfig -> userInfoConfig
                                .userService(handleOauth2Login())
                                .oidcUserService(handleOidcLogin())
                        )
                )
                .authorizeHttpRequests(authorize -> authorize
                        .anyRequest().permitAll()
                )
                .build();
    }

    private OAuth2UserService<OAuth2UserRequest, OAuth2User> handleOauth2Login() {
        return userRequest -> {
            OAuth2User oAuth2User = new DefaultOAuth2UserService().loadUser(userRequest);
            FirstLast firstLast = FirstLast.fromName(Objects.requireNonNull(oAuth2User.getAttribute("name")));
            AuthUserDetails userDetails = AuthUserDetails.builder()
                    .firstName(firstLast.firstName)
                    .lastName(firstLast.lastName)
                    .attributes(oAuth2User.getAttributes())
                    .authorities(oAuth2User.getAuthorities())
                    .password(passwordEncoder().encode(UUID.randomUUID().toString()))
                    .email(oAuth2User.getAttribute("email"))
                    .enabled(true)
                    .imageUrl(oAuth2User.getAttribute("avatar_url"))
                    .authProvider(AuthProvider.valueOf(userRequest.getClientRegistration().getRegistrationId().toUpperCase()))
                    .build();
            saveOrUpdate(userDetails);
            return userDetails;
        };
    }

    private OAuth2UserService<OidcUserRequest, OidcUser> handleOidcLogin() {
        return userRequest -> {
            OidcUser oidcUser = new OidcUserService().loadUser(userRequest);
            AuthUserDetails userDetails = AuthUserDetails.builder()
                    .firstName(oidcUser.getGivenName())
                    .lastName(oidcUser.getFamilyName())
                    .attributes(oidcUser.getAttributes())
                    .authorities(oidcUser.getAuthorities())
                    .password(passwordEncoder().encode(UUID.randomUUID().toString()))
                    .email(oidcUser.getEmail())
                    .enabled(true)
                    .claims(oidcUser.getClaims())
                    .userInfo(oidcUser.getUserInfo())
                    .idToken(oidcUser.getIdToken())
                    .imageUrl(oidcUser.getPicture())
                    .authProvider(AuthProvider.valueOf(userRequest.getClientRegistration().getRegistrationId().toUpperCase()))
                    .build();
            saveOrUpdate(userDetails);
            return userDetails;
        };
    }

    private record FirstLast(String firstName, String lastName) {
        public static FirstLast fromName(String name) {
            String[] parts = name.split(" +");
            return new FirstLast(parts[0], parts.length == 1 ? "" : parts[1]);
        }
    }

    private void saveOrUpdate(AuthUserDetails userDetails) {
        Optional<User> userOptional = userRepository.findByEmail(userDetails.getEmail());
        User user;
        if (userOptional.isPresent()) { // update existing
            user = userOptional.get();
            if (user.getAuthProvider() != userDetails.getAuthProvider()) {
                throw new IllegalStateException("This user registered with " + user.getAuthProvider() + " provider");
            }

            user.setFirstName(userDetails.getFirstName());
            user.setLastName(userDetails.getLastName());
            user.setImageUrl(userDetails.getImageUrl());
        } else { // save new
            user = new User(
                    userDetails.getFirstName(),
                    userDetails.getLastName(),
                    userDetails.getEmail(),
                    userDetails.getPassword(),
                    userDetails.getEnabled(),
                    userDetails.getImageUrl(),
                    userDetails.getAuthProvider()
            );
        }
        userRepository.save(user);
    }
}
