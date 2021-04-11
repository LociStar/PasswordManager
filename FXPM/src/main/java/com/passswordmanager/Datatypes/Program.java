package com.passswordmanager.Datatypes;

import java.util.ArrayList;
import java.util.List;

/**
 * Representation of a program
 */
public class Program {

    private String title;
    private String nickname;
    private List<Account> accounts;

    public Program(String title, String nickname, List<Account> accounts) {
        this.title = title;
        this.nickname = nickname;
        this.accounts = accounts;
    }

    public Program(String title, List<Account> accounts) {
        this.title = title;
        this.nickname = "";
        this.accounts = accounts;
    }

    public Program(String title) {
        this.title = title;
        this.accounts = new ArrayList<>();
    }

    public Program() {
        this.title = "";
        this.accounts = new ArrayList<>();
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public List<Account> getPasswords() {
        return accounts;
    }

    public void setPasswords(List<Account> accounts) {
        this.accounts = accounts;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }
}
