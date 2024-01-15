package com.example.partjava;

import Data.UserObj;
import Tools.JavaClient;
import Tools.Password2Hash;
import Tools.ShowAlert;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

public class LoanController {

    @FXML
    private Label currencyLabel;

    @FXML
    private Label debtLabel;

    @FXML
    private TextField loanAmountField;

    @FXML
    private PasswordField passwordField;

    public Button returnButton;

    private static final double rate = 0.0025;
    private static final double loanLimit = -3000;

    @FXML
    public void initialize() {
        // Initialize labels with current values from UserObj
        currencyLabel.setText("Devise : " + UserObj.account.currency);
        debtLabel.setText("Dette : " + UserObj.account.debt);
    }

    @FXML
    private void applyLoan() {
        String amountStr = loanAmountField.getText();
        double amount;

        try {
            amount = Double.parseDouble(amountStr);
        } catch (NumberFormatException e) {
            // Handle the case where parsing fails (e.g., non-numeric input)
            ShowAlert.Error("Erreur", "Montant invalide. Veuillez entrer un nombre valide.");
            return;
        }
        int hash_password = Password2Hash.hashPassword(passwordField.getText().trim());

        if (amount <= 0.0) {
            ShowAlert.Error("Erreur", "Montant invalide.");
            return;
        }
        if (UserObj.account.debt - amount * (1 + rate) < loanLimit) {
            ShowAlert.Error("Erreur", "Limite de crédit dépassée.");
        } else {
            JavaClient client = new JavaClient();
            String apply = client.sendAndReceive("applyLoan:" + UserObj.username + " " + amount + " " + rate + " " + hash_password);
            client.close();

            if ("1".equals(apply)) {
                UserObj.account.debt -= (float) (amount * (1 + rate));
                UserObj.account.currency += (float) amount;
                initialize();
                ShowAlert.Information("Prêt", "Demande de prêt réussie.");
            } else {
                ShowAlert.Error("Prêt", "Demande de prêt échouée.");
            }
        }
    }

    @FXML
    private void onReturnButtonClick() {
        SceneNavigator.getToInterface("UsersInterface.fxml", returnButton);
    }
}
