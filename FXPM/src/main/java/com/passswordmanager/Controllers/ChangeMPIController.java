package com.passswordmanager.Controllers;

import com.lambdaworks.crypto.SCryptUtil;
import com.passswordmanager.Database.DatabaseConnectionHandler;
import com.passswordmanager.Datatypes.MasterPassword;
import com.passswordmanager.Util.Config;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.PasswordField;
import javafx.stage.Stage;

public class ChangeMPIController {
    private final Config config = new Config();
    @FXML
    public PasswordField conPwd;
    @FXML
    public PasswordField newPwd;
    @FXML
    public PasswordField oldPwd;
    private DatabaseConnectionHandler db;
    private MasterPassword oldMasterPassword;

    @FXML
    public void onConfirmPressed(ActionEvent actionEvent) {
        if (oldPwd.getText().equals(oldMasterPassword.getPassword()) &&
                SCryptUtil.check(oldMasterPassword.getPassword(), config.getMasterPassword()) &&
                newPwd.getText().equals(conPwd.getText())) {

            MasterPassword newMasterPassword = new MasterPassword(conPwd.getText().toCharArray());
            config.setMasterPassword(SCryptUtil.scrypt(newMasterPassword.getPassword(), 8, 8, 8));
            config.writeConfig();

            db.changeMP(oldMasterPassword, newMasterPassword);
            newMasterPassword.clearPasswordCache();
            oldMasterPassword.clearPasswordCache();
            oldMasterPassword.clearGuardedString();
            newMasterPassword.clearGuardedString();
            closeStage(actionEvent);
        }
    }

    @FXML
    public void onCanceledPressed(ActionEvent actionEvent) {
        closeStage(actionEvent);
    }

    private void closeStage(Event event) {
        Node source = (Node) event.getSource();
        Stage stage = (Stage) source.getScene().getWindow();
        stage.close();
    }

    public void setDb(DatabaseConnectionHandler db) {
        this.db = db;
    }

    public void setOldMasterPassword(MasterPassword oldMasterPassword) {
        this.oldMasterPassword = oldMasterPassword;
    }
}
