package com.itzroma.advancedauth.util;

public record FirstLastNames(String firstName, String lastName) {
    public static FirstLastNames fromName(String name) {
        String[] parts = name.split(" +");
        return new FirstLastNames(parts[0], parts.length == 1 ? "" : parts[1]);
    }
}
