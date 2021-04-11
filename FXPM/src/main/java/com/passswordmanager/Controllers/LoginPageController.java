package com.passswordmanager.Controllers;

import com.lambdaworks.crypto.SCryptUtil;
import com.passswordmanager.Database.DatabaseConnectionHandler;
import com.passswordmanager.Datatypes.MasterPassword;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.PasswordField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;
import javafx.stage.Window;

import java.io.IOException;

/**
 * FXML Controller to control loginPage.fxml
 */
public class LoginPageController {
    @FXML
    public PasswordField passwordField;

    private PasswordListController passwordListController;
    private Stage passwordStage;
    private Parent parent;
    private MasterPassword masterPassword;
    private boolean locked = true;

    /**
     * Handles the event, if the unlock button is pressed.
     * Changes Scene, if password does match.
     *
     * @throws IOException Input exception
     */
    public void onUnlockPressed() throws IOException {
        //the hashed master password
        String hash = "$s0$30808$EIjYo1QSYopS4FBUoAJgBw==$Alr+ZkCNpNxnAA2R4PCAYzfSSMF3oj47tpSrad7OA0w=";
        boolean matched = SCryptUtil.check(passwordField.getText(), hash);
        if (matched) {
            char[] password = passwordField.getText().toCharArray();
            System.out.println(password);
            this.masterPassword = new MasterPassword(password);
            System.out.println(masterPassword.getPassword());
            masterPassword.clearChar(password);
            //change Scene
            Scene scene = passwordField.getScene();
            Window window = scene.getWindow();
            Stage stage = (Stage) window;
            stage.hide();
            passwordField.clear();
            passwordListController.setDb(new DatabaseConnectionHandler(masterPassword.getPassword()));
            passwordListController.loadTable(masterPassword.getPassword());
            passwordListController.setMasterPassword(this.masterPassword);
            masterPassword.clearPasswordCache();
            stage = passwordStage;
            this.locked = false;
            stage.show();
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
     * Gets the password Stage
     *
     * @return password stage
     */
    public Stage getPasswordStage() {
        return passwordStage;
    }

    /**
     * Sets the password stage
     *
     * @param passwordStage password stage
     */
    public void setPasswordStage(Stage passwordStage) {
        this.passwordStage = passwordStage;
    }

    public void onKeyPressed(KeyEvent keyEvent) throws IOException {
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
}
