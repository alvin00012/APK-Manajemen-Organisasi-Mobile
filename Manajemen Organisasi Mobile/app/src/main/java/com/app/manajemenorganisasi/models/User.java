package com.app.manajemenorganisasi.models;

public class User {
    String username, password, role_name, role_group;

    public User() {
    }

    public User(String username, String password, String role_group, String role_name) {
        this.username = username;
        this.password = password;
        this.role_name = role_name;
        this.role_group = role_group;
    }

    public String getRole_group() {
        return role_group;
    }

    public void setRole_group(String role_group) {
        this.role_group = role_group;
    }

    public String getRole_name() {
        return role_name;
    }

    public void setRole_name(String role_name) {
        this.role_name = role_name;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
