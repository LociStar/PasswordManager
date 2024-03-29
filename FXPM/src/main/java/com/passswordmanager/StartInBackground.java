package com.passswordmanager;

import com.passswordmanager.Controllers.InstallUIController;
import com.passswordmanager.Controllers.LoginPageController;
import com.passswordmanager.Controllers.PasswordListController;
import com.passswordmanager.Util.Config;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.WindowEvent;
import org.jnativehook.GlobalScreen;
import org.jnativehook.NativeHookException;
import org.jnativehook.dispatcher.SwingDispatchService;

import javax.imageio.ImageIO;
import java.io.*;
import java.util.Properties;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Level;
import java.util.logging.Logger;

// Java 8 code
public class StartInBackground extends Application {

    // one icon location is shared between the application tray icon and task bar icon.
    // you could also use multiple icons to allow for clean display of tray icons on hi-dpi devices.
    //private static final String iconImageLoc = StartInBackground.class.getResource("/icon.jpeg").getPath();
    //"https://icons.iconarchive.com/icons/icons8/ios7/16/User-Interface-Password-icon.png";

    // application stage is stored so that it can be shown and hidden based on system tray icon operations.
    private Stage passwordStage;
    private Stage loginStage;
    private LoginPageController loginPageController;

    private Config config;

    private boolean isInstalled = false;

    private final String icon = "/icon.png";

    // a timer allowing the tray icon to provide a periodic notification event.
    private final Timer notificationTimer = new Timer();

    // format used to display the current time in a tray icon notification.
    //private final DateFormat timeFormat = SimpleDateFormat.getTimeInstance();

    // sets up the javafx application.
    // a tray icon is setup for the icon, but the main stage remains invisible until the user
    // interacts with the tray icon.
    @Override
    public void start(final Stage stage) throws IOException {

        // stores a reference to the stage.
        this.loginStage = stage;

        newInstall();

        if (!isInstalled) {
            FXMLLoader loaderLP = loadFXML("installUI");
            Parent parent = loaderLP.load();
            InstallUIController installUIController = loaderLP.getController();
            installUIController.setDatabasePath(System.getenv("APPDATA") + "\\PasswordManager");
            Scene scene = new Scene(parent);
            scene.getStylesheets().add(getClass().getResource("/styles.css").toExternalForm());
            stage.setTitle("PasswordManager");
            stage.getIcons().add(new Image(StartInBackground.class.getResourceAsStream(icon)));
            stage.setOnCloseRequest(event -> {
                Platform.exit();
                System.exit(0);
            });
            stage.setScene(scene);
            stage.show();
        } else {
            this.config = new Config();

            // instructs the javafx system not to exit implicitly when the last application window is shut.
            Platform.setImplicitExit(false);

            // sets up the tray icon (using awt code run on the swing thread).
            javax.swing.SwingUtilities.invokeLater(this::addAppToTray);

            // out stage will be translucent, so give it a transparent style.
            stage.initStyle(StageStyle.TRANSPARENT);

            //load fxml
            FXMLLoader loaderLP = loadFXML("loginPage");
            Parent parent = loaderLP.load();
            this.loginPageController = loaderLP.getController();
            Scene scene = new Scene(parent);
            scene.getStylesheets().add(getClass().getResource("/styles.css").toExternalForm());
            scene.setFill(Color.TRANSPARENT);

            stage.setScene(scene);
            stage.setTitle("PasswordManager");
            stage.getIcons().add(new Image(StartInBackground.class.getResourceAsStream(icon)));

            //load second fxml
            FXMLLoader loaderPL = loadFXML("passwordListUI");
            Parent parent1 = loaderPL.load();
            Scene scene1 = new Scene(parent1);
            passwordStage = new Stage();
            passwordStage.setScene(scene1);
            passwordStage.initStyle(StageStyle.DECORATED);
            scene1.getStylesheets().add(getClass().getResource("/styles.css").toExternalForm());

            PasswordListController passwordListController = loaderPL.getController();
            passwordStage.setOnCloseRequest(event -> passwordListController.hideAllPasswords());
            passwordListController.setMasterPassword(loginPageController.getMasterPassword());

            loginPageController.setPasswordListController(passwordListController);
            loginPageController.setPasswordStage(passwordStage);

            //add keyListener Listener
            javax.swing.SwingUtilities.invokeLater(() -> GlobalScreen.addNativeKeyListener(passwordListController));
        }
    }

    private void exit(WindowEvent event) {
        System.exit(0);
    }

    private void newInstall() throws IOException {
        InputStream inputStream = null;
        try {
            Properties prop = new Properties();
            System.out.println("---------------------------");
            System.out.println(new File(".").getCanonicalPath());
            System.out.println("---------------------------");
            inputStream = new FileInputStream(new File(".").getCanonicalPath() + "\\config.txt");
            prop.load(inputStream);
            this.isInstalled = Boolean.parseBoolean(prop.getProperty("installed"));
        } catch (Exception e) {
            System.out.println("Exception: " + e);
            File file = new File(new File(".").getCanonicalPath() + "\\config.txt");
            OutputStream outputStream = new FileOutputStream(file);
            Properties prop = new Properties();

            // set the properties value
            prop.setProperty("installed", "" + false + "");

            // save properties to project root folder
            prop.store(outputStream, null);
            newInstall();
            outputStream.close();

        } finally {
            if (inputStream != null)
                inputStream.close();
        }
    }

