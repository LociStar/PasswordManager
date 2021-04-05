package com.passswordmanager.Datatypes;

import java.util.ArrayList;
import java.util.List;

/**
 * Representation of a program
 */
public class Program {

    private String title;
    private String nickname;
    private List<Password> passwords;

    public Program(String title, List<Password> passwords) {
        this.title = title;
        this.passwords = passwords;
    }

    public Program(String title) {
        this.title = title;
        this.passwords = new ArrayList<Password>();
    }

    public Program() {
        this.title = "";
        this.passwords = new ArrayList<Password>();
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public List<Password> getPasswords() {
        return passwords;
    }

    public void setPasswords(List<Password> passwords) {
        this.passwords = passwords;
    }
}
