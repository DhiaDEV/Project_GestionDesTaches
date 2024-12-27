package com.todo.projet_springboot.Enum;

public enum Role {
    MANAGER("MANAGER"),
    DEVELOPER("DEVELOPER");

    private final String displayValue;

    Role(String displayValue) {
        this.displayValue = displayValue;
    }

    public String getDisplayValue() {
        return displayValue;
    }
}