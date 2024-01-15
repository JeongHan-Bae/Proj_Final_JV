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

public class VirementController {

    @FXML
    private Label currencyLabel;

    @FXML
    private TextField beneficiaryField;

    @FXML
    private TextField amountField;

    @FXML
    private PasswordField passwordField;

    public Button returnButton;

    @FXML
    public void initialize() {
        // Initialize labels with the account's currency from UserObj
        currencyLabel.setText("Devise : " + UserObj.account.currency);
    }

    @FXML
    private void transfer() {
        // function to transfer
        String beneficiary = beneficiaryField.getText();
        int hash_password = Password2Hash.hashPassword(passwordField.getText().trim());
        double amount;
        if (beneficiary.equals(UserObj.username)){
            ShowAlert.Warning("Attention", "Impossible de transférer vers soi-même.");
            return;
        }

        try {
            amount = Double.parseDouble(amountField.getText());
            if (amount <= 0.0 || amount > UserObj.account.currency) {
                ShowAlert.Error("Erreur", "Montant invalide.");
                return;
            }
        } catch (NumberFormatException e) {
            ShowAlert.Error("Erreur", "Format de montant invalide. Veuillez entrer un nombre valide.");
            return;
        }

        // Check for space in beneficiary username
        if (beneficiary.contains(" ")) {
            ShowAlert.Error("Erreur", "Nom d'utilisateur du bénéficiaire invalide.");
            return;
        }

        JavaClient client = new JavaClient();
        String apply = client.sendAndReceive("transfer:" + UserObj.username + " " + amount + " " + beneficiary + " " + hash_password);
        client.close();

        switch (apply) {
            case "Wrong password":
                ShowAlert.Error("Virement", "Mot de passe incorrect.");
                break;
            case "Beneficiary doesn't exist":
                ShowAlert.Error("Virement", "Le bénéficiaire n'existe pas.");
                break;
            case "success":
                UserObj.account.currency -= (float) amount;
                initialize();
                ShowAlert.Information("Virement", "Transfert réussi.");
                break;
            default:
                ShowAlert.Error("Virement", "Échec du transfert.");
                break;
        }
    }

    @FXML
    private void onReturnButtonClick() {
        SceneNavigator.getToInterface("UsersInterface.fxml", returnButton);
    }
}
