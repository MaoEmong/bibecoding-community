package com.example.bibecoding01.employee;

public enum EmployeeStatus {
    EMPLOYED("재직"),
    LEAVE("휴직"),
    RESIGNED("퇴사");

    private final String label;

    EmployeeStatus(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}
