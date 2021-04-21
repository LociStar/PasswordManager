package com.passswordmanager.Controllers;

import com.passswordmanager.Datatypes.Account;
import com.passswordmanager.Datatypes.MasterPassword;
import com.passswordmanager.Datatypes.Program;
import com.passswordmanager.Util.FileCrypt;
import com.passswordmanager.Util.Keyboard;
import javafx.collections.FXCollections;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;

import java.awt.*;
import java.util.List;

public class UserSelectionDialogController {
    @FXML
    public TableView<Account> table;

    private Program program;

    private MasterPassword masterPassword;

    private boolean onlyPassword = false;

    public void loadTable() {
        addListToTable(program.getPasswords());
    }

    public void setEntry(Program program) {
        this.program = program;
    }

    private void addListToTable(List<Account> accountList) {
        table.getColumns().removeAll(table.getColumns());
        TableColumn<Account, String> username = new TableColumn<>("username");
        TableColumn<Account, String> password = new TableColumn<>("password");

        username.setCellValueFactory(new PropertyValueFactory<>("username"));
        password.setCellValueFactory(new PropertyValueFactory<>("password"));

        table.getColumns().add(username);
        //table.getColumns().add(password);

        table.setItems(FXCollections.observableArrayList(accountList));
        table.getSelectionModel().select(0);
    }

    void sendKeys(String keys) {
        Thread thread = new Thread(() -> {
            try {
                Keyboard keyboard = new Keyboard(false);
                keyboard.type(keys);
            } catch (AWTException | InterruptedException awtException) {
                awtException.printStackTrace();
            }
        });
        thread.start();
    }

    private void hideStage(Event event) {
        Node source = (Node) event.getSource();
        Stage stage = (Stage) source.getScene().getWindow();
        stage.hide();
    }

    private void closeStage(Event event) {
        Node source = (Node) event.getSource();
        Stage stage = (Stage) source.getScene().getWindow();
        stage.close();
    }

    @FXML
    public void onKeyReleased(KeyEvent keyEvent) {
        if (keyEvent.getCode().equals(KeyCode.ENTER)) {
            closeStage(keyEvent);
            if (onlyPassword) {
                sendKeys(FileCrypt.decryptText(table.getSelectionModel().getSelectedItem().getPassword(), masterPassword.getPassword()));
            } else {
                sendKeys(table.getSelectionModel().getSelectedItem().getUsername() + "\t" +
                        FileCrypt.decryptText(table.getSelectionModel().getSelectedItem().getPassword(), masterPassword.getPassword()));
            }
            masterPassword.clearPasswordCache();
        }
    }

    public void setMasterPassword(MasterPassword masterPassword) {
        this.masterPassword = masterPassword;
    }

    public void setOnlyPassword(boolean onlyPassword) {
        this.onlyPassword = onlyPassword;
    }
}