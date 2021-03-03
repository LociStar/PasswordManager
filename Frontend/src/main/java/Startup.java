import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;

public class Startup extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    private static Scene scene;

    @Override
    public void start(Stage stage) throws IOException {

        scene = new Scene(loadFXML("LoginPage"));
        stage.setScene(scene);
        stage.initStyle(StageStyle.DECORATED);
        stage.show();

        startBackground(stage);


    }

    @Override
    public void stop() throws IOException {
        setRoot("loginPage");
    }

    static void setRoot(String fxml) throws IOException {
        scene.setRoot(loadFXML(fxml));
    }

    private static void startBackground(Stage stage){
        Image imageTrayIcon = Toolkit.getDefaultToolkit().getImage("E:\\Data\\Bilder\\GamePics\\MainChar.png");
        final TrayIcon trayIcon = new TrayIcon(imageTrayIcon, "title");

        // optional : a listener
        trayIcon.addMouseListener(new MouseAdapter() {

            @Override
            public void mouseClicked(MouseEvent e) {

                if (e.getClickCount() == 2 && !e.isConsumed()) {
                    e.consume();
                    try {
                        setRoot("loginPage");
                    } catch (IOException ioException) {
                        ioException.printStackTrace();
                    }
                }
            }
        });
        // optional : adding a popup menu for the icon
        PopupMenu popup = new PopupMenu("Test");
        MenuItem exitItem = new MenuItem("Exit");
        exitItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                System.exit(1);
            }
        });
        popup.add(exitItem);
        trayIcon.setPopupMenu(popup);

        // mandatory
        try {
            SystemTray.getSystemTray().add(trayIcon);
        }
        catch (AWTException e1) {
            // process the exception
        }
    }

    private static Parent loadFXML(String fxml) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(Startup.class.getResource(fxml + ".fxml"));
        return fxmlLoader.load();
    }
}
