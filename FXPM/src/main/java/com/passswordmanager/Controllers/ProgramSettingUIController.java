package com.passswordmanager.Controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;

public class ProgramSettingUIController {
    @FXML
    public TextField keyShortCut;
    @FXML
    public TextField nickname;
    @FXML
    public TextField title;

    @FXML
    public void onConfirmPressed(ActionEvent actionEvent) {

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
}
