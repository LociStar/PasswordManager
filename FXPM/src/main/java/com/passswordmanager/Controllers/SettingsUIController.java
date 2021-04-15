package com.passswordmanager.Controllers;

import com.passswordmanager.Util.Config;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;

public class SettingsUIController {
    @FXML
    private TextField databasePathField;

    @FXML
    private TextField resetTimeField;

    @FXML
    private TextField maxTitleLengthField;

    @FXML
    private TextField masterPasswordField;

    //private LoginPageController loginPageController;
    //private PasswordListController passwordListController;
    private Config config;

    @FXML
    public void onCancelPressed(ActionEvent actionEvent) {
        closeStage(actionEvent);
    }

    @FXML
    public void onSavedPressed(ActionEvent actionEvent) {
        config.setDatabasePath(databasePathField.getText());
        config.setDelay(Long.parseLong(resetTimeField.getText()));
        config.setMAX_TITLE_LENGTH(Integer.parseInt(maxTitleLengthField.getText()));
        config.writeConfig();
        closeStage(actionEvent);
    }

    private void closeStage(Event event) {
        Node source = (Node) event.getSource();
        Stage stage = (Stage) source.getScene().getWindow();
        stage.close();
    }

    public void loadConfig() {
        try {
            config.readConfig();
        } catch (IOException e) {
            e.printStackTrace();
        }
        databasePathField.setText(config.getDatabasePath());
        resetTimeField.setText(config.getDelay() + "");
        maxTitleLengthField.setText(config.getMAX_TITLE_LENGTH() + "");
        masterPasswordField.setText(config.getMasterPassword());
    }

    public void setConfig(Config config) {
        this.config = config;
    }
}
