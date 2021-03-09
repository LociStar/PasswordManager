package com.passswordmanager.Controllers;

import com.passswordmanager.Util.FileCrypt;
import com.lambdaworks.crypto.SCryptUtil;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.PasswordField;
import javafx.stage.Stage;
import javafx.stage.Window;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class LoginPageController {
    @FXML
    public PasswordField masterPassword;

    private PasswordListController passwordListController;
    private Stage passwordStage;
    private Parent parent;

    public void onUnlockPressed(ActionEvent actionEvent) throws IOException {
        String hash = "$s0$30808$EIjYo1QSYopS4FBUoAJgBw==$Alr+ZkCNpNxnAA2R4PCAYzfSSMF3oj47tpSrad7OA0w=";
        boolean matched = SCryptUtil.check(masterPassword.getText(), hash);
        if (matched) {
            printPasswords(masterPassword.getText());

            Scene scene = masterPassword.getScene();
            Window window = scene.getWindow();
            Stage stage = (Stage) window;
            stage.hide();
            //stage.initStyle(StageStyle.DECORATED);

            //FXMLLoader loader = loadFXML("passwordListUI");
            //Parent parent = loader.load();
            //passwordListController.setLoginPageController(this);
            passwordListController.loadTable(masterPassword.getText());

            //Stage mainStage = new Stage();
            //mainStage.setScene(parent.getScene());
            //mainStage.initStyle(StageStyle.DECORATED);
            //mainStage.setOnHiding(event -> mainStage.hide());

            stage = passwordStage;
            //stage.setScene(newScene);
            stage.show();

            //FXMLLoader loader = new FXMLLoader(); // create and load() view
            //masterPassword.getScene().setRoot(loader.getRoot());
        }

    }

    public void onXClicked() {
        Stage stage = (Stage) masterPassword.getScene().getWindow();
        stage.hide();
    }

    private void printPasswords(String masterPassword) throws FileNotFoundException {
        Map<String, String> hashMap = new HashMap<>();
        hashMap = FileCrypt.getPasswords(masterPassword);
        hashMap.forEach((s, s2) -> System.out.println(s + ": " + s2));

    }

    private static FXMLLoader loadFXML(String fxml) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(LoginPageController.class.getResource(fxml + ".fxml"));
        fxmlLoader.setLocation(LoginPageController.class.getResource("/passwordListUI.fxml"));

        return fxmlLoader;
    }

    public PasswordListController getPasswordListController() {
        return passwordListController;
    }

    public void setPasswordListController(PasswordListController passwordListController) {
        this.passwordListController = passwordListController;
    }

    public Stage getPasswordStage() {
        return passwordStage;
    }

    public void setPasswordStage(Stage passwordStage) {
        this.passwordStage = passwordStage;
    }

    public Parent getParent() {
        return parent;
    }

    public void setParent(Parent parent) {
        this.parent = parent;
    }
}
