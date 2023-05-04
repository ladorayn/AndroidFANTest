package com.lado.fanmobiletest.model;

public class User {

    private String name;
    private String email;

    public boolean isEmailVerified() {
        return isEmailVerified;
    }

    public void setEmailVerified(boolean emailVerified) {
        isEmailVerified = emailVerified;
    }

    public User(String name, String email, boolean isEmailVerified) {
        this.name = name;
        this.email = email;
        this.isEmailVerified = isEmailVerified;
    }

    private boolean isEmailVerified;

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public User() {

    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
