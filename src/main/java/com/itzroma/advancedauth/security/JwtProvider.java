package com.itzroma.advancedauth.security;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.exceptions.SignatureVerificationException;
import com.auth0.jwt.exceptions.TokenExpiredException;
import jakarta.annotation.PostConstruct;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Date;

@Log4j2
@Component
public class JwtProvider {
    @Value("${app.security.jwt.access.secret}")
    private String accessSecret;

    @Value("${app.security.jwt.access.expiration-ms}")
    private Long expirationMs;

    private Algorithm accessAlgorithm;

    @PostConstruct
    private void init() {
        accessAlgorithm = Algorithm.HMAC512(accessSecret);
    }

    public String generateAccessToken(AuthUserDetails authUserDetails) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + expirationMs);

        return JWT.create()
                .withSubject(authUserDetails.getEmail())
                .withIssuedAt(now)
                .withExpiresAt(expiryDate)
                .sign(accessAlgorithm);
    }

    public String getUserEmailFromToken(String token) {
        JWTVerifier verifier = JWT.require(accessAlgorithm).build();
        return verifier.verify(token).getSubject();
    }

    public boolean validateToken(String token) {
        try {
            JWT.require(accessAlgorithm).build().verify(token);
            return true;
        } catch (SignatureVerificationException ex) {
            log.error("Invalid JWT signature: {}", ex.getMessage());
        } catch (TokenExpiredException ex) {
            log.error("JWT token is expired: {}", ex.getMessage());
        } catch (JWTVerificationException ex) {
            log.error("Invalid JWT token: {}", ex.getMessage());
        } catch (Exception ex) {
            log.error("JWT token validation failed: {}", ex.getMessage());
        }
        return false;
    }
}
