package com.example.bibecoding01.auth;

public record SessionUser(
        Long id,
        String name,
        String email,
        UserRole role
) {

    public boolean isAdmin() {
        return role == UserRole.ADMIN;
    }
}
