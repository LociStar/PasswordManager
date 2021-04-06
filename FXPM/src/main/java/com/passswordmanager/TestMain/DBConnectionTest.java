package com.passswordmanager.TestMain;

import com.passswordmanager.Database.DatabaseConnectionHandler;

public class DBConnectionTest {
    public static void main(String[] args) {
        DatabaseConnectionHandler connectionHandler = new DatabaseConnectionHandler("test");
        connectionHandler.insert("username1", "password", "programName", "nickname");
        connectionHandler.insert("username", "password", "anotherProgram", "");
        connectionHandler.insert("username2", "password", "programName", "nickname");
        System.out.println(connectionHandler.getProgramNames());
        System.out.println(connectionHandler.getUsrPwNames("programName"));
    }
}
