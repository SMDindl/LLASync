package com.learner.controllers;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URISyntaxException;

import com.learner.game.App;
import com.learner.model.Facade;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;

public class LoginController {

    private final Facade facade = Facade.getInstance();

    @FXML
    private Button backButton;

    @FXML
    private TextField emailBox;

    @FXML
    private Button loginButton;

    @FXML
    private TextField passwordBox;

    @FXML
    public void goToHome(ActionEvent event) throws IOException {
        App.setRoot("home");
    }

    @FXML
    public void goToMain(ActionEvent event) throws IOException, URISyntaxException {
        handleProfileImage();
        loginIn(); // root is set here if login is successful
    }

    public void handleProfileImage() throws IOException, URISyntaxException {
        InputStream is = getClass().getResourceAsStream("/com/learner/game/profile_picture.png");
        File testFile = new File(is.toString());
        FileOutputStream out = new FileOutputStream(testFile);
        copyStream(is, out);
        is.close();
        out.close();
        System.out.println(testFile.toString());
        Image defaultPicture = new Image(testFile.toURI().toString());
        ImageModel.setCurrentImage(defaultPicture);
        if(testFile.exists()) {
            testFile.delete();
        }
    }

    private void loginIn() throws IOException {
        String email = emailBox.getText(); 
        String password = passwordBox.getText(); 
        boolean signedIn = facade.loginUser(email, password);
        if(signedIn) {
            App.setRoot("main");
        } else {
            Alert alert = new Alert(AlertType.INFORMATION);
            alert.setTitle(" Login Failed");
            alert.setHeaderText(null);
            alert.setContentText("Invalid email or password.");

            // Set a custom icon for the alert
            Stage stage = (Stage) alert.getDialogPane().getScene().getWindow();
            stage.getIcons().add(new Image(getClass().getResourceAsStream("/com/learner/game/fxml-images/logo.png")));
            ImageView customIcon = new ImageView(new Image(getClass().getResourceAsStream("/com/learner/game/fxml-images/red-x.png")));
            customIcon.setFitHeight(48); // Set the desired size
            customIcon.setFitWidth(48); 
            alert.setGraphic(customIcon);

            // Show the alert
            alert.showAndWait();
        }
    }

    public static void copyStream(InputStream in, OutputStream out) throws IOException {
        byte[] buffer = new byte[1024];
        int read;
        while ((read = in.read(buffer)) != -1) {
            out.write(buffer, 0, read);
        }
    }

}
