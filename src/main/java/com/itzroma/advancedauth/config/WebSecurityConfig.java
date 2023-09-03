package com.itzroma.advancedauth.config;

import com.itzroma.advancedauth.security.JwtAuthenticationFilter;
import com.itzroma.advancedauth.security.oauth2.HttpCookieOAuth2AuthorizationRequestRepository;
import com.itzroma.advancedauth.security.oauth2.Oauth2FailureHandler;
import com.itzroma.advancedauth.security.oauth2.Oauth2LoginService;
import com.itzroma.advancedauth.security.oauth2.Oauth2SuccessHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
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
    public SecurityFilterChain securityFilterChain(
            HttpSecurity http,
            AuthenticationEntryPoint authenticationEntryPoint,
            HttpCookieOAuth2AuthorizationRequestRepository authorizationRequestRepository,
            Oauth2LoginService oauth2LoginService,
            Oauth2SuccessHandler successHandler,
            Oauth2FailureHandler failureHandler,
            JwtAuthenticationFilter jwtAuthenticationFilter
    ) throws Exception {
        return http
                .cors(Customizer.withDefaults())
                .csrf(AbstractHttpConfigurer::disable)
                .httpBasic(AbstractHttpConfigurer::disable)
                .formLogin(AbstractHttpConfigurer::disable)
                .sessionManagement(config -> config.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .exceptionHandling(config -> config.authenticationEntryPoint(authenticationEntryPoint))
                .authorizeHttpRequests(config -> config
                        .requestMatchers("/auth/**", "/oauth2/**", "/test/all").permitAll()
                        .anyRequest().authenticated()
                )
                .oauth2Login(config -> config
                        .authorizationEndpoint(aeConfig -> aeConfig
                                .authorizationRequestRepository(authorizationRequestRepository)
                        )
                        .userInfoEndpoint(uieConfig -> uieConfig
                                .userService(oauth2LoginService)
                        )
                        .redirectionEndpoint(reConfig -> reConfig
                                .baseUri("/oauth2/callback/*")
                        )
                        .successHandler(successHandler)
                        .failureHandler(failureHandler)
                )
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
                .build();
    }
}
