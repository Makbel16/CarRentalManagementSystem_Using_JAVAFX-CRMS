package controllers;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import javafx.animation.*;
import javafx.util.Duration;
import application.DatabaseConnection;
import models.UserRole;
import controllers.utils.SessionManager;
import controllers.utils.Alerts;

public class LoginController {
    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;
    @FXML private TextField passwordVisibleField;
    @FXML private Label errorLabel;
    @FXML private Button togglePasswordBtn;
    @FXML private ImageView carImage;
    
    // Typewriter animation labels
    @FXML private Label welcomeText;
    @FXML private Label toText;
    @FXML private Label carRentalText;
    @FXML private Label systemText;
    @FXML private Label subtitleText;
    @FXML private Label cursor;
    
    private boolean isPasswordVisible = false;
    private Timeline cursorBlinkTimeline;
    private SequentialTransition typewriterAnimation;
    private boolean isAnimating = false;
    
    @FXML
    public void initialize() {
        // Setup single password toggle button
        togglePasswordBtn.setOnAction(e -> togglePasswordVisibility());
        
        // Bind password fields together
        passwordField.textProperty().addListener((obs, oldVal, newVal) -> {
            if (!isPasswordVisible) {
                passwordVisibleField.setText(newVal);
            }
        });
        
        passwordVisibleField.textProperty().addListener((obs, oldVal, newVal) -> {
            if (isPasswordVisible) {
                passwordField.setText(newVal);
            }
        });
        
        // FIXED: Only clear error when user starts typing in a field that was empty
        usernameField.textProperty().addListener((obs, oldVal, newVal) -> {
            // Only clear error if the field was empty and user starts typing
            if (oldVal.isEmpty() && !newVal.isEmpty()) {
                errorLabel.setText("");
            }
        });
        
        passwordField.textProperty().addListener((obs, oldVal, newVal) -> {
            // Only clear error if the field was empty and user starts typing
            if (oldVal.isEmpty() && !newVal.isEmpty()) {
                errorLabel.setText("");
            }
        });
        
        // Start animations
        startCarAnimation();
        startTypewriterAnimation();
        
        // Make animation loop continuously
        setupAnimationLoop();
    }
    
    private void startCarAnimation() {
        if (carImage != null) {
            // Subtle floating animation
            TranslateTransition floatAnim = new TranslateTransition(Duration.seconds(6), carImage);
            floatAnim.setFromY(0);
            floatAnim.setToY(-8);
            floatAnim.setCycleCount(TranslateTransition.INDEFINITE);
            floatAnim.setAutoReverse(true);
            floatAnim.setInterpolator(Interpolator.EASE_BOTH);
            floatAnim.play();
        }
    }
    
    private void startTypewriterAnimation() {
        // Clear all text initially
        clearAllText();
        
        // Create cursor blinking animation
        cursorBlinkTimeline = new Timeline(
            new KeyFrame(Duration.millis(0), e -> cursor.setOpacity(0)),
            new KeyFrame(Duration.millis(500), e -> cursor.setOpacity(1)),
            new KeyFrame(Duration.millis(1000), e -> cursor.setOpacity(0))
        );
        cursorBlinkTimeline.setCycleCount(Timeline.INDEFINITE);
        
        // Build the typewriter animation sequence
        buildTypewriterSequence();
    }
    
    private void clearAllText() {
        welcomeText.setText("");
        toText.setText("");
        carRentalText.setText("");
        systemText.setText("");
        subtitleText.setText("");
    }
    
    private void buildTypewriterSequence() {
        typewriterAnimation = new SequentialTransition();
        
        // EXTREMELY FAST - Almost instant typing and deleting
        
        // Type "WELCOME TO" - Extremely fast (15ms per character)

        
        // Type "CAR RENTAL SYSTEM" - Extremely fast (12ms per character)
        typewriterAnimation.getChildren().add(new PauseTransition(Duration.millis(50)));
        typewriterAnimation.getChildren().add(createTypewriterTransition(carRentalText, "WELLCOME TO \n CAR RENTAL \n  MANAGMENT SYSTEM", 5));
        
        // Type subtitle - Extremely fast (10ms per character)
        typewriterAnimation.getChildren().add(new PauseTransition(Duration.millis(50)));
        typewriterAnimation.getChildren().add(createTypewriterTransition(subtitleText, "Your Journey Begins Here", 5));
        
        // Very short pause
        typewriterAnimation.getChildren().add(new PauseTransition(Duration.millis(3000)));
        
        // Delete animations (even faster)
        typewriterAnimation.getChildren().add(createDeleterTransition(subtitleText, "Your Journey Begins Here", 5));
        typewriterAnimation.getChildren().add(new PauseTransition(Duration.millis(40)));
        typewriterAnimation.getChildren().add(createDeleterTransition(carRentalText, "WELLCOM TO \n CAR RENTAL \n MANAGMENT SYSTEM", 5));
        typewriterAnimation.getChildren().add(new PauseTransition(Duration.millis(300)));
        // Minimal pause before restart
        typewriterAnimation.getChildren().add(new PauseTransition(Duration.millis(300)));
        
      
    }

