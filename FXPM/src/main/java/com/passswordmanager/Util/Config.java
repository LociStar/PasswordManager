package com.passswordmanager.Util;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
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
            inputStream = getClass().getClassLoader().getResourceAsStream(propFileName);

            if (inputStream != null) {
                prop.load(inputStream);
            } else {
                throw new FileNotFoundException("property file '" + propFileName + "' not found in the classpath");
            }

            this.masterPassword = prop.getProperty("masterPassword");
            this.databasePath = prop.getProperty("databasePath");
            this.delay = Long.parseLong(prop.getProperty("pwResetTime"));
            this.MAX_TITLE_LENGTH = Integer.parseInt(prop.getProperty("MAX_TITLE_LENGTH"));

        } catch (Exception e) {
            System.out.println("Exception: " + e);
        } finally {
            assert inputStream != null;
            inputStream.close();
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
}
