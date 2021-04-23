package com.passswordmanager.Controllers;

import com.passswordmanager.Database.DatabaseConnectionHandler;
import com.passswordmanager.Util.KeyShortCutUtil;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class ProgramSettingUIController {
    @FXML
    public TextField keyShortCut;
    @FXML
    public TextField nickname;
    @FXML
    public TextField title;

    private DatabaseConnectionHandler db;
    private String oldPName;

    @FXML
    public void onConfirmPressed(ActionEvent actionEvent) {
        String nick = nickname.getText().equals(title.getText()) ? "" : nickname.getText();
        if (!KeyShortCutUtil.validate(keyShortCut.getText())) {
            keyShortCut.setStyle("-fx-background-color: red");
            keyShortCut.getTooltip().show(keyShortCut.getScene().getWindow());
            closeStage(actionEvent);
            return;
        } else if (db.isValidNickname(nick)) {
            db.updateProgram(oldPName, title.getText(), nick, keyShortCut.getText());
        }

        closeStage(actionEvent);
    }

    private void closeStage(ActionEvent event) {
        Node source = (Node) event.getSource();
        Stage stage = (Stage) source.getScene().getWindow();
        stage.close();
    }

    public TextField getKeyShortCut() {
        return keyShortCut;
    }

    public void setKeyShortCut(TextField keyShortCut) {
        this.keyShortCut = keyShortCut;
    }

    public TextField getNickname() {
        return nickname;
    }

    public void setNickname(TextField nickname) {
        this.nickname = nickname;
    }

    public TextField getTitle() {
        return title;
    }

    public void setTitle(TextField title) {
        this.title = title;
    }

    public void setDb(DatabaseConnectionHandler db) {
        this.db = db;
    }

    public void setOldPName(String oldPName) {
        this.oldPName = oldPName;
    }
}
