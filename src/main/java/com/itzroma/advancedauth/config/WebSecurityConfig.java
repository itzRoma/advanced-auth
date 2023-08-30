package com.itzroma.advancedauth.config;

import com.itzroma.advancedauth.security.HttpCookieOAuth2AuthorizationRequestRepository;
import com.itzroma.advancedauth.security.Oauth2FailureHandler;
import com.itzroma.advancedauth.security.Oauth2SuccessHandler;
import com.itzroma.advancedauth.service.Oauth2Service;
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
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class WebSecurityConfig {
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
    public SecurityFilterChain securityFilterChain(HttpSecurity http,
                                                   Oauth2Service oauth2Service,
                                                   AuthenticationEntryPoint authenticationEntryPoint,
                                                   Oauth2SuccessHandler successHandler,
                                                   Oauth2FailureHandler failureHandler,
                                                   HttpCookieOAuth2AuthorizationRequestRepository authorizationRequestRepository) throws Exception {
        return http
                .formLogin(Customizer.withDefaults())
                .oauth2Login(config -> config
                        .authorizationEndpoint(aeConfig -> aeConfig
                                .authorizationRequestRepository(authorizationRequestRepository)
                        )
                        .userInfoEndpoint(userInfoConfig -> userInfoConfig
                                .userService(oauth2Service::processOauth2Auth)
                                .oidcUserService(oauth2Service::processOidcAuth)
                        )
                        .successHandler(successHandler)
                        .failureHandler(failureHandler)
                )
                .authorizeHttpRequests(config -> config.anyRequest().permitAll())
                .exceptionHandling(config -> config.authenticationEntryPoint(authenticationEntryPoint))
                .sessionManagement(config -> config.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .build();
    }
}
