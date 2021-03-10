package com.passswordmanager.Controllers;

import com.passswordmanager.Datatypes.Password;
import com.passswordmanager.Util.FileCrypt;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.ContextMenuEvent;
import javafx.scene.input.MouseEvent;

import java.awt.*;
import java.awt.datatransfer.StringSelection;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Map;

public class PasswordListController {
    @FXML
    public TableView<Password> tableView;
    public TableColumn<Password, String> nameColumn;
    public TableColumn<Password, String> passwordColumn;
    public PasswordField passwordField;
    public TextField nameField;

    private LoginPageController loginPageController;
    private ContextMenu contextMenu;

    public PasswordListController() {
        contextMenu = new ContextMenu();
        MenuItem copyName = new MenuItem("copy Name");
        MenuItem copyPassword = new MenuItem("copy Password");
        contextMenu.getItems().add(copyName);
        contextMenu.getItems().add(copyPassword);

        copyName.setOnAction(event -> getName());
        copyPassword.setOnAction(event -> getPassword());
    }

    public void getName() {
        Toolkit.getDefaultToolkit().getSystemClipboard().setContents(
                new StringSelection(tableView.getSelectionModel().getSelectedItem().getName()), null
        );
    }

    public void getPassword() {
        Toolkit.getDefaultToolkit().getSystemClipboard().setContents(
                new StringSelection(tableView.getSelectionModel().getSelectedItem().getPassword()), null
        );
    }

    public void loadTable(String masterPassword) throws FileNotFoundException {
        Map<String, String> hashMap = FileCrypt.getPasswords(masterPassword);
        ObservableList<Password> data = FXCollections.observableArrayList();
        hashMap.forEach((s, s2) -> data.add(new Password(s, s2)));
        nameColumn.setCellValueFactory(new PropertyValueFactory<Password, String>("name"));
        passwordColumn.setCellValueFactory(new PropertyValueFactory<Password, String>("password"));
        tableView.setItems(data);
    }

    private static FXMLLoader loadFXML(String fxml) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(LoginPageController.class.getResource(fxml + ".fxml"));
        fxmlLoader.setLocation(LoginPageController.class.getResource("/loginPage.fxml"));

        return fxmlLoader;
    }

    public LoginPageController getLoginPageController() {
        return loginPageController;
    }

    public void setLoginPageController(LoginPageController loginPageController) {
        this.loginPageController = loginPageController;
    }

    public void onRandomPressed(ActionEvent actionEvent) {
        passwordField.setText(FileCrypt.generatePassword());
    }

    public void onAddPressed(ActionEvent actionEvent) throws FileNotFoundException {
        FileCrypt.addPwToFile(nameField.getText(), passwordField.getText(), loginPageController.masterPassword.getText());
        nameField.setText("");
        loadTable(loginPageController.masterPassword.getText());
    }

    public void onContextMenuRequested(ContextMenuEvent contextMenuEvent) {
        contextMenu.show(tableView, contextMenuEvent.getScreenX(), contextMenuEvent.getScreenY());
    }

    public void onMouseClicked(MouseEvent mouseEvent) {
        if (mouseEvent.getSource() instanceof TableView) {
            contextMenu.hide();
        }
    }
}
