package com.passswordmanager.Datatypes;

/**
 * Representation of a password
 */
public class Password {
    private String username;
    private String password;

    public Password() {
        new Password("", "");
    }

    public Password(String username, String password) {
        this.username = username;
        this.password = password;
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
