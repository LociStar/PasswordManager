package com.passswordmanager.Controllers;

import com.passswordmanager.Database.DatabaseConnectionHandler;
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

/**
 * FXML Controller to control passwordListController.fxml
 */
public class PasswordListController {
    @FXML
    public TableView<Password> tableView;
    public TableColumn<Password, String> nameColumn;
    public TableColumn<Password, String> passwordColumn;
    public PasswordField passwordField;
    public TextField nameField;

    private LoginPageController loginPageController;
    private ContextMenu contextMenu;
    private DatabaseConnectionHandler db;

    /**
     * Constructor, creates the context menu for the table view
     */
    public PasswordListController() {
        contextMenu = new ContextMenu();
        MenuItem copyName = new MenuItem("copy Name");
        MenuItem copyPassword = new MenuItem("copy Password");
        contextMenu.getItems().add(copyName);
        contextMenu.getItems().add(copyPassword);

        copyName.setOnAction(event -> getName());
        copyPassword.setOnAction(event -> getPassword());
    }

    /**
     * copy the name to the clipboard
     */
    public void getName() {
        Toolkit.getDefaultToolkit().getSystemClipboard().setContents(
                new StringSelection(tableView.getSelectionModel().getSelectedItem().getName()), null
        );
    }

    /**
     * copy the password to the clipboard
     */
    public void getPassword() {
        Toolkit.getDefaultToolkit().getSystemClipboard().setContents(
                new StringSelection(tableView.getSelectionModel().getSelectedItem().getPassword()), null
        );
    }

    /**
     * Decrypts the password list and loads the passwords into the table view
     * @param masterPassword master password to decrypt the password list
     */
    public void loadTable(String masterPassword) {
        Map<String, String> hashMap = FileCrypt.getListDB(masterPassword, db);
        ObservableList<Password> data = FXCollections.observableArrayList();
        hashMap.forEach((s, s2) -> data.add(new Password(s, s2)));
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        passwordColumn.setCellValueFactory(new PropertyValueFactory<>("password"));
        tableView.setItems(data);
    }

    /**
     * gets the login page controller
     * @return login page controller
     */
    public LoginPageController getLoginPageController() {
        return loginPageController;
    }

    /**
     * sets the login page controller
     * @param loginPageController login page controller
     */
    public void setLoginPageController(LoginPageController loginPageController) {
        this.loginPageController = loginPageController;
    }

    /**
     * sets the passwordField to a random password
     */
    public void onRandomPressed() {
        passwordField.setText(FileCrypt.generatePassword());
    }

    /**
     * adds a new password to the passwordField and the password lost file
     * @throws FileNotFoundException password list not found
     */
    public void onAddPressed() throws FileNotFoundException {
        FileCrypt.addPwToDatabase(nameField.getText(), passwordField.getText(), loginPageController.masterPassword.getText(), db);
        nameField.setText("");
        loadTable(loginPageController.masterPassword.getText());
    }

    /**
     * handles the context menu request
     * @param contextMenuEvent ContextMenuEvent
     */
    public void onContextMenuRequested(ContextMenuEvent contextMenuEvent) {
        contextMenu.show(tableView, contextMenuEvent.getScreenX(), contextMenuEvent.getScreenY());
    }

    /**
     * hides the context menu with mouse left click
     * @param mouseEvent mouse left click
     */
    public void onMouseClicked(MouseEvent mouseEvent) {
        if (mouseEvent.getSource() instanceof TableView) {
            contextMenu.hide();
        }
    }

    public void setDb(DatabaseConnectionHandler db) {
        this.db = db;
    }
}
