package com.passswordmanager.Controllers;

import com.passswordmanager.Database.DatabaseConnectionHandler;
import com.passswordmanager.Datatypes.MasterPassword;
import com.passswordmanager.Util.FileCrypt;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class AddEntryDialogController {
    @FXML
    private TextField pName;

    @FXML
    private TextField nickname;

    @FXML
    private TextField username;

    @FXML
    private PasswordField password;

    private MasterPassword masterPassword;

    private DatabaseConnectionHandler db;

    @FXML
    void btnAddClicked(ActionEvent event) {
        FileCrypt.addPwToDatabase(username.getText(), password.getText(), masterPassword.getPassword(), pName.getText(), nickname.getText(), db);
        masterPassword.clearPasswordCache();

        closeStage(event);
    }

    private void closeStage(ActionEvent event) {
        Node source = (Node) event.getSource();
        Stage stage = (Stage) source.getScene().getWindow();
        stage.close();
    }

    public void setDb(DatabaseConnectionHandler db) {
        this.db = db;
    }

    @FXML
    public void onRandomPressed() {
        password.setText(FileCrypt.generatePassword());
    }

    public void setPNameText(String pName) {
        this.pName.setText(pName);
    }

    public void setMasterPassword(MasterPassword masterPassword) {
        this.masterPassword = masterPassword;
    }
}

