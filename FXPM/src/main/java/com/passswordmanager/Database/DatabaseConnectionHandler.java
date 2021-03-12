package com.passswordmanager.Database;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DatabaseConnectionHandler {
    private final String url = "jdbc:h2:file:C:/data/passwordManager";
    private final String user = "sa";

    private Connection con;
    private Statement st;

    public DatabaseConnectionHandler(String password) {
        createDB(password);
    }

    public void createDB(String password) {
        String query = "ALTER USER sa SET PASSWORD '" + password + "'";
        String createTable = "create table pm\n" +
                "(name VARCHAR(255) PRIMARY KEY, pw VARCHAR(255));";
        try {
            this.con = DriverManager.getConnection(url, user, "");
            this.st = con.createStatement();
        } catch (SQLException ignored) {
            System.out.println("Database already exists");
            connect(password);
            System.out.println("connected");
            return;
        }
        try {
            assert st != null;
            st.execute(query);
            st.execute(createTable);
        } catch (Exception throwables) {
            throwables.printStackTrace();
        }
    }

    public void connect(String passwd) {
        try {
            this.con = DriverManager.getConnection(url, user, passwd);
            this.st = con.createStatement();
        } catch (SQLException ignored) {

        }
    }

    public boolean insert(String dbName, String name, String pw) {
        try {
            st.execute("INSERT INTO " + dbName + " VALUES('" + name + "', '" + pw + "');");
            return true;
        } catch (SQLException sqlException) {
            System.out.println("Insert Error: "+ sqlException.getErrorCode());
            return false;
        }
    }

    public boolean insert(String name, String pw) {
        return insert("pm", name, pw);
    }

    public boolean delete(String dbName, String column, String name) {
        try {
            st.execute("DELETE FROM " + dbName + " WHERE " + column + " = '" + name + "';");
            return true;
        } catch (SQLException sqlException) {
            System.out.println("Delete Error: " + sqlException.getErrorCode());
            return false;
        }
    }

    public boolean delete(String name) {
        return delete("pm", "name", name);
    }

    public List<String> selectAll() {
        try {
            ResultSet rs = st.executeQuery("SELECT * FROM pm; ");
            ResultSetMetaData rsmd = rs.getMetaData();
            int columnsNumber = rsmd.getColumnCount();
            List<String> list = new ArrayList<>();
            while (rs.next()) {
                StringBuilder stringBuilder = new StringBuilder();
                //Print one row
                for (int i = 1; i <= columnsNumber; i++) {
                    //Print one element of a row
                    stringBuilder.append(rs.getString(i)).append(" ");
                }
                list.add(stringBuilder.toString());
            }
            return list;
        } catch (SQLException sqlException) {
            System.out.println("Select Error: " + sqlException.getErrorCode());
        }
        return new ArrayList<>();
    }
}