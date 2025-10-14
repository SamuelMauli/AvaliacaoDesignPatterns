package com.bank.model;

import java.util.UUID;

public class User {
    private UUID id;
    private String username;
    private String password;
    private String name;
    private String email;

    public User(UUID id, String username, String password, String name, String email) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.name = name;
        this.email = email;
    }

    // Getters
    public UUID getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    // Setters (if needed, for simplicity, we might omit some)
    public void setUsername(String username) {
        this.username = username;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @Override
    public String toString() {
        return "User{" +
               "id=" + id +
               ", username=\"" + username + "\"" +
               ", name=\"" + name + "\"" +
               ", email=\"" + email + "\"" +
               "}";
    }
}

