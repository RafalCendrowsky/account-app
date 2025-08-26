package com.rafalcendrowski.accountapp.model.user;

public enum UserRole {
    USER,
    ADMINISTRATOR,
    ACCOUNTANT,
    AUDITOR;


    @Override
    public String toString() {
        return "ROLE_".concat(super.toString());
    }
}