    /**
     * Sets up a system tray icon for the application.
     */
    private void addAppToTray() {
        try {
            // ensure awt toolkit is initialized.
            java.awt.Toolkit.getDefaultToolkit();

            // app requires system tray support, just exit if there is no support.
            if (!java.awt.SystemTray.isSupported()) {
                System.out.println("No system tray support, application exiting.");
                Platform.exit();
            }

            // set up a system tray icon.
            java.awt.SystemTray tray = java.awt.SystemTray.getSystemTray();
            java.awt.Image image = ImageIO.read(Main.class.getResource(icon));
            java.awt.TrayIcon trayIcon = new java.awt.TrayIcon(image);
            trayIcon.setImageAutoSize(true);

            // if the user double-clicks on the tray icon, show the main app stage.
            trayIcon.addActionListener(event -> Platform.runLater(this::showStage));

            // if the user selects the default menu item (which includes the app name),
            // show the main app stage.
            java.awt.MenuItem openItem = new java.awt.MenuItem("Unlock");
            openItem.addActionListener(event -> Platform.runLater(this::showStage));

            // the convention for tray icons seems to be to set the default icon for opening
            // the application stage in a bold font.
            java.awt.Font defaultFont = java.awt.Font.decode(null);
            java.awt.Font boldFont = defaultFont.deriveFont(java.awt.Font.BOLD);
            openItem.setFont(boldFont);

            // to really exit the application, the user must go to the system tray icon
            // and select the exit option, this will shutdown JavaFX and remove the
            // tray icon (removing the tray icon will also shut down AWT).
            java.awt.MenuItem exitItem = new java.awt.MenuItem("Close");
            exitItem.addActionListener(event -> {
                notificationTimer.cancel();
                Platform.runLater(() -> {
                    passwordStage.close();
                    loginPageController.getDb().closeConnection();
                    if (loginPageController.getMasterPassword() != null)
                        loginPageController.getMasterPassword().clearGuardedString();
                    loginStage.close();
                });
                Platform.exit();
                tray.remove(trayIcon);
                System.exit(0);
            });

            // setup the popup menu for the application.
            final java.awt.PopupMenu popup = new java.awt.PopupMenu();
            popup.add(openItem);
            popup.addSeparator();
            popup.add(exitItem);
            trayIcon.setPopupMenu(popup);

            // create a timer which periodically displays a notification message.
            notificationTimer.schedule(
                    new TimerTask() {
                        @Override
                        public void run() {
                            javax.swing.SwingUtilities.invokeLater(() -> {
                                if (loginPageController == null) return;
                                if (!loginPageController.isLocked()) {
                                    trayIcon.displayMessage(
                                            "State",
                                            "Password Manager locked",
                                            java.awt.TrayIcon.MessageType.INFO
                                    );
                                    //Password reset
                                    loginPageController.passwordField.setText("");
                                    loginPageController.resetPassword();
                                    loginPageController.getPasswordListController().accordion.getPanes().removeAll(loginPageController.getPasswordListController().accordion.getPanes());
                                        }
                                    }
                            );
                        }
                    },
                    0,
                    config.getDelay()
            );

            // add the application tray icon to the system tray.
            tray.add(trayIcon);
        } catch (java.awt.AWTException | IOException e) {
            System.out.println("Unable to init system tray");
            e.printStackTrace();
        }
    }

    /**
     * Shows the application stage and ensures that it is brought ot the front of all stages.
     */
    private void showStage() {
        if (loginPageController.isLocked()) {
            if (loginStage != null) {
                loginStage.show();
                loginStage.toFront();
            } else {
                System.out.println("Error");
            }
        } else {
            passwordStage.show();
        }

    }

    public static void main(String[] args) {

        try {
            // Get the logger for "org.jnativehook" and set the level to warning.
            Logger logger = Logger.getLogger(GlobalScreen.class.getPackage().getName());
            logger.setLevel(Level.WARNING);

            // Don't forget to disable the parent handlers.
            logger.setUseParentHandlers(false);

            GlobalScreen.setEventDispatcher(new SwingDispatchService());
            GlobalScreen.registerNativeHook();
        } catch (NativeHookException ex) {
            System.err.println("There was a problem registering the native hook.");
            System.err.println(ex.getMessage());

            System.exit(1);
        }

        // Just launches the JavaFX application.
        // Due to way the application is coded, the application will remain running
        // until the user selects the Exit menu option from the tray icon.

        launch(args);
    }

    /**
     * returns a FXML loader
     *
     * @param fxml the fxml name
     * @return FXMLLoader
     */
    private static FXMLLoader loadFXML(String fxml) {
        return new FXMLLoader(StartInBackground.class.getResource("/" + fxml + ".fxml"));
    }
}