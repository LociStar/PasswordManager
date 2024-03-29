package com.passswordmanager.Datatypes;

/**
 * Representation of a password
 */
public class Account {
    private String username;
    private String password;

    public Account() {
        new Account("", "");
    }

    public Account(String username, String password) {
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

    public boolean equals(Account account) {
        return (account.getUsername().equals(this.username) && account.getPassword().equals(this.password));
    }
}
