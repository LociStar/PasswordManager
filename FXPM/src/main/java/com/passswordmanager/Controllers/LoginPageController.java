package com.passswordmanager.Controllers;

import com.lambdaworks.crypto.SCryptUtil;
import com.passswordmanager.Database.DatabaseConnectionHandler;
import com.passswordmanager.Datatypes.MasterPassword;
import com.passswordmanager.StartInBackground;
import com.passswordmanager.Util.Config;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.PasswordField;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;
import javafx.stage.Window;

/**
 * FXML Controller to control loginPage.fxml
 */
public class LoginPageController {
    private final Config config = new Config();
    @FXML
    public PasswordField passwordField;
    private PasswordListController passwordListController;
    private Stage passwordStage;
    private MasterPassword masterPassword;
    private boolean locked = true;
    private DatabaseConnectionHandler db;

    /**
     * Handles the event, if the unlock button is pressed.
     * Changes Scene, if password does match.
     */
    public void onUnlockPressed() {
        //the hashed master password
        String hash = config.getMasterPassword();
        boolean matched = SCryptUtil.check(passwordField.getText(), hash);
        if (matched) {
            char[] password = passwordField.getText().toCharArray();
            this.masterPassword = new MasterPassword(password);
            masterPassword.clearChar(password);
            //change Scene
            Scene scene = passwordField.getScene();
            Window window = scene.getWindow();
            Stage stage = (Stage) window;
            stage.hide();
            passwordField.clear();
            this.db = new DatabaseConnectionHandler(masterPassword.getPassword(), config.getDatabasePath());
            passwordListController.setDb(this.db);
            passwordListController.loadTable(masterPassword.getPassword());
            passwordListController.setMasterPassword(this.masterPassword);
            masterPassword.clearPasswordCache();
            stage = passwordStage;
            stage.setTitle("PasswordManager");
            stage.getIcons().add(new Image(StartInBackground.class.getResourceAsStream("/icon.png")));
            this.locked = false;
            stage.show();
        } else {
            System.out.println(hash);
            System.out.println("Incorrect Password");
        }

    }

    /**
     * On X clicked, hide the scene
     */
    public void onXClicked() {
        Stage stage = (Stage) passwordField.getScene().getWindow();
        stage.hide();
    }

    /**
     * Returns the password controller
     *
     * @return password controller
     */
    public PasswordListController getPasswordListController() {
        return passwordListController;
    }

    /**
     * Sets the password controller
     *
     * @param passwordListController password controller
     */
    public void setPasswordListController(PasswordListController passwordListController) {
        this.passwordListController = passwordListController;
    }

    /**
     * Sets the password stage
     *
     * @param passwordStage password stage
     */
    public void setPasswordStage(Stage passwordStage) {
        this.passwordStage = passwordStage;
    }

    public void onKeyPressed(KeyEvent keyEvent) {
        if (keyEvent.getCode().equals(KeyCode.ENTER)) {
            onUnlockPressed();
        }
    }

    public MasterPassword getMasterPassword() {
        return this.masterPassword;
    }

    public void resetPassword() {
        this.locked = true;
        if (this.masterPassword != null) {
            this.masterPassword.clearPasswordCache();
            this.masterPassword.clearGuardedString();
        }
        this.masterPassword = null;
        passwordListController.resetPassword();
    }

    public boolean isLocked() {
        return locked;
    }

    public DatabaseConnectionHandler getDb() {
        return db;
    }
}
