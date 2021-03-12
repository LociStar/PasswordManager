package com.passswordmanager.TestMain;

import com.passswordmanager.Database.DatabaseConnectionHandler;

public class DBConnectionTest {
    public static void main(String[] args) {
        DatabaseConnectionHandler connectionHandler = new DatabaseConnectionHandler("test");
        boolean insert1 = connectionHandler.insert("test@test.de", "1234");
        boolean insert = connectionHandler.insert("test@web.de", "123");
        System.out.println(connectionHandler.selectAll());
        boolean delete = connectionHandler.delete("test@web.de");
        System.out.println(connectionHandler.selectAll());
        System.out.println(insert + " " + delete);
    }
}
