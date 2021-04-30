package com.passswordmanager.Database;

import com.passswordmanager.Datatypes.Account;
import com.passswordmanager.Datatypes.MasterPassword;
import com.passswordmanager.Datatypes.Program;
import com.passswordmanager.Util.FileCrypt;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class DatabaseConnectionHandler {
    private final String user = "admin";
    private String url = "jdbc:h2:file:C:/data/passwordManager;mode=MySQL";
    private Connection con;
    private Statement st;

    public DatabaseConnectionHandler(String password, String databasePath) {
        if (!databasePath.equals("")) url = "jdbc:h2:file:" + databasePath + "/passwordManager;mode=MySQL";
        createDB(password);
    }

    public void createDB(String password) {
        String query = "ALTER USER admin SET PASSWORD '" + password + "'";
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
                "ON DELETE CASCADE \n" +
                "ON UPDATE CASCADE);";
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
        } catch (SQLException error) {
            error.printStackTrace();
        }
    }

    public boolean insert(String username, String pw, String programName, String nickname) {
        //filter
        programName = filter(programName);
        nickname = filter(nickname);

        nickname = nickname.equals("") ? programName : nickname;

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
        programName = filter(programName);
        try {
            st.execute("DELETE FROM Password WHERE username='" + username + "' AND pName='" + programName + "';");
            return true;
        } catch (SQLException sqlException) {
            System.out.println("Delete Error: " + sqlException.getErrorCode());
            return false;
        }
    }

    public void deleteEmptyPrograms() {
        try {
            st.execute("DELETE FROM ProgramName WHERE name NOT IN (SELECT pName FROM Password WHERE pName is NOT NULL);");
        } catch (SQLException sqlException) {
            System.out.println("Delete Error: " + sqlException.getErrorCode());
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
        programName = filter(programName);
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

    public Account getPassword(String username, String nickname) {
        nickname = filter(nickname);
        Program program = new Program();
        program.setNickname(nickname);
        Account account = new Account(username, "");
        try {
            ResultSet resultSet = st.executeQuery("SELECT name FROM ProgramName WHERE nickname='" + nickname + "';");
            while (resultSet.next()) {
                //add Passwords to List
                program.setTitle(resultSet.getString("name"));
            }
            ResultSet rs = st.executeQuery("SELECT pw FROM Password WHERE username='" + username + "' AND pName='" + program.getTitle() + "';");
            while (rs.next()) {
                //add Passwords
                account.setPassword(rs.getString("pw"));
            }
        } catch (SQLException sqlException) {
            System.out.println("Select Error (Entry not found): " + sqlException.getMessage());
        }
        return account;
    }

    public Program getPasswords(String pName) {
        pName = filter(pName);
        List<Account> accounts = new ArrayList<>();
        try {
            ResultSet rs = st.executeQuery("SELECT username, pw FROM Password WHERE pName='" + pName + "';");
            while (rs.next()) {
                //add Passwords to List
                accounts.add(new Account(rs.getString("username"), rs.getString("pw")));
            }
        } catch (SQLException sqlException) {
            System.out.println("Select Error (Entry not found): " + sqlException.getMessage());
        }
        return new Program(pName, accounts);
    }

    public String getNickname(String pName) {
        String pNameFiltered = filter(pName);
        String nickname = "";
        try {
            ResultSet rs = st.executeQuery("SELECT nickname FROM ProgramName WHERE name='" + pNameFiltered + "';");
            while (rs.next()) {
                //add Passwords to List
                nickname = rs.getString("nickname");
            }
        } catch (SQLException sqlException) {
            System.out.println("Select Error (Entry not found): " + sqlException.getMessage());
        }
        return nickname.equals("") ? pName : nickname;
    }

    public String getPName(String nickname) {
        String nicknameFiltered = filter(nickname);
        String pName = "";
        try {
            ResultSet rs = st.executeQuery("SELECT name FROM ProgramName WHERE nickname='" + nicknameFiltered + "';");
            while (rs.next()) {
                //add Passwords to List
                pName = rs.getString("name");
            }
        } catch (SQLException sqlException) {
            System.out.println("Select Error (Entry not found): " + sqlException.getMessage());
        }
        return pName.equals("") ? nickname : pName;
    }

    public void updateEntry(String pName, String oldUsername, String newUsername, String password) {
        pName = filter(pName);
        try {
            if (password == null)
                st.execute("UPDATE Password " +
                        "SET usernaem='" + newUsername + "', " +
                        "password='" + password + "' " +
                        "WHERE username='" + oldUsername + "' AND pName='" + pName + "';");
            else
                st.execute("UPDATE Password " +
                        "SET username='" + newUsername + "'" +
                        "WHERE username='" + oldUsername + "' AND pName='" + pName + "';");
        } catch (SQLException sqlException) {
            System.out.println("Select Error (Entry not found): " + sqlException.getMessage());
        }
    }

    public boolean isValidNickname(String nickname, String oldNickname) {
        nickname = filter(nickname);
        if (oldNickname != null)
            oldNickname = filter(oldNickname);
        if (nickname.equals(oldNickname)) return true;
        if (nickname.equals("")) return true;
        try {
            ResultSet rs = st.executeQuery("SELECT name FROM ProgramName WHERE name='" + nickname + "';");
            if (rs.next()) {
                System.out.println("nickname cannot be a ProgramName");
                return false;
            }
            ResultSet rs2 = st.executeQuery("SELECT name FROM ProgramName WHERE nickname='" + nickname + "';");
            if (rs2.next()) {
                System.out.println("Nickname already exits");
                return false;
            }

        } catch (SQLException sqlException) {
            System.out.println("Select Error: " + sqlException.getMessage());
        }
        return true;
    }

    public boolean isValidProgramName(String oldName, String pName) {
        pName = filter(pName);
        oldName = filter(oldName);
        if (oldName.equals(pName)) return true;
        if (pName.equals("")) return false;
        try {
            ResultSet rs = st.executeQuery("SELECT name FROM ProgramName WHERE name='" + pName + "';");
            if (rs.next()) {
                System.out.println("ProgramName already exists");
                return false;
            }
            ResultSet rs2 = st.executeQuery("SELECT name FROM ProgramName WHERE nickname='" + pName + "';");
            if (rs2.next()) {
                System.out.println("ProgramName cant be a Nickname");
                return false;
            }
        } catch (SQLException sqlException) {
            System.out.println("Select Error: " + sqlException.getMessage());
        }
        return true;
    }

    public void setKeyBehaviour(String behaviour) {
        try {
            //for backwards compatibility
            st.execute("ALTER TABLE ProgramName ADD COLUMN IF NOT EXISTS keyBehaviour varchar(255) NOT NULL DEFAULT 'USERNAME+TAB+PASSWORD';");

            st.execute("UPDATE ProgramName SET keyBehaviour='" + behaviour + "';");
        } catch (SQLException sqlException) {
            System.out.println("Update Error: " + sqlException.getMessage());
        }
    }

    public String getKeyBehaviour(String pName) {
        pName = filter(pName);
        try {
            //for backwards compatibility
            st.execute("ALTER TABLE ProgramName ADD COLUMN IF NOT EXISTS keyBehaviour varchar(255) NOT NULL DEFAULT 'USERNAME+TAB+PASSWORD';");

            ResultSet rs = st.executeQuery("SELECT keyBehaviour FROM ProgramName WHERE name='" + pName + "';");
            while (rs.next()) {
                //add Passwords to List
                pName = rs.getString("keyBehaviour");
            }
            return pName;
        } catch (SQLException sqlException) {
            System.out.println("Select Error (Entry not found): " + sqlException.getMessage());
        }
        return "USERNAME+TAB+PASSWORD";
    }

    public void updateProgram(String oldName, String newName, String newNickname, String behaviour) {
        oldName = filter(oldName);
        newName = filter(newName);
        newNickname = filter(newNickname);
        try {
            //for backwards compatibility
            st.execute("UPDATE ProgramName " +
                    "SET name='" + newName + "', " +
                    "nickname='" + newNickname + "', " +
                    "keyBehaviour='" + behaviour + "'" +
                    "WHERE name='" + oldName + "';");
        } catch (SQLException sqlException) {
            System.out.println("Update Error: " + sqlException.getMessage());
        }
    }

    public void closeConnection() {
        try {
            st.close();
            con.close();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }


    }

    public String filter(String name) {
        return name.replaceAll("'", "''");
    }

    public void changeMP(MasterPassword oldMasterPassword, MasterPassword newMasterPassword) {
        try {
            st.execute("ALTER USER admin SET PASSWORD '" + newMasterPassword.getPassword() + "'");
            ResultSet rs = st.executeQuery("SELECT username, pw FROM Password;");
            Statement st2 = con.createStatement();
            while (rs.next()) {
                st2.execute("UPDATE Password " +
                        "SET pw='" + FileCrypt.encryptText(FileCrypt.decryptText(rs.getString("pw"), oldMasterPassword.getPassword()), newMasterPassword.getPassword()) + "' " +
                        "WHERE username='" + rs.getString("username") + "';");
            }
            newMasterPassword.clearPasswordCache();
            oldMasterPassword.clearPasswordCache();
        } catch (SQLException sqlException) {
            System.out.println("Update Error: " + sqlException.getMessage());
        }
    }

    public void addURL(String programName, String url) {
        programName = filter(programName);
        try {
            st.execute("ALTER TABLE ProgramName ADD COLUMN IF NOT EXISTS url varchar(255);");
            st.execute("UPDATE ProgramName " +
                    "SET url='" + url + "'" +
                    "WHERE name='" + programName + "';");
        } catch (SQLException sqlException) {
            System.out.println("Update Error: " + sqlException.getMessage());
        }
    }
}