package com.example.bibecoding01.auth;

public enum UserRole {
    ADMIN("관리자"),
    EMPLOYEE("사원");

    private final String label;

    UserRole(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}