    private SequentialTransition createTypewriterTransition(Label label, String text, int speedMs) {
        SequentialTransition sequence = new SequentialTransition();
        
        for (int i = 0; i <= text.length(); i++) {
            final int index = i;
            PauseTransition pause = new PauseTransition(Duration.millis(speedMs * i));
            pause.setOnFinished(e -> label.setText(text.substring(0, index)));
            sequence.getChildren().add(pause);
        }
        
        return sequence;
    }

    private SequentialTransition createDeleterTransition(Label label, String text, int speedMs) {
        SequentialTransition sequence = new SequentialTransition();
        
        for (int i = text.length(); i >= 0; i--) {
            final int index = i;
            PauseTransition pause = new PauseTransition(Duration.millis(speedMs * (text.length() - i)));
            pause.setOnFinished(e -> label.setText(text.substring(0, index)));
            sequence.getChildren().add(pause);
        }
        
        return sequence;
    }

    private void setupAnimationLoop() {
        // Set to loop indefinitely with no additional delays
        typewriterAnimation.setCycleCount(SequentialTransition.INDEFINITE);
        
        // Start immediately
        typewriterAnimation.play();
        isAnimating = true;
    }
    
    private void togglePasswordVisibility() {
        isPasswordVisible = !isPasswordVisible;
        
        if (isPasswordVisible) {
            // Show password in plain text
            passwordVisibleField.setText(passwordField.getText());
            passwordVisibleField.setManaged(true);
            passwordVisibleField.setVisible(true);
            passwordField.setManaged(false);
            passwordField.setVisible(false);
            togglePasswordBtn.setText("🙈 Hide Password"); // Updated text
        } else {
            // Hide password (show asterisks)
            passwordField.setText(passwordVisibleField.getText());
            passwordField.setManaged(true);
            passwordField.setVisible(true);
            passwordVisibleField.setManaged(false);
            passwordVisibleField.setVisible(false);
            togglePasswordBtn.setText("👁 Show Password"); // Updated text
        }
    }
    
    @FXML
    private void handleLogin() {
        String username = usernameField.getText().trim();
        String password = isPasswordVisible ? 
            passwordVisibleField.getText() : passwordField.getText();

        if (username.isEmpty() || password.isEmpty()) {
            errorLabel.setText("Please enter both username and password");
            errorLabel.setStyle("-fx-text-fill: #ff6b6b;"); // Ensure red color
            shakeAnimation();
            return;
        }

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(
                     "SELECT id, username, role FROM users WHERE username = ? AND password = ?")) {
            
            stmt.setString(1, username);
            stmt.setString(2, password);
            
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                // Get user details
                int userId = rs.getInt("id");
                String roleString = rs.getString("role");
                UserRole role = UserRole.fromString(roleString);
                
                // Initialize session
                SessionManager.getInstance().login(username, userId, role, username);
                
                System.out.println("Login successful: " + SessionManager.getInstance().toString());
                
                successAnimation(() -> {
                    try {
                        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/MainLayout.fxml"));
                        Parent root = loader.load();
                        Stage stage = (Stage) usernameField.getScene().getWindow();
                        stage.setScene(new Scene(root));
                        stage.setTitle("Car Rental Management System - " + role.getDisplayName());
                        stage.setMaximized(true);
                    } catch (Exception e) {
                        Alerts.showError("Error", "Failed to load main application: " + e.getMessage());
                        e.printStackTrace();
                    }
                });
            } else {
                errorLabel.setText("Invalid username or password");
                errorLabel.setStyle("-fx-text-fill: #ff6b6b;"); // Ensure red color
                shakeAnimation();
            }
        } catch (SQLException e) {
            e.printStackTrace();
            errorLabel.setText("Database connection failed. Please try again.");
            errorLabel.setStyle("-fx-text-fill: #ff6b6b;"); // Ensure red color
            shakeAnimation();
        }
    }
    
    private void shakeAnimation() {
        // Reset position first
        errorLabel.setTranslateX(0);
        
        TranslateTransition shake = new TranslateTransition(Duration.millis(70), errorLabel);
        shake.setFromX(0);
        shake.setByX(6);
        shake.setCycleCount(3);
        shake.setAutoReverse(true);
        shake.play();
    }
    
    private void successAnimation(Runnable onComplete) {
        // Stop animations
        if (cursorBlinkTimeline != null) cursorBlinkTimeline.stop();
        if (typewriterAnimation != null) typewriterAnimation.stop();
        
        // Fade out animation
        FadeTransition fadeOut = new FadeTransition(Duration.millis(600), 
            usernameField.getScene().getRoot());
        fadeOut.setFromValue(1);
        fadeOut.setToValue(0);
        fadeOut.setOnFinished(e -> onComplete.run());
        fadeOut.play();
    }
}