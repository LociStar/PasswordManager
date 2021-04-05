package com.passswordmanager.Database;

import com.passswordmanager.Datatypes.Password;
import com.passswordmanager.Datatypes.Program;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

//
public class DatabaseConnectionHandler {
    private final String url = "jdbc:h2:file:C:/data/passwordManager;mode=MySQL";
    private final String user = "sa";

    private Connection con;
    private Statement st;

    public DatabaseConnectionHandler(String password) {
        createDB(password);
    }

    public void createDB(String password) {
        String query = "ALTER USER sa SET PASSWORD '" + password + "'";
        String createTable = "CREATE TABLE ProgramName\n" +
                "(name varchar(255) NOT NULL ,\n" +
                " nickname varchar(255) ,\n " +
                "PRIMARY KEY (name));";
        String crateTablePasswords = "CREATE TABLE Password\n" +
                "(\n" +
                " id       int AUTO_INCREMENT ,\n" +
                " username varchar(255) ,\n" +
                " pw       varchar(255) NOT NULL ,\n" +
                " pName     varchar(255) NOT NULL ,\n" +
                "\n" +
                "PRIMARY KEY (id),\n" +
                "CONSTRAINT FK FOREIGN KEY (pName) REFERENCES ProgramName (name)\n" +
                ");";
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
            st.execute(crateTablePasswords);
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

    public boolean insert(String username, String pw, String programName, String nickname) {
        try {

            st.execute("INSERT INTO ProgramName (name, nickname) VALUES('" + programName + "', '" + nickname + "')\n " +
                    "ON DUPLICATE KEY UPDATE nickname=nickname;");
            st.execute("INSERT INTO Password (username, pw, pName) VALUES ('" + username + "', '" + pw + "', '" + programName + "' )\n" +
                    "ON DUPLICATE KEY UPDATE pw=pw;");
            return true;
        } catch (SQLException sqlException) {
            System.out.println("Insert Error: " + sqlException.getMessage());
            return false;
        }
    }

    public boolean delete(String username, String programName) {
        try {
            st.execute("DELETE FROM Password WHERE username='" + username + "' AND WHERE pName='" + programName + "';");
            return true;
        } catch (SQLException sqlException) {
            System.out.println("Delete Error: " + sqlException.getErrorCode());
            return false;
        }
    }

    public Map<String, String> getProgramNames() {
        try {
            ResultSet rs = st.executeQuery("SELECT name, nickname FROM ProgramName;");
            Map<String, String> map = new HashMap<>();
            while (rs.next()) {
                //put one row into map
                map.put(rs.getString("name"), rs.getString("nickname"));
            }
            return map;
        } catch (SQLException sqlException) {
            System.out.println("Select Error: " + sqlException.getMessage());
        }
        return new HashMap<>();
    }

    public Map<String, String> getUsrPwNames(String programName) {
        try {
            ResultSet rs = st.executeQuery("SELECT username, pw FROM Password WHERE pName='" + programName + "';");
            Map<String, String> map = new HashMap<>();
            while (rs.next()) {
                //put one row into map
                map.put(rs.getString("username"), rs.getString("pw"));
            }
            return map;
        } catch (SQLException sqlException) {
            System.out.println("Select Error: " + sqlException.getMessage());
        }
        return new HashMap<>();
    }

    public Map<String, String> selectAll() {
        try {
            ResultSet rs = st.executeQuery("SELECT * FROM Password;");
            Map<String, String> map = new HashMap<>();
            while (rs.next()) {
                //put one row into map
                map.put(rs.getString("username"), rs.getString("pw"));
            }
            return map;
        } catch (SQLException sqlException) {
            System.out.println("Select Error: " + sqlException.getErrorCode());
        }
        return new HashMap<>();
    }

    public Program getPassword(String pName) {
        List<Password> passwords = new ArrayList<>();
        try {
            ResultSet rs = st.executeQuery("SELECT pw FROM Password WHERE pName='" + pName + "';");
            while (rs.next()) {
                //add Passwords to List
                passwords.add(new Password(rs.getString("name"), rs.getString("pw")));
            }
        } catch (SQLException sqlException) {
            System.out.println("Select Error (Entry not found): " + sqlException.getMessage());
        }
        return new Program(pName, passwords);
    }
}