package com.example.domatetodo;

public class UserProfile {
    private final String username;
    private final String email;

    public UserProfile(String username, String email) {
        this.username = username;
        this.email = email;
    }

    public String getUsername() {
        return username;
    }

    public String getEmail() {
        return email;
    }
}
