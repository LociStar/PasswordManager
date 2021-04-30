package com.passswordmanager.Datatypes;

import java.util.ArrayList;
import java.util.List;

/**
 * Representation of a program
 */
public class Program {

    private String title;
    private String nickname;
    private String url;
    private List<Account> accounts;

    public Program(String title, String nickname, String url, List<Account> accounts) {
        this.title = title;
        this.nickname = nickname;
        this.accounts = accounts;
        this.url = url;
    }

    public Program(String title, String nickname, List<Account> accounts) {
        this(title, nickname, "", accounts);
    }

    public Program(String title, List<Account> accounts) {
        this(title, "", accounts);
    }

    public Program(String title, String url) {
        this(title, "", url, new ArrayList<>());
    }

    public Program(String title) {
        this(title, new ArrayList<>());
    }

    public Program() {
        this("");
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

    public void appendAccount(Account newAccount) {
        for (Account a : accounts) {
            if (a.equals(newAccount)) {
                return;
            }
        }
        accounts.add(newAccount);
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
