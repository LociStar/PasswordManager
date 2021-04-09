package com.passswordmanager.Datatypes;

import java.util.ArrayList;
import java.util.List;

/**
 * Representation of a program
 */
public class Entry {

    private String title;
    private String nickname;
    private List<Password> passwords;

    public Entry(String title, String nickname, List<Password> passwords) {
        this.title = title;
        this.nickname = nickname;
        this.passwords = passwords;
    }

    public Entry(String title, List<Password> passwords) {
        this.title = title;
        this.nickname = "";
        this.passwords = passwords;
    }

    public Entry(String title) {
        this.title = title;
        this.passwords = new ArrayList<Password>();
    }

    public Entry() {
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

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }
}