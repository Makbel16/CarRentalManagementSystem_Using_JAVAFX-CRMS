package application;

import java.io.IOException;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class AppNavigator {
    public static final String LOGIN = "/fxml/Login.fxml";
    public static final String MAIN_LAYOUT = "/fxml/MainLayout.fxml";
    
    private static Stage mainStage;
    
    public static void setStage(Stage stage) {
        mainStage = stage;
    }
    
    public static void loadScene(String fxmlPath) {
        try {
            // Check if resource exists before loading
            java.net.URL resource = AppNavigator.class.getResource(fxmlPath);
            if (resource == null) {
                throw new IOException("FXML file not found: " + fxmlPath + 
                    "\nMake sure 'resources' folder is configured as a source folder in Eclipse.");
            }
            
            Parent root = FXMLLoader.load(resource);
            if (root == null) {
                throw new IOException("Failed to load FXML content from: " + fxmlPath);
            }
            
            Scene scene = new Scene(root);
            mainStage.setScene(scene);
            mainStage.centerOnScreen();
            mainStage.setTitle("Car Rental Management System");
        } catch (IOException e) {
            System.err.println("Error loading FXML: " + fxmlPath);
            System.err.println("Error details: " + e.getMessage());
            e.printStackTrace();
        } catch (Exception e) {
            System.err.println("Unexpected error loading FXML: " + fxmlPath);
            e.printStackTrace();
        }
    }
}
