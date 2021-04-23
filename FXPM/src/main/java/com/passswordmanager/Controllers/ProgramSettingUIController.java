package com.passswordmanager.Controllers;

import com.passswordmanager.Database.DatabaseConnectionHandler;
import com.passswordmanager.Util.KeyShortCutUtil;
import javafx.animation.PauseTransition;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.control.Control;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.stage.Stage;
import javafx.util.Duration;

public class ProgramSettingUIController {
    @FXML
    public TextField keyShortCut;
    @FXML
    public TextField nickname;
    @FXML
    public TextField title;

    private DatabaseConnectionHandler db;
    private String oldPName;
    private String oldNickname;

    public static void showOneTimeTooltip(Control control, String tooltipText) {

        Point2D p = control.localToScreen(5, 5);

        final Tooltip customTooltip = new Tooltip(tooltipText);
        customTooltip.setAutoHide(false);
        customTooltip.show(control, p.getX(), p.getY());

        PauseTransition pt = new PauseTransition(Duration.millis(2000));
        pt.setOnFinished(e -> {
            customTooltip.hide();
        });
        pt.play();
    }

    @FXML
    public void onConfirmPressed(ActionEvent actionEvent) {
        String nick = nickname.getText().equals(title.getText()) ? "" : nickname.getText();
        if (!KeyShortCutUtil.validate(keyShortCut.getText())) {
            keyShortCut.setStyle("-fx-background-color: red");
            showOneTimeTooltip(nickname, keyShortCut.getTooltip().getText());
            return;
        } else if (!db.isValidProgramName(oldPName, title.getText())) {
            showOneTimeTooltip(title, "ProgramName is already taken and cant be empty!");
            return;
        } else if (!db.isValidNickname(nick, oldNickname)) {
            nickname.setStyle("-fx-border-color: red");
            showOneTimeTooltip(nickname, "Nickname cant be a ProgramName and must be unique!");
            return;
        } else
            db.updateProgram(oldPName, title.getText(), nick, keyShortCut.getText());
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

    public void setOldNickname(String oldNickname) {
        this.oldNickname = oldNickname;
    }
}
