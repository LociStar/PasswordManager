package com.passswordmanager.Controllers;

import com.passswordmanager.Database.DatabaseConnectionHandler;
import com.passswordmanager.Datatypes.Password;
import com.passswordmanager.Datatypes.Program;
import com.passswordmanager.Util.FileCrypt;
import com.passswordmanager.Util.Keyboard;
import com.sun.jna.Native;
import com.sun.jna.platform.win32.User32;
import com.sun.jna.platform.win32.WinDef;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.ContextMenuEvent;
import javafx.scene.input.MouseEvent;
import org.jnativehook.keyboard.NativeKeyEvent;
import org.jnativehook.keyboard.NativeKeyListener;

import java.awt.*;
import java.awt.datatransfer.StringSelection;
import java.io.FileNotFoundException;
import java.sql.Array;
import java.util.Map;

/**
 * FXML Controller to control passwordListController.fxml
 */
public class PasswordListController implements NativeKeyListener {
    @FXML
    public PasswordField passwordField;
    public TextField nameField;
    public Accordion accordion;

    private LoginPageController loginPageController;
    private ContextMenu contextMenu;
    private DatabaseConnectionHandler db;

    private static final int MAX_TITLE_LENGTH = 1024;

    /**
     * Constructor, creates the context menu for the table view
     */
    public PasswordListController() {

        contextMenu = new ContextMenu();
        //create new MenuItem
        MenuItem copyName = new MenuItem("copy Name");
        MenuItem copyPassword = new MenuItem("copy Password");
        //add MenuItem to ContextMenu
        contextMenu.getItems().add(copyName);
        contextMenu.getItems().add(copyPassword);
        //set onActionEvent of MenuItem
        copyName.setOnAction(event -> getName());
        copyPassword.setOnAction(event -> getPassword());

    }

    /**
     * copy the name to the clipboard
     */
    public void getName() {
//        Toolkit.getDefaultToolkit().getSystemClipboard().setContents(
//                new StringSelection(tableView.getSelectionModel().getSelectedItem().getUsername()), null
//        );
    }

    /**
     * copy the password to the clipboard
     */
    public void getPassword() {
//        Toolkit.getDefaultToolkit().getSystemClipboard().setContents(
//                new StringSelection(tableView.getSelectionModel().getSelectedItem().getPassword()), null
//        );
    }

    private TitledPane createTiltedPane(String name, String nickname, Map<String, String> list) {
        String label = nickname.equals("") ? name : nickname;

        TableView<Password> tableView = new TableView<>();
        TableColumn<Password, String> username = new TableColumn<>("username");
        TableColumn<Password, String> password = new TableColumn<>("password");

        username.setCellValueFactory(new PropertyValueFactory<>("username"));
        password.setCellValueFactory(new PropertyValueFactory<>("password"));

        tableView.getColumns().add(username);
        tableView.getColumns().add(password);

        ObservableList<Password> data = FXCollections.observableArrayList();
        list.forEach((s, s2) -> data.add(new Password(s, s2)));

        tableView.setItems(data);

        TitledPane pane = new TitledPane(label, tableView);

        return pane;
    }

    /**
     * Decrypts the password list and loads the passwords into the table view
     *
     * @param masterPassword master password to decrypt the password list
     */
    public void loadTable(String masterPassword) {
        Map<String, Map<String, String>> hashMap = FileCrypt.getListDB(masterPassword, db);
        hashMap.forEach((s, stringStringMap) -> {
            String[] names = s.split(":");
            if (names.length == 2) {
                accordion.getPanes().add(createTiltedPane(names[0], names[1], stringStringMap));
            } else {
                accordion.getPanes().add(createTiltedPane(names[0], "", stringStringMap));
            }
        });
    }

    /**
     * gets the login page controller
     *
     * @return login page controller
     */
    public LoginPageController getLoginPageController() {
        return loginPageController;
    }

    /**
     * sets the login page controller
     *
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
     *
     * @throws FileNotFoundException password list not found
     */
    public void onAddPressed() throws FileNotFoundException {
        FileCrypt.addPwToDatabase(nameField.getText(), passwordField.getText(), loginPageController.masterPassword.getText(), getActiveWindow(), db);
        nameField.setText("");
        loadTable(loginPageController.masterPassword.getText());
    }

    /**
     * handles the context menu request
     *
     * @param contextMenuEvent ContextMenuEvent
     */
    public void onContextMenuRequested(ContextMenuEvent contextMenuEvent) {
        //contextMenu.show(tableView, contextMenuEvent.getScreenX(), contextMenuEvent.getScreenY());
    }

    /**
     * hides the context menu on mouse left click
     *
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

    @Override
    public void nativeKeyTyped(NativeKeyEvent e) {
    }

    @Override
    public void nativeKeyPressed(NativeKeyEvent e) {
        if ((e.getModifiers() & NativeKeyEvent.CTRL_MASK) != 0
                && (e.getModifiers() & NativeKeyEvent.ALT_MASK) != 0
                && e.getKeyCode() == NativeKeyEvent.VC_A) {
            if (loginPageController.masterPassword.getText().equals("")) {
                System.out.println("PasswordManager is Locked");
                return;
            }
            System.out.println("Paste password");
            String activeWindow = getActiveWindow();

            Password password = db.getPassword(activeWindow).getPasswords().get(0); //TODO: selection model for Passwords needed
            System.out.println(FileCrypt.decryptText(password.getPassword(), loginPageController.masterPassword.getText()));

            sendKeys(FileCrypt.decryptText(password.getPassword(), loginPageController.masterPassword.getText()));
        }
    }

    void sendKeys(String keys) {
        try {
            Keyboard keyboard = new Keyboard();
            keyboard.type(keys);
        } catch (AWTException | InterruptedException awtException) {
            awtException.printStackTrace();
        }
    }

    private String getActiveWindow() {
        //only Windows
        char[] buffer = new char[MAX_TITLE_LENGTH * 2];
        WinDef.HWND hwnd = User32.INSTANCE.GetForegroundWindow();
        User32.INSTANCE.GetWindowText(hwnd, buffer, MAX_TITLE_LENGTH);
        System.out.println("Active window title: " + Native.toString(buffer));
        return Native.toString(buffer);
    }

    @Override
    public void nativeKeyReleased(NativeKeyEvent e) {

    }


}
