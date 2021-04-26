package com.passswordmanager.Util;

import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvException;
import com.passswordmanager.Controllers.PasswordListController;
import com.passswordmanager.Database.DatabaseConnectionHandler;
import com.passswordmanager.Datatypes.Account;
import com.passswordmanager.Datatypes.MasterPassword;
import com.passswordmanager.Datatypes.Program;
import javafx.application.Platform;
import javafx.scene.control.Alert;
import javafx.scene.control.ChoiceDialog;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import javax.swing.*;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class PasswordCSV {
    private final DatabaseConnectionHandler db;
    private final Stage stage;

    private final List<Program> programList;
    private final MasterPassword masterPassword;
    private final List<String> urlErrors;
    private PasswordListController passwordListController;

    public PasswordCSV(DatabaseConnectionHandler db, Stage stage, MasterPassword masterPassword) {
        this.db = db;
        this.stage = stage;
        this.masterPassword = masterPassword;
        this.urlErrors = new ArrayList<>();
        programList = new ArrayList<>();
    }

    public void importPasswords() {

        List<String> choices = new ArrayList<>();
        choices.add("Google Chrome");
        choices.add("Mozilla Firefox");
        choices.add("Opera (GX)");

        ChoiceDialog<String> dialog = new ChoiceDialog<>("Google Chrome", choices);
        dialog.setTitle("Choice BrowserCSV Format");
        dialog.setHeaderText("The information about the browser is needed, to correctly import the password-list");
        dialog.setContentText("Choose the BrowserCSV format:");

        Stage stage = (Stage) dialog.getDialogPane().getScene().getWindow();
        stage.setAlwaysOnTop(true);
        stage.toFront();

        Optional<String> result = dialog.showAndWait();
        result.ifPresent(letter -> {
            switch (letter) {
                case "Google Chrome" -> importGoogleChrome();
                case "Mozilla Firefox" -> importMozillaFirefox();
                case "Opera (GX)" -> importOpera();
            }
        });
    }

    private void importOpera() {
    }

    private void importMozillaFirefox() {
        startParser(0, 1, 2);
    }

    public void importGoogleChrome() {
        startParser(1, 2, 3);
    }

    public void startParser(int posUrl, int posUName, int posPwd) {
        File file = getFile();

        Thread thread = new Thread(() -> {
            if (file == null) return;

            try (CSVReader reader = new CSVReader(new FileReader(file))) {
                List<String[]> result = reader.readAll();
                result.remove(0);

                ThreadPoolExecutor threadPoolExecutor = (ThreadPoolExecutor) Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
                List<ParseURL> tasks = new ArrayList<>();
                result.forEach(strings -> tasks.add(new ParseURL(strings, posUrl, posUName, posPwd)));
                tasks.forEach(threadPoolExecutor::execute);

                threadPoolExecutor.shutdown();

                if (threadPoolExecutor.awaitTermination(1, TimeUnit.HOURS)) {
                    tasks.forEach(this::convert);
                    System.out.println("Tasks completed: " + threadPoolExecutor.getCompletedTaskCount());
                    addListToDB();
                    Platform.runLater(() -> {
                        passwordListController.loadTable(masterPassword.getPassword());
                        masterPassword.clearPasswordCache();
                    });
                    JOptionPane.showMessageDialog(null, "Import has finished!", "Info:", JOptionPane.INFORMATION_MESSAGE);

                }

            } catch (IOException | CsvException | InterruptedException e) {
                e.printStackTrace();
            }
        });
        thread.start();
        System.out.println("Thread started");

        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Information");
        alert.setHeaderText(null);
        alert.setContentText("You will get a message, if the import has finished.");

        Stage stage = (Stage) alert.getDialogPane().getScene().getWindow();
        stage.setAlwaysOnTop(true);
        stage.toFront();
    }

    private File getFile() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open CSV File");
        return fileChooser.showOpenDialog(this.stage);
    }


    public void addListToDB() {
        for (Program program : programList) {
            System.out.println(program.getTitle());
            for (Account account : program.getPasswords()) {
                System.out.println("ACCOUNT: " + account.getUsername());
                db.insert(account.getUsername(), FileCrypt.encryptText(account.getPassword(), masterPassword.getPassword()), program.getTitle(), "");
                masterPassword.clearPasswordCache();
            }
        }
    }

    private void convert(ParseURL parseURL) {

        Program program = appendProgram(parseURL.getTitle());
        if (program == null) return;

        Account account = parseURL.getAccount();
        program.appendAccount(account);
        System.out.println(program.getPasswords().size());

    }

    private Program appendProgram(String title) {
        if (title.equals("")) return null;
        System.out.println(title);
        if (programList.size() == 0) {
            Program program = new Program(title);
            programList.add(program);
        }
        for (Program p : programList) {
            if (p.getTitle().equals(title)) {
                return p;
            }
        }
        Program program = new Program(title);
        programList.add(program);
        return program;
    }


    public List<String> getUrlErrors() {
        return urlErrors;
    }

    public void setPasswordListController(PasswordListController passwordListController) {
        this.passwordListController = passwordListController;
    }
}
