package com.itzroma.advancedauth.util;

public record FullName(String firstName, String lastName) {
    public static FullName fromName(String name) {
        String[] parts = name.split(" +");
        return new FullName(parts[0], parts.length == 1 ? "" : parts[1]);
    }
}
