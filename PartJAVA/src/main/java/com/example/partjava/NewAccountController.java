package com.example.partjava;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

public class NewAccountController {
    @FXML
    private TextField nameField;

    @FXML
    private TextField phoneField;

    @FXML
    private TextField mailField;

    @FXML
    private TextField passwordField;

    @FXML
    private TextField passwordField2;

    @FXML
    private Label alerteLabel2;

    @FXML
    private Button sInscrireButton;

    @FXML
    private Button clearButton;

    @FXML
    private void inscriptionClick(ActionEvent event) {
        // This method will be called when the "S'inscrire" button is clicked
        String name = nameField.getText();
        String phone = phoneField.getText();
        String email = mailField.getText();
        String password = passwordField.getText();
        String confirmPassword = passwordField2.getText();

        // Perform the registration logic here
        // You can use the input values for creating a new account

        // For demonstration purposes, let's just print the input values
        System.out.println("Name: " + name);
        System.out.println("Phone: " + phone);
        System.out.println("Email: " + email);
        System.out.println("Password: " + password);
        System.out.println("Confirm Password: " + confirmPassword);
    }

    @FXML
    private void clearClick(ActionEvent event) {
        // This method will be called when the "Clear" button is clicked
        nameField.clear();
        phoneField.clear();
        mailField.clear();
        passwordField.clear();
        passwordField2.clear();
        alerteLabel2.setText(""); // Clear any previous alert messages
    }
}
