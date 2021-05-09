package com.passswordmanager.Controllers;

import com.passswordmanager.Database.DatabaseConnectionHandler;
import com.passswordmanager.Datatypes.MasterPassword;
import com.passswordmanager.Util.FileCrypt;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.stage.Stage;

public class AddEntryDialogController {
    @FXML
    public Label description;

    @FXML
    public Button button;

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

    private String oldNickname;

    @FXML
    void btnAddClicked(ActionEvent event) {
        if (!(password.getText().equals("") && pName.getText().equals(""))) {
            if (db.isValidNickname(nickname.getText(), oldNickname)) {
                FileCrypt.addPwToDatabase(username.getText(), password.getText(), masterPassword.getPassword(), pName.getText(), nickname.getText(), db);
                masterPassword.clearPasswordCache();
                closeStage(event);
            } else {
                nickname.setStyle("-fx-background-color: red");
                nickname.setTooltip(new Tooltip("Nickname must be unique and cant be a ProgramName"));
                nickname.getTooltip().show(nickname.getScene().getWindow());
            }
        }

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

    public void setUsername(String username) {
        this.username.setText(username);
    }

    public void setPassword(String password) {
        this.password.setText(password);
    }

    public void setDescription(String text) {
        this.description.setText(text);
    }

    public TextField getpName() {
        return pName;
    }

    public TextField getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname.setText(nickname);
    }

    public Button getButton() {
        return button;
    }

    public String getUsernameText() {
        return username.getText();
    }

    public String getPasswordText() {
        return password.getText();
    }

    public void setOldNickname(String oldNickname) {
        this.oldNickname = oldNickname;
    }
}

