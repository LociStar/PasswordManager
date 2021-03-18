package com.passswordmanager.Controllers;

import com.passswordmanager.Database.DatabaseConnectionHandler;
import com.passswordmanager.Util.FileCrypt;
import com.lambdaworks.crypto.SCryptUtil;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.PasswordField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;
import javafx.stage.Window;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.security.Key;
import java.util.HashMap;
import java.util.Map;

/**
 * FXML Controller to control loginPage.fxml
 */
public class LoginPageController {
    @FXML
    public PasswordField masterPassword;

    private PasswordListController passwordListController;
    private Stage passwordStage;
    private Parent parent;

    /**
     * Handles the event, if the unlock button is pressed.
     * Changes Scene, if password does match.
     *
     * @throws IOException Input exception
     */
    public void onUnlockPressed() throws IOException {
        //the hashed master password
        String hash = "$s0$30808$EIjYo1QSYopS4FBUoAJgBw==$Alr+ZkCNpNxnAA2R4PCAYzfSSMF3oj47tpSrad7OA0w=";
        boolean matched = SCryptUtil.check(masterPassword.getText(), hash);
        if (matched) {
            Scene scene = masterPassword.getScene();
            Window window = scene.getWindow();
            Stage stage = (Stage) window;
            stage.hide();
            passwordListController.setDb(new DatabaseConnectionHandler(masterPassword.getText()));
            passwordListController.loadTable(masterPassword.getText());
            stage = passwordStage;
            stage.show();
        }

    }

    /**
     * On X clicked, hide the scene
     */
    public void onXClicked() {
        Stage stage = (Stage) masterPassword.getScene().getWindow();
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
}
