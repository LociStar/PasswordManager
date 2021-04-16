package com.passswordmanager.Controllers;

import com.lambdaworks.crypto.SCryptUtil;
import com.passswordmanager.Util.Config;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Properties;

public class InstallUIController {
    @FXML
    private TextField databasePathField;

    @FXML
    private TextField resetTimeField;

    @FXML
    private TextField maxTitleLengthField;

    @FXML
    private TextField masterPasswordField;

    @FXML
    private TextField masterPasswordFieldConfirm;

    @FXML
    public void onCancelPressed() {
        closeStage();
    }

    private void closeStage() {
        Platform.exit();
        System.exit(0);
    }

    @FXML
    public void onInstallPressed() {
        if (!masterPasswordField.getText().equals(masterPasswordFieldConfirm.getText())) return;
        String hash = SCryptUtil.scrypt(masterPasswordFieldConfirm.getText(), 8, 8, 8);
        System.out.println("Hash:");
        System.out.println(hash);

        boolean matched = SCryptUtil.check(masterPasswordFieldConfirm.getText(), hash);
        System.out.println("Must be True: " + matched);
        if (!matched) closeStage();

        Config config = new Config(hash, databasePathField.getText(), Long.parseLong(resetTimeField.getText()), Integer.parseInt(maxTitleLengthField.getText()));
        config.writeConfig();
        try (OutputStream output = new FileOutputStream(new File(".").getCanonicalPath() + "\\config.txt")) {

            Properties prop = new Properties();

            // set the properties value
            prop.setProperty("installed", "true");

            // save properties to project root folder
            prop.store(output, null);

        } catch (IOException io) {
            io.printStackTrace();
        }
        closeStage();
    }

    public void setDatabasePath(String databasePath) {
        this.databasePathField.setText(databasePath);
    }
}
