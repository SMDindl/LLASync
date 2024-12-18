package com.learner.controllers;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.ResourceBundle;
import java.util.UUID;

import com.learner.game.App;
import com.learner.model.Difficulty;
import com.learner.model.Facade;
import com.learner.model.Game;
import com.learner.model.GameManager;
import com.learner.model.Language;
import com.learner.model.innerdata.GameCategory;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

public class GameSelectController implements Initializable {

    private final Facade facade = Facade.getInstance();
    private final Language currentLanguage = facade.getCurrentLanguage();
    private final Difficulty currentDifficulty = facade.getCurrentDifficulty();

    @FXML
    private Button backButton;

    @FXML
    private Text category1;

    @FXML
    private Text category2;

    @FXML
    private Text category3;

    @FXML
    private HBox hbox1;

    @FXML
    private HBox hbox2;

    @FXML
    private HBox hbox3;

    @FXML
    private Label title;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        HashMap<GameCategory, ArrayList<Game>> gamesByCategory = GameManager.getInstance().getGamesByCategory(currentLanguage.getUUID(), currentDifficulty);

        for (GameCategory category : gamesByCategory.keySet()) {
            ArrayList<Game> games = gamesByCategory.get(category);
            HBox targetHBox = getTargetHBox(category);

            for (Game game : games) {
                Button gameButton = new Button();
                String gameTitle = game.getGameTitle();
                UUID gameUUID = game.getUUID();
                gameButton.setUserData(gameUUID);
                gameButton.setMaxWidth(98);
                gameButton.setMaxHeight(85);
                gameButton.setPrefWidth(98);
                gameButton.setPrefHeight(85);
                gameButton.setMinWidth(100);
                gameButton.setMinHeight(80);

                // Check if the image file exists
                String imagePath = "/com/learner/game/game-select-icons/" + gameTitle + ".png";
                URL imageUrl = getClass().getResource(imagePath);
                if (imageUrl != null) {
                    // Set the button's graphic to the image
                    ImageView imageView = new ImageView(new Image(imageUrl.toString()));
                    imageView.setFitWidth(100);  // Adjust the width as needed
                    imageView.setFitHeight(100); // Adjust the height as needed
                    imageView.setPreserveRatio(true);
                    gameButton.setGraphic(imageView);
                } else {
                    // Fallback to setting the button's text to the game name with size 8 font
                    gameButton.setText(gameTitle);
                    gameButton.setFont(Font.font(10));
                    // System.out.println("image file not found:" + gameTitle);
                }

                // Add the button to the appropriate HBox
                targetHBox.getChildren().add(gameButton);

                // Set up the event handler
                gameButton.setOnAction(event -> {
                    UUID clickedUUID = (UUID) gameButton.getUserData();
                    facade.selectGame(clickedUUID);
                    try {
                        goToGameIntroduction(event);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                });
            }
        }

        // Remove empty HBoxes
        if (hbox1.getChildren().isEmpty()) {
            hbox1.setVisible(false);
            hbox1.setManaged(false);
            category1.setVisible(false);
        }
        if (hbox2.getChildren().isEmpty()) {
            hbox2.setManaged(false);
            hbox2.setVisible(false);
            category2.setVisible(false);
        }
        if (hbox3.getChildren().isEmpty()) {
            hbox3.setManaged(false);
            hbox3.setVisible(false);
            category3.setVisible(false);
        }

        title.setText(currentDifficulty.getLabel() + " " + currentLanguage.getLanguageName() + " Games");
    }

    private HBox getTargetHBox(GameCategory category) {
        switch (category) {
            case WORD:
                category1.setText(category.getCategory() + ": " + category.getDescription());
                return hbox1;
            case ALPHABET:
            case CULTURE:
                category2.setText(category.getCategory() + ": " + category.getDescription());
                return hbox2;
            case STORY:
                category3.setText(category.getCategory() + ": " + category.getDescription());
                return hbox3;
            default:
                return new HBox(); // Default case, should not happen
        }
    }

    @FXML
    private void goToLanguageSelect(ActionEvent event) {
        try {
            App.setRoot("setDifficulty");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void goToGameIntroduction(ActionEvent event) throws IOException {
        App.setRoot("gameIntroScreen");
    }
}
