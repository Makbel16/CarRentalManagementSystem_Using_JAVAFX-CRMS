package application;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.io.File;
import java.net.URL;

public class Main extends Application {
    private static Stage primaryStage;

    @Override

    public void start(Stage primaryStage) {
        Main.primaryStage = primaryStage;
        try {
            FXMLLoader loader = new FXMLLoader();

            // Correct location - DO NOT include 'resources' in the path
            URL fxmlUrl = getClass().getResource("/fxml/Login.fxml");

            if (fxmlUrl == null) {
                throw new Exception("Login.fxml not found! Check if 'resources/fxml' is marked as a source folder.");
            }

            System.out.println("Loading FXML from: " + fxmlUrl);

            loader.setLocation(fxmlUrl);
            Parent root = loader.load();

            Scene scene = new Scene(root, 900, 600);
            primaryStage.setTitle("Car Rental System - Login");
            primaryStage.setScene(scene);
            primaryStage.setMinWidth(900);
            primaryStage.setMinHeight(600);
            primaryStage.setResizable(true);
            primaryStage.show();

        } catch (Exception e) {
            System.err.println("Error loading application: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Get the URL of a resource file
     */
    private URL getResource(String path) {
        // Remove leading slash if present
        if (path.startsWith("/")) {
            path = path.substring(1);
        }
        return getClass().getClassLoader().getResource(path);
    }

    public static void main(String[] args) {
        launch(args);
    }
}
