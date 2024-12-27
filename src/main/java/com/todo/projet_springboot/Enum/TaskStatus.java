package com.todo.projet_springboot.Enum;

public enum TaskStatus {
    EN_COURS("En cours"),
    TERMINE("Terminé"),
    QA("QA");


    private final String displayValue;

    TaskStatus(String displayValue) {
        this.displayValue = displayValue;
    }

    public String getDisplayValue() {
        return displayValue;
    }
}
