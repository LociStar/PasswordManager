package com.passswordmanager.Controllers;

import com.passswordmanager.Datatypes.Entry;
import com.passswordmanager.Datatypes.Password;
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
import org.jasypt.util.text.AES256TextEncryptor;

import java.awt.*;
import java.util.List;

public class UserSelectionDialogController {
    @FXML
    public TableView<Password> table;

    private Entry entry;

    private LoginPageController loginPageController;

    public void loadTable() {
        addListToTable(entry.getPasswords());
    }

    public void setEntry(Entry entry) {
        this.entry = entry;
    }

    private void addListToTable(List<Password> passwordList) {
        table.getColumns().removeAll(table.getColumns());
        TableColumn<Password, String> username = new TableColumn<>("username");
        TableColumn<Password, String> password = new TableColumn<>("password");

        username.setCellValueFactory(new PropertyValueFactory<>("username"));
        password.setCellValueFactory(new PropertyValueFactory<>("password"));

        table.getColumns().add(username);
        table.getColumns().add(password);

        table.setItems(FXCollections.observableArrayList(passwordList));
        table.getSelectionModel().select(0);
    }

    void sendKeys(String keys) {
        try {
            Keyboard keyboard = new Keyboard();
            keyboard.type(keys);
        } catch (AWTException | InterruptedException awtException) {
            awtException.printStackTrace();
        }
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
        System.out.println(keyEvent.getCode());
        if (keyEvent.getCode().equals(KeyCode.ENTER)) {
            closeStage(keyEvent);
            AES256TextEncryptor textEncryptor = new AES256TextEncryptor();
            textEncryptor.setPassword(loginPageController.masterPassword.getText());
            sendKeys(table.getSelectionModel().getSelectedItem().getUsername());
            sendKeys("\t");
            sendKeys(textEncryptor.decrypt(table.getSelectionModel().getSelectedItem().getPassword()));
            //closeStage(keyEvent);
        }
    }

    public void setLoginPageController(LoginPageController loginPageController) {
        this.loginPageController = loginPageController;
    }
}