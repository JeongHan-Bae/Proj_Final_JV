package com.example.partjava;

import Tools.JavaClient;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.stage.Stage;
import javafx.scene.control.TextField;
import java.io.IOException;

public class LoginController {

    @FXML
    private TextField loginField;

    @FXML
    private TextField mdpField;

    @FXML
    private void connexionClick() {
        String email = loginField.getText();
        String password = mdpField.getText();
        String loginInfo = "userLogin:" + email + " " + password;
        System.out.println(loginInfo);
        JavaClient client = new JavaClient();
        String userInfo = client.sendAndReceive(loginInfo);
        System.out.println(userInfo);
        client.close();

        if ("Invalid user data".equals(userInfo)) {
            showAlert("Invalid User Data", "Invalid user data. Please check your credentials.");
        } else {
            System.out.println(userInfo);
        }
    }

    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    @FXML
    private void createNewAcc() {
        try {
            // Load the FXML file for the new account
            FXMLLoader loader = new FXMLLoader(getClass().getResource("NewAccount.fxml"));
            Parent root = loader.load();

            // Create a new stage for the new account
            Stage newAccountStage = new Stage();
            newAccountStage.setTitle("New Account");
            newAccountStage.setScene(new Scene(root));

            // Close the current login stage
            Stage currentStage = (Stage) loginField.getScene().getWindow();
            currentStage.close();

            // Show the new account stage
            newAccountStage.show();
        } catch (IOException e) {
            e.printStackTrace();
            // Handle exception if FXML file loading fails
        }
    }
}
