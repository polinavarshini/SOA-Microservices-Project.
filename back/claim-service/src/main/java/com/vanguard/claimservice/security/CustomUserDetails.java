package com.vanguard.claimservice.security;

public class CustomUserDetails {
    private final String userId;
    private final String username;
    private final String role;

    public CustomUserDetails(String userId, String username, String role) {
        this.userId = userId;
        this.username = username;
        this.role = role;
    }

    public String getUserId() { return userId; }
    public String getUsername() { return username; }
    public String getRole() { return role; }
}
