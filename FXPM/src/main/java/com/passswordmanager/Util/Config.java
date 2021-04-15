package com.passswordmanager.Util;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Properties;

public class Config {
    private String masterPassword;
    private long delay;
    private String databasePath;
    private int MAX_TITLE_LENGTH;

    public Config(String configPath) {
        try {
            readConfig();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void readConfig() throws IOException {
        InputStream inputStream = null;
        try {
            Properties prop = new Properties();
            String propFileName = "config.properties";
            inputStream = getClass().getClassLoader().getResourceAsStream(propFileName);//new FileInputStream(Config.class.getResource("/config.properties").getPath());
            System.out.println(Config.class.getResource("/config.properties").getPath());
            prop.load(inputStream);
            System.out.println(prop);

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
        try (OutputStream output = new FileOutputStream(Config.class.getResource("/config.properties").getPath())) {

            Properties prop = new Properties();

            // set the properties value
            prop.setProperty("masterPassword", "" + masterPassword + "");
            prop.setProperty("databasePath", "" + databasePath + "");
            prop.setProperty("pwResetTime", delay + "");
            prop.setProperty("MAX_TITLE_LENGTH", MAX_TITLE_LENGTH + "");

            // save properties to project root folder
            prop.store(output, null);

            System.out.println(Config.class.getResource("/config.properties").getPath());

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
