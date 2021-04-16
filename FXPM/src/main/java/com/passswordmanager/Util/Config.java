package com.passswordmanager.Util;

import java.io.*;
import java.util.Properties;

public class Config {
    private String masterPassword;
    private long delay;
    private String databasePath;
    private int MAX_TITLE_LENGTH;
    private final String configPath = System.getenv("APPDATA") + "\\PasswordManager\\config.properties";

    public Config() {
        try {
            readConfig();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Config(String masterPasswordHashed, String databasePath, long delay, int maxTitleLength) {
        try {
            createConfig(masterPasswordHashed, databasePath, delay, maxTitleLength);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void createConfig(String masterPasswordHashed, String databasePath, long delay, int maxTitleLength) throws IOException {
        File config = new File(configPath);
        config.getParentFile().mkdir();
        if (config.createNewFile()) {
            //FileOutputStream configOutputStream = new FileOutputStream(config, false);
            this.masterPassword = masterPasswordHashed;
            this.delay = delay;
            this.databasePath = databasePath;
            this.MAX_TITLE_LENGTH = maxTitleLength;
            try (OutputStream output = new FileOutputStream(config, false)) {
                output.write(1);
            } catch (IOException io) {
                io.printStackTrace();
            }
            this.writeConfig();
        }


    }

    public void readConfig() throws IOException {
        InputStream inputStream = null;
        try {
            Properties prop = new Properties();
            String propFileName = "config.properties";
            inputStream = new FileInputStream(configPath);
            prop.load(inputStream);

            this.masterPassword = prop.getProperty("masterPassword").replace("\"", "");
            this.databasePath = prop.getProperty("databasePath").replace("\"", "");
            this.delay = Long.parseLong(prop.getProperty("pwResetTime"));
            this.MAX_TITLE_LENGTH = Integer.parseInt(prop.getProperty("MAX_TITLE_LENGTH"));

        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Exception: " + e);
        } finally {
            assert inputStream != null;
            inputStream.close();
        }
    }

    public void writeConfig() {
        try (OutputStream output = new FileOutputStream(configPath)) {

            Properties prop = new Properties();

            // set the properties value
            prop.setProperty("masterPassword", "" + masterPassword + "");
            prop.setProperty("databasePath", "" + databasePath + "");
            prop.setProperty("pwResetTime", delay + "");
            prop.setProperty("MAX_TITLE_LENGTH", MAX_TITLE_LENGTH + "");

            // save properties to project root folder
            prop.store(output, null);

        } catch (IOException io) {
            io.printStackTrace();
        }
    }

    public String getMasterPassword() {
        return masterPassword;
    }

    public long getDelay() {
        return delay;
    }

    public String getDatabasePath() {
        return databasePath;
    }

    public int getMAX_TITLE_LENGTH() {
        return MAX_TITLE_LENGTH;
    }

    public void setMasterPassword(String masterPassword) {
        this.masterPassword = masterPassword;
    }

    public void setDelay(long delay) {
        this.delay = delay;
    }

    public void setDatabasePath(String databasePath) {
        this.databasePath = databasePath;
    }

    public void setMAX_TITLE_LENGTH(int MAX_TITLE_LENGTH) {
        this.MAX_TITLE_LENGTH = MAX_TITLE_LENGTH;
    }
}
