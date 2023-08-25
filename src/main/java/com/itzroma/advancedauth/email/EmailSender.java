package com.itzroma.advancedauth.email;

public interface EmailSender {
    void send(String receiver, String email);
}
