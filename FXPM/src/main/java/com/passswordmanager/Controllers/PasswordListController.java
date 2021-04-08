package com.passswordmanager.Controllers;

import com.passswordmanager.Database.DatabaseConnectionHandler;
import com.passswordmanager.Datatypes.Password;
import com.passswordmanager.Datatypes.Entry;
import com.passswordmanager.Util.FileCrypt;
import com.passswordmanager.Util.Keyboard;
import com.sun.jna.Native;
import com.sun.jna.platform.win32.User32;
import com.sun.jna.platform.win32.WinDef;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.ContextMenuEvent;
import javafx.scene.input.MouseEvent;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.jasypt.util.text.AES256TextEncryptor;
import org.jnativehook.keyboard.NativeKeyEvent;
import org.jnativehook.keyboard.NativeKeyListener;

import java.awt.*;
import java.awt.datatransfer.StringSelection;
import java.io.FileNotFoundException;
import java.io.IOException;
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


    }

    /**
     * copy the name to the clipboard
     */
    public void getName() {
        TableView<Password> tableView = (TableView<Password>) accordion.getExpandedPane().getContent();
        Toolkit.getDefaultToolkit().getSystemClipboard().setContents(
                new StringSelection(tableView.getSelectionModel().getSelectedItem().getUsername()), null
        );
    }

    /**
     * copy the password to the clipboard
     */
    public void getPassword() {
        TableView<Password> tableView = (TableView<Password>) accordion.getExpandedPane().getContent();
        AES256TextEncryptor textEncryptor = new AES256TextEncryptor();
        textEncryptor.setPassword(loginPageController.masterPassword.getText());
        Toolkit.getDefaultToolkit().getSystemClipboard().setContents(
                new StringSelection(textEncryptor.decrypt(db.getPassword(tableView.getSelectionModel().getSelectedItem().getUsername(), accordion.getExpandedPane().getText()).getPassword())), null
        );
    }

    public ContextMenu createContextMenu() {
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

        return contextMenu;
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

        tableView.setContextMenu(createContextMenu());

        ObservableList<Password> data = FXCollections.observableArrayList();
        list.forEach((s, s2) -> data.add(new Password(s, "********")));

        tableView.setItems(data);

        return new TitledPane(label, tableView);
    }

    /**
     * Decrypts the password list and loads the passwords into the table view
     *
     * @param masterPassword master password to decrypt the password list
     */
    public void loadTable(String masterPassword) {
        accordion.getPanes().removeAll(accordion.getPanes());
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
    public void onAddPressed() throws IOException {

        //FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("addEntryDialog.fxml"));
        FXMLLoader fxmlLoader = loadFXML("addEntryDialog");
        Parent parent = fxmlLoader.load();
        AddEntryDialogController dialogController = fxmlLoader.getController();
        dialogController.setPasswordListController(this);
        dialogController.setDb(db);
        Scene scene = new Scene(parent);
        Stage stage = new Stage();
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setScene(scene);
        stage.showAndWait();
//        FileCrypt.addPwToDatabase(nameField.getText(), passwordField.getText(), loginPageController.masterPassword.getText(), getActiveWindow(), db);
//        loadTable(loginPageController.masterPassword.getText());
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
        //STRG+ALT+A  -> write Username + ENTER + Password
        if ((e.getModifiers() & NativeKeyEvent.CTRL_MASK) != 0
                && (e.getModifiers() & NativeKeyEvent.ALT_MASK) != 0
                && e.getKeyCode() == NativeKeyEvent.VC_A) {
            if (loginPageController.masterPassword.getText().equals("")) {
                System.out.println("PasswordManager is Locked");
                return;
            }
            String activeWindow = getActiveWindow();

            Entry entry = db.getPasswords(activeWindow);
            //no match found
            if (entry.getPasswords().size() == 0) return;

            Password password = entry.getPasswords().get(0); //TODO: selection model for Passwords needed

            sendKeys(password.getUsername());
            sendKeys("\n");
            sendKeys(FileCrypt.decryptText(password.getPassword(), loginPageController.masterPassword.getText()));
        }
        //STRG+ALT+Y  -> write Password
        if ((e.getModifiers() & NativeKeyEvent.CTRL_MASK) != 0
                && (e.getModifiers() & NativeKeyEvent.ALT_MASK) != 0
                && e.getKeyCode() == NativeKeyEvent.VC_Y) {
            if (loginPageController.masterPassword.getText().equals("")) {
                System.out.println("PasswordManager is Locked");
                return;
            }
            String activeWindow = getActiveWindow();

            Entry entry = db.getPasswords(activeWindow);
            //no match found
            if (entry.getPasswords().size() == 0) return;

            Password password = entry.getPasswords().get(0); //TODO: selection model for Passwords needed

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

    public String getActiveWindow() {
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

    private static FXMLLoader loadFXML(String fxml) {
        return new FXMLLoader(AddEntryDialogController.class.getResource("/" + fxml + ".fxml"));
    }


}
