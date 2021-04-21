package com.passswordmanager.Controllers;

import com.passswordmanager.Database.DatabaseConnectionHandler;
import com.passswordmanager.Datatypes.Account;
import com.passswordmanager.Datatypes.MasterPassword;
import com.passswordmanager.Datatypes.Program;
import com.passswordmanager.StartInBackground;
import com.passswordmanager.Util.Config;
import com.passswordmanager.Util.FileCrypt;
import com.passswordmanager.Util.Keyboard;
import com.sun.jna.Native;
import com.sun.jna.platform.win32.User32;
import com.sun.jna.platform.win32.WinDef;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.MenuItem;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
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
    public Accordion accordion;

    private MasterPassword masterPassword;
    private DatabaseConnectionHandler db;

    private final Config config = new Config();

    private final int MAX_TITLE_LENGTH = config.getMAX_TITLE_LENGTH();

    /**
     * Constructor, creates the context menu for the table view
     */
    public PasswordListController() {


    }

    /**
     * copy the name to the clipboard
     */
    public void getName() {
        TableView<Account> tableView = (TableView<Account>) accordion.getExpandedPane().getContent();
        Toolkit.getDefaultToolkit().getSystemClipboard().setContents(
                new StringSelection(tableView.getSelectionModel().getSelectedItem().getUsername()), null
        );
    }

    /**
     * copy the password to the clipboard
     */
    public void getPassword() {
        TableView<Account> tableView = (TableView<Account>) accordion.getExpandedPane().getContent();
        Account account = db.getPassword(tableView.getSelectionModel().getSelectedItem().getUsername(), accordion.getExpandedPane().getText());
        Toolkit.getDefaultToolkit().getSystemClipboard().setContents(
                new StringSelection(FileCrypt.decryptText(account.getPassword(), masterPassword.getPassword())), null
        );
        masterPassword.clearPasswordCache();
    }

    public ContextMenu createContextMenu() {
        ContextMenu contextMenu = new ContextMenu();
        //create new MenuItem
        MenuItem copyName = new MenuItem("copy Name");
        MenuItem copyPassword = new MenuItem("copy Password");
        MenuItem deletePassword = new MenuItem("delete Entry");
        MenuItem showPassword = new MenuItem("show Password");
        MenuItem hidePassword = new MenuItem("hide Password");
        //add MenuItem to ContextMenu
        contextMenu.getItems().add(copyName);
        contextMenu.getItems().add(copyPassword);
        contextMenu.getItems().add(showPassword);
        contextMenu.getItems().add(hidePassword);
        contextMenu.getItems().add(deletePassword);
        //set onActionEvent of MenuItem
        copyName.setOnAction(event -> getName());
        copyPassword.setOnAction(event -> getPassword());
        deletePassword.setOnAction(event -> deletePassword());
        showPassword.setOnAction(event -> showPassword());
        hidePassword.setOnAction(event -> hidePassword());

        return contextMenu;
    }

    private void hidePassword() {
        TableView<Account> tableView = (TableView<Account>) accordion.getExpandedPane().getContent();
        tableView.getSelectionModel().getSelectedItem().setPassword("********");
        tableView.refresh();
    }

    private void showPassword() {
        TableView<Account> tableView = (TableView<Account>) accordion.getExpandedPane().getContent();
        TitledPane pane = accordion.getExpandedPane();
        String username = tableView.getSelectionModel().getSelectedItem().getUsername();
        String pName = pane.getText();
        tableView.getSelectionModel().getSelectedItem().setPassword(FileCrypt.decryptText(db.getPassword(username, pName).getPassword(), masterPassword.getPassword()));
        masterPassword.clearPasswordCache();
        tableView.refresh();
    }

    private void deletePassword() {
        TableView<Account> tableView = (TableView<Account>) accordion.getExpandedPane().getContent();
        db.delete(tableView.getSelectionModel().getSelectedItem().getUsername(), accordion.getExpandedPane().getText());
        db.deleteEmptyPrograms();
        loadTable(masterPassword.getPassword());
        masterPassword.clearPasswordCache();
    }

    private TitledPane createTiltedPane(String name, String nickname, Map<String, String> list) {
        String label = nickname.equals("") ? name : nickname;

        TableView<Account> tableView = new TableView<>();
        TableColumn<Account, String> username = new TableColumn<>("username");
        TableColumn<Account, String> password = new TableColumn<>("password");

        username.setCellValueFactory(new PropertyValueFactory<>("username"));
        password.setCellValueFactory(new PropertyValueFactory<>("password"));

        tableView.getColumns().add(username);
        tableView.getColumns().add(password);

        tableView.setContextMenu(createContextMenu());

        ObservableList<Account> data = FXCollections.observableArrayList();
        list.forEach((s, s2) -> data.add(new Account(s, "********")));

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
     * sets the master Password
     *
     * @param masterPassword login page controller
     */
    public void setMasterPassword(MasterPassword masterPassword) {
        this.masterPassword = masterPassword;
    }

    /**
     * adds a new password to the passwordField and the password lost file
     *
     * @throws FileNotFoundException password list not found
     */
    public void onAddPressed() throws IOException {
        createAddEntryDialog();
    }

    private void createAddEntryDialog(String pName) throws IOException {
        FXMLLoader fxmlLoader = loadFXML("addEntryDialog");
        Parent parent = fxmlLoader.load();
        AddEntryDialogController dialogController = fxmlLoader.getController();
        dialogController.setMasterPassword(this.masterPassword);
        dialogController.setDb(db);
        dialogController.setPNameText(pName);
        Scene scene = new Scene(parent);
        Stage stage = new Stage();
        stage.setTitle("PasswordManager");
        stage.getIcons().add(new Image(StartInBackground.class.getResourceAsStream("/icon.jpeg")));
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setScene(scene);
        stage.showAndWait();
        loadTable(masterPassword.getPassword());
        masterPassword.clearPasswordCache();
    }

    private void createAddEntryDialog() throws IOException {
        createAddEntryDialog("");
    }

    public void setDb(DatabaseConnectionHandler db) {
        this.db = db;
    }

    @Override
    public void nativeKeyTyped(NativeKeyEvent e) {
    }

    @Override
    public void nativeKeyReleased(NativeKeyEvent e) {
        if (STRG_ALT_A(e)) return;
        if (STRG_ALT_Y(e)) return;
    }

    @Override
    public void nativeKeyPressed(NativeKeyEvent e) {
        STRG_ALT_X(e);
    }

    private void STRG_ALT_X(NativeKeyEvent e) {
        if ((e.getModifiers() & NativeKeyEvent.CTRL_MASK) != 0
                && (e.getModifiers() & NativeKeyEvent.ALT_MASK) != 0
                && e.getKeyCode() == NativeKeyEvent.VC_X) {

            if (masterPassword == null || masterPassword.isEmpty()) {
                System.out.println("PasswordManager is Locked");
                return;
            }

            masterPassword.clearPasswordCache();
            String activeWindow = getActiveWindow();

            Platform.runLater(() -> {
                try {
                    createAddEntryDialog(activeWindow);
                } catch (IOException ioException) {
                    ioException.printStackTrace();
                }
            });
        }
    }

    private boolean STRG_ALT_Y(NativeKeyEvent e) {
        //STRG+ALT+Y  -> write Password
        if ((e.getModifiers() & NativeKeyEvent.CTRL_MASK) != 0
                && (e.getModifiers() & NativeKeyEvent.ALT_MASK) != 0
                && e.getKeyCode() == NativeKeyEvent.VC_Y) {

            if (masterPassword == null || masterPassword.isEmpty()) {
                System.out.println("PasswordManager is Locked");
                return true;
            }

            masterPassword.clearPasswordCache();
            String activeWindow = getActiveWindow();

            Program program = db.getPasswords(activeWindow);
            //no match found
            if (program.getPasswords().size() == 0) return true;
            else if (program.getPasswords().size() > 1) {
                Platform.runLater(() -> startSelectionDialog(program, true));
                return true;
            }

            Account account = program.getPasswords().get(0);

            sendKeys(FileCrypt.decryptText(account.getPassword(), masterPassword.getPassword()));
            masterPassword.clearPasswordCache();
            return true;
        }
        return false;
    }

    private boolean STRG_ALT_A(NativeKeyEvent e) {
        //STRG+ALT+A  -> write Username + TAB + Password
        if ((e.getModifiers() & NativeKeyEvent.CTRL_MASK) != 0
                && (e.getModifiers() & NativeKeyEvent.ALT_MASK) != 0
                && e.getKeyCode() == NativeKeyEvent.VC_A) {
            e.setKeyCode(0);

            if (masterPassword == null || masterPassword.isEmpty()) {
                System.out.println("PasswordManager is Locked");
                return true;
            }

            masterPassword.clearPasswordCache();
            String activeWindow = getActiveWindow();
            Program program = db.getPasswords(activeWindow);
            //no match found
            if (program.getPasswords().size() == 0) return true;
            else if (program.getPasswords().size() > 1) {
                Platform.runLater(() -> startSelectionDialog(program, false));
                return true;
            }


            Account account = program.getPasswords().get(0);

            sendKeys(account.getUsername() + "\t" + FileCrypt.decryptText(account.getPassword(), masterPassword.getPassword()));
            masterPassword.clearPasswordCache();
            return true;
        }
        return false;
    }

    private void startSelectionDialog(Program program, boolean onlyPassword) {
        FXMLLoader fxmlLoader = loadFXML("userSelectionDialog");
        Parent parent = null;
        try {
            parent = fxmlLoader.load();
        } catch (IOException ioException) {
            ioException.printStackTrace();
        }
        assert parent != null;
        Scene scene = new Scene(parent);
        Stage stage = new Stage();
        UserSelectionDialogController userSelectionDialogController = fxmlLoader.getController();
        userSelectionDialogController.setEntry(program);
        userSelectionDialogController.loadTable();
        userSelectionDialogController.setMasterPassword(masterPassword);
        userSelectionDialogController.setOnlyPassword(onlyPassword);
        stage.initStyle(StageStyle.UNDECORATED);
        stage.initModality(Modality.WINDOW_MODAL);
        stage.setAlwaysOnTop(true);
        stage.setScene(scene);
        stage.setTitle("PasswordManager");
        stage.getIcons().add(new Image(StartInBackground.class.getResourceAsStream("/icon.jpeg")));
        stage.showAndWait();
    }

    void sendKeys(String keys) {
        Thread thread = new Thread(() -> {
            try {
                Keyboard keyboard = new Keyboard(true);
                keyboard.type(keys);
            } catch (AWTException | InterruptedException awtException) {
                awtException.printStackTrace();
            }
        });
        thread.start();
    }

    public String getActiveWindow() {
        //only Windows
        char[] buffer = new char[MAX_TITLE_LENGTH * 2];
        WinDef.HWND hwnd = User32.INSTANCE.GetForegroundWindow();
        User32.INSTANCE.GetWindowText(hwnd, buffer, MAX_TITLE_LENGTH);
        System.out.println("Active window title: " + Native.toString(buffer));
        return Native.toString(buffer);
    }

    private static FXMLLoader loadFXML(String fxml) {
        return new FXMLLoader(AddEntryDialogController.class.getResource("/" + fxml + ".fxml"));
    }

    public void resetPassword() {
        if (this.masterPassword != null) {
            this.masterPassword.clearPasswordCache();
            this.masterPassword.clearGuardedString();
        }
        this.masterPassword = null;
    }

    @FXML
    public void onSettingsPressed(ActionEvent actionEvent) {
        FXMLLoader fxmlLoader = loadFXML("settingsUI");
        Parent parent = null;
        try {
            parent = fxmlLoader.load();
        } catch (IOException ioException) {
            ioException.printStackTrace();
        }
        assert parent != null;
        Scene scene = new Scene(parent);
        Stage stage = new Stage();
        stage.setTitle("PasswordManager");
        stage.getIcons().add(new Image(StartInBackground.class.getResourceAsStream("/icon.jpeg")));
        SettingsUIController settingsUIController = fxmlLoader.getController();
        settingsUIController.setConfig(this.config);
        settingsUIController.loadConfig();
        //stage.initStyle(StageStyle.UNDECORATED);
        //stage.initModality(Modality.WINDOW_MODAL);
        stage.setAlwaysOnTop(true);
        stage.setScene(scene);
        stage.showAndWait();
    }

    public Config getConfig() {
        return config;
    }
}
