package com.example.partjava;

import Tools.JavaClient;
import Tools.Password2Hash;
import Tools.ShowAlert;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

public class NewAccountController {

    @FXML
    public ChoiceBox<String> civilityField;
    @FXML
    private TextField usernameField;

    @FXML
    private TextField firstNameField;

    @FXML
    private TextField familyNameField;

    @FXML
    private TextField telephoneField;

    @FXML
    private TextField emailField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private PasswordField repeatPasswordField;

    public Button createButton;

    public Button clearButton;

    @FXML
    private void initialize() {
        // Initialize civilityField with choices
        ObservableList<String> civilityOptions = FXCollections.observableArrayList("Monsieur", "Madame", " - - ");
        civilityField.setItems(civilityOptions);

    }


    @FXML
    private void onCreateButtonClicked() {
        // check every field to ensure the account infos are valid
        String username = usernameField.getText().trim();
        String civility = civilityField.getValue();

        if (username.isEmpty() || civility == null || civility.isEmpty()) {
            ShowAlert.Warning("Error", "Please fill in all required fields.");
            return;
        }

        String firstName;

        // Check civility and format firstName accordingly
        if ("Monsieur".equals(civility)) {
            firstName = "M.<" + firstNameField.getText().replace(" ", "<");
        } else if ("Madame".equals(civility)) {
            firstName = "Mme.<" + firstNameField.getText().replace(" ", "<");
        } else {
            firstName = firstNameField.getText().replace(" ", "<");
        }
        String familyName = familyNameField.getText().replace(" ", "<");
        String telephone = telephoneField.getText().replace(" ", "");
        // As we use space to split the send infos, spaces in these fields should be replaced
        String email = emailField.getText().trim();
        String password = passwordField.getText();
        String repeatPassword = repeatPasswordField.getText();

        if (familyName.isEmpty() || telephone.isEmpty() || email.isEmpty() || password.isEmpty() || repeatPassword.isEmpty()) {
            ShowAlert.Warning("Error", "Please fill in all required fields.");
            return;
        }

        // As we use space to split the send infos, username with space should be invalid
        if (username.contains(" ")) {
            ShowAlert.Error("Error", "Username cannot contain spaces.");
            return;
        }

        // E-mail address should have no spaces and exactly one @
        if (email.contains(" ") || !email.contains("@") || email.lastIndexOf("@") != email.indexOf("@")) {
            ShowAlert.Error("Error", "Invalid email format.");
            return;
        }

        // Make sure the two passwords match
        if (!password.equals(repeatPassword)) {
            ShowAlert.Error("Error", "Passwords do not match.");
            return;
        }

        // Hash the password
        int hashedPassword = Password2Hash.hashPassword(password);

        // Construct registerInfo string
        String registerInfo = "createNewAcc:" + username + " " + hashedPassword + " " + firstName + " " + familyName + " " + telephone + " " + email;

        JavaClient client = new JavaClient();
        // Send the registration info to the server
        String attempt = client.sendAndReceive(registerInfo);

        client.close();

        System.out.println(attempt);
        if ("1".equals(attempt)) {
            // New account created successfully
            ShowAlert.Information("Info", "New account created successfully.");
            // Load the Login.fxml and close the current stage
            SceneNavigator.getToInterface("Login.fxml", createButton, "Login");
        } else{
            // Username already exists
            ShowAlert.Warning("Registration failed","User already exists. Choose a different username.");
        }
    }

    @FXML
    private void onClearButtonClicked() {
        civilityField.setValue(null);
        usernameField.clear();
        firstNameField.clear();
        familyNameField.clear();
        telephoneField.clear();
        emailField.clear();
        passwordField.clear();
        repeatPasswordField.clear();
    }
}
