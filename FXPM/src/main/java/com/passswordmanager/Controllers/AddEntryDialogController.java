package com.passswordmanager.Controllers;

import com.passswordmanager.Database.DatabaseConnectionHandler;
import com.passswordmanager.Util.FileCrypt;
import com.sun.jna.Native;
import com.sun.jna.platform.win32.User32;
import com.sun.jna.platform.win32.WinDef;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class AddEntryDialogController {
    private static final int MAX_TITLE_LENGTH = 1024;
    @FXML
    private TextField pName;

    @FXML
    private TextField nickname;

    @FXML
    private TextField username;

    @FXML
    private PasswordField password;

    private PasswordListController passwordListController;

    private DatabaseConnectionHandler db;

    @FXML
    void btnAddClicked(ActionEvent event) {
        System.out.println(passwordListController.getLoginPageController().masterPassword.getText());
        FileCrypt.addPwToDatabase(username.getText(), password.getText(), passwordListController.getLoginPageController().masterPassword.getText(), pName.getText(), nickname.getText(), db);
        passwordListController.loadTable(passwordListController.getLoginPageController().masterPassword.getText());

        closeStage(event);
    }

    private void closeStage(ActionEvent event) {
        Node source = (Node) event.getSource();
        Stage stage = (Stage) source.getScene().getWindow();
        stage.close();
    }

    public void setPasswordListController(PasswordListController passwordListController) {
        this.passwordListController = passwordListController;
    }

    public void setDb(DatabaseConnectionHandler db) {
        this.db = db;
    }

    @FXML
    public void onRandomPressed() {
        password.setText(FileCrypt.generatePassword());
    }
}

