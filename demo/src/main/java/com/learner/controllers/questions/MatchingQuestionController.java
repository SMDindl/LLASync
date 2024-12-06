package com.learner.controllers.questions;

import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.ResourceBundle;

import com.learner.model.Facade;
import com.learner.model.questions.MatchingQuestion;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;

public class MatchingQuestionController implements Initializable {

    private final Facade facade = Facade.getInstance();
    private MatchingQuestion currentQuestion = (MatchingQuestion) facade.getQuizQuestion();

    private final HashMap<Button, String> leftButtonMap = new HashMap<>();
    private final HashMap<Button, String> rightButtonMap = new HashMap<>();
    private final List<Pair<Button, Button>> selectedPairs = new ArrayList<>(3);

    private Button selectedLeftButton;
    private Button selectedRightButton;

    private final String[] colors = {"-fx-background-color: lightpink;", "-fx-background-color: teal;", "-fx-background-color: orange;"};
    private final List<String> availableColors = new ArrayList<>(List.of(colors));

    @FXML
    private Button clearButton;

    @FXML
    private ImageView exitButton;

    @FXML
    private Button leftButton1;

    @FXML
    private Button leftButton2;

    @FXML
    private Button leftButton3;

    @FXML
    private Button rightButton1;

    @FXML
    private Button rightButton2;

    @FXML
    private Button rightButton3;

    @FXML
    private Button submit;

    @FXML
    private Label title;

    @FXML
    private Label correctIncorrectDisplayText;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        title.setText(facade.getCurrentGame().getGameTitle());
        loadQuestion();
        submit.setDisable(true); // Disable submit button initially
    }

    private void loadQuestion() {
        ArrayList<String> leftSide = currentQuestion.getLeftSide();
        ArrayList<String> rightSide = currentQuestion.getRightSide();

        List<Button> leftButtons = List.of(leftButton1, leftButton2, leftButton3);
        for (int i = 0; i < 3; i++) {
            Button button = leftButtons.get(i);
            button.setText(leftSide.get(i));
            leftButtonMap.put(button, leftSide.get(i));
            button.setOnAction(event -> handleLeftButtonClick(button));
        }

        List<Button> rightButtons = List.of(rightButton1, rightButton2, rightButton3);
        for (int i = 0; i < 3; i++) {
            Button button = rightButtons.get(i);
            button.setText(rightSide.get(i));
            rightButtonMap.put(button, rightSide.get(i));
            button.setOnAction(event -> handleRightButtonClick(button));
        }
    }

    private void handleLeftButtonClick(Button button) {
        if (selectedLeftButton != null) {
            selectedLeftButton.setStyle(""); // Reset previous left button style
        }
        selectedLeftButton = button;
        selectedLeftButton.setStyle(availableColors.get(0)); // Highlight selected left button

        if (selectedRightButton != null) {
            pairButtons(selectedLeftButton, selectedRightButton);
        }
    }

    private void handleRightButtonClick(Button button) {
        if (selectedRightButton != null) {
            selectedRightButton.setStyle(""); // Reset previous right button style
        }
        selectedRightButton = button;
        selectedRightButton.setStyle(availableColors.get(0)); // Highlight selected right button

        if (selectedLeftButton != null) {
            pairButtons(selectedLeftButton, selectedRightButton);
        }
    }

    private void pairButtons(Button leftButton, Button rightButton) {
        String color = availableColors.remove(0);
        selectedPairs.add(new Pair<>(leftButton, rightButton));
        leftButton.setStyle(color);
        rightButton.setStyle(color);

        selectedLeftButton = null;
        selectedRightButton = null;

        submit.setDisable(false); // Enable submit button when a pair is selected
        disableButtons(leftButton, rightButton); // Disable the selected pair
    }

    private void disableButtons(Button leftButton, Button rightButton) {
        leftButton.setDisable(true);
        rightButton.setDisable(true);
    }

    @FXML
    private void clearAllPairLinks(ActionEvent event) {
        for (Pair<Button, Button> pair : selectedPairs) {
            pair.getKey().setStyle("");
            pair.getValue().setStyle("");
            pair.getKey().setDisable(false);
            pair.getValue().setDisable(false);
        }
        selectedPairs.clear();
        availableColors.clear();
        availableColors.addAll(List.of(colors));
        submit.setDisable(true); // Disable submit button
    }

    @FXML
    private void submitQuestion(ActionEvent event) {
        // Create a right side to compare with correct right side out of select pairs
        // use selected pairs, if correct count = 3 then its fully correct

        int correctCount = 0;
        HashMap<String, String> correctPairs = currentQuestion.getCorrectPairs();

        for (Pair<Button, Button> pair : selectedPairs) {
            String leftWord = pair.getKey().getText();
            String selectedMeaning = pair.getValue().getText();

            if (correctPairs.containsKey(leftWord) && correctPairs.get(leftWord).equals(selectedMeaning)) {
                correctCount++;
            }
        }

        // Disable clear button after submitting
        clearButton.setDisable(true);
        
        // Print the number of correct answers to the terminal
        if (correctCount == 3) {
            correctIncorrectDisplayText.setText("All pairs are correct! Awesome job!");
            submit.setText("Continue");
        } else {
            correctIncorrectDisplayText.setText(correctCount + "/3 correct pairs. Nice Try!");
            submit.setText("View Answer");
        }
        correctIncorrectDisplayText.setVisible(true);

    }

    private static class Pair<K, V> {
        private final K key;
        private final V value;

        public Pair(K key, V value) {
            this.key = key;
            this.value = value;
        }

        public K getKey() {
            return key;
        }

        public V getValue() {
            return value;
        }
    }
}