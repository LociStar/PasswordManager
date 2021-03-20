package com.passswordmanager.Datatypes;

/**
 * Representation of a password
 */
public class Password {
    private String name;
    private String password;

    public Password(){
        new Password("", "");
    }

    public Password(String name, String password) {
        this.name = name;
        this.password = password;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
