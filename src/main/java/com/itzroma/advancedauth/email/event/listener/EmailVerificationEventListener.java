package com.itzroma.advancedauth.email.event.listener;

import com.itzroma.advancedauth.email.EmailSender;
import com.itzroma.advancedauth.email.event.EmailVerificationEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

@Component
@RequiredArgsConstructor
public class EmailVerificationEventListener {
    private final EmailSender emailSender;

    @EventListener
    public void onApplicationEvent(EmailVerificationEvent event) {
        String url = event.getUrl() + "/verify?token=" + event.getEmailVerificationToken().getToken();
        String userEmail = event.getUser().getEmail();

        // TODO: 8/25/2023 think how to replace hardcoded value
        long expirationMinutes = 600_000 / 1000 / 60;
        emailSender.send(userEmail, buildEmail(userEmail, url, expirationMinutes));
    }

    private String buildEmail(String name, String link, long expirationMinutes) {
        try {
            String emailText = Files.readString(Path.of("src/main/resources/static/email-verification-letter.html"));
            emailText = emailText.replace("[name-placeholder]", name);
            emailText = emailText.replace("[activation-link-placeholder]", link);
            emailText = emailText.replace("[expiration-minutes-placeholder]", String.valueOf(expirationMinutes));
            return emailText;
        } catch (IOException e) {
            throw new IllegalStateException("Failed to build an email", e);
        }
    }
}
