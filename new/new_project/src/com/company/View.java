package com.company;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.util.Duration;

import java.net.URL;
import java.sql.SQLException;
import java.util.Random;

public class View {

    @FXML private AnchorPane loginPane;
    @FXML private AnchorPane registerPane;
    @FXML private AnchorPane gamePane;

    @FXML private Label welcomeLabel;
    @FXML private Label randomNumberLabel;
    @FXML private Label gameStatusLabel;
    @FXML private Button timerButton;

    @FXML private TextField loginUsername;
    @FXML private PasswordField loginPassword;

    @FXML private TextField regUsername;
    @FXML private TextField regEmail;
    @FXML private PasswordField regPassword;
    @FXML private PasswordField regConfirmPassword;

    // Game variables
    private String currentUsername = "";
    private int randomNumber = 0;
    private double elapsedTime = 0.0;
    private Timeline timeline;
    private boolean isTimerRunning = false;

    @FXML
    public void showRegister(ActionEvent event) {
        stopTimer();
        loginPane.setVisible(false);
        gamePane.setVisible(false);
        registerPane.setVisible(true);
    }

    @FXML
    public void showLogin(ActionEvent event) {
        stopTimer();
        registerPane.setVisible(false);
        gamePane.setVisible(false);
        loginPane.setVisible(true);
    }

    @FXML
    public void showGame(String userDisplayName) {
        this.currentUsername = userDisplayName;

        loginPane.setVisible(false);
        registerPane.setVisible(false);
        gamePane.setVisible(true);

        try {
            URL imageResource = getClass().getResource("green.jpg");
            if (imageResource != null) {
                String imagePath = imageResource.toExternalForm();
                gamePane.setStyle("-fx-background-color: #2980b9; " +
                        "-fx-background-image: url('" + imagePath + "'); " +
                        "-fx-background-repeat: no-repeat; " +
                        "-fx-background-position: left center; " +
                        "-fx-background-size: contain;");
            } else {
                gamePane.setStyle("-fx-background-color: #2980b9;");
            }
        } catch (Exception e) {
            System.out.println("Could not load green.jpg: " + e.getMessage());
            gamePane.setStyle("-fx-background-color: #2980b9;");
        }

        if (welcomeLabel != null && userDisplayName != null && !userDisplayName.isEmpty()) {
            welcomeLabel.setText("Welcome, " + userDisplayName + "!");
        }

        startNewGame();
    }

    private void startNewGame() {
        Random random = new Random();
        randomNumber = random.nextInt(20);
        randomNumberLabel.setText(String.valueOf(randomNumber));

        elapsedTime = 0.0;
        isTimerRunning = true;
        timerButton.setText("STOP TIMER");
        timerButton.setStyle("-fx-background-color: #e74c3c; -fx-background-radius: 20; -fx-cursor: hand;");
        gameStatusLabel.setText("Timer is running hidden... Tap to stop!");

        if (timeline != null) {
            timeline.stop();
        }

        timeline = new Timeline(new KeyFrame(Duration.millis(100), e -> {
            elapsedTime += 0.1;
        }));
        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.play();
    }

    @FXML
    public void handleTimerClick(ActionEvent event) {
        if (!isTimerRunning) {
            startNewGame();
            return;
        }

        stopTimer();

        // Round elapsedTime to 1 decimal place (e.g. 2.7)
        double exactTime = Math.round(elapsedTime * 10.0) / 10.0;
        String stoppedTimeStr = String.format("%.1fs", exactTime);

        timerButton.setText("Stopped: " + stoppedTimeStr);
        timerButton.setStyle("-fx-background-color: #2ecc71; -fx-background-radius: 20; -fx-cursor: hand;");

        try {
            DB db = new DB();

            boolean saved = db.saveGameData(randomNumber, exactTime);

            if (saved) {
                gameStatusLabel.setText("Saved! Number: " + randomNumber + " | Time: " + stoppedTimeStr + " (Tap to retry)");
            } else {
                gameStatusLabel.setText("Failed to save game data.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Database Error", "Failed to save game score.");
        }
    }

    private void stopTimer() {
        if (timeline != null) {
            timeline.stop();
        }
        isTimerRunning = false;
    }

    @FXML
    public void handleLogin(ActionEvent event) {
        String username = loginUsername.getText().trim();
        String password = loginPassword.getText();

        if (username.isEmpty() || password.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Validation Error", "Please fill in all fields.");
            return;
        }

        showGame(username);
        loginUsername.clear();
        loginPassword.clear();
    }

    @FXML
    public void handleRegister(ActionEvent event) {
        String username = regUsername.getText().trim();
        String email = regEmail.getText().trim();
        String password = regPassword.getText();
        String confirmPassword = regConfirmPassword.getText();

        if (username.isEmpty() || email.isEmpty() || password.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Validation Error", "Please fill in all fields.");
            return;
        }

        if (!password.equals(confirmPassword)) {
            showAlert(Alert.AlertType.ERROR, "Password Error", "Passwords do not match!");
            return;
        }

        try {
            DB db = new DB();
            boolean success = db.insertUser(username, email, password);

            if (success) {
                showAlert(Alert.AlertType.INFORMATION, "Success", "Account created successfully!");
                clearRegisterFields();
                showGame(username);
            } else {
                showAlert(Alert.AlertType.ERROR, "Registration Failed", "Could not save user data.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Database Error", "Failed to connect to the database.");
        }
    }

    private void clearRegisterFields() {
        regUsername.clear();
        regEmail.clear();
        regPassword.clear();
        regConfirmPassword.clear();
    }

    private void showAlert(Alert.AlertType alertType, String title, String content) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}