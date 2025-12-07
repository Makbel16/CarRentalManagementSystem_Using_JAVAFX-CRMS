package controllers.customers;

//In BaseController.java

import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.layout.StackPane;

public abstract class BaseController {
 @FXML protected StackPane contentPane;
 
 protected void showContent(Node node) {
     contentPane.getChildren().setAll(node);
 }
 
 protected void showError(String message) {
     Alert alert = new Alert(Alert.AlertType.ERROR);
     alert.setTitle("Error");
     alert.setHeaderText(null);
     alert.setContentText(message);
     alert.showAndWait();
 }
}