package com.example.partjava;

import Data.*;
import Tools.*;
import javafx.fxml.FXML;

import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

public class InvestController {
    // The class of buying financial products

    @FXML
    private Label currencyLabel;

    @FXML
    private TextField companyNameField;

    @FXML
    private TextField amountField;

    @FXML
    private PasswordField passwordField;

    @FXML
    public void initialize() {
        updateCurrencyLabel();
    }

    @FXML
    private void onSearchButtonClick() {
        String companyName = companyNameField.getText().trim();
        if (GlobalObj.currDataMap.containsKey(companyName)) {
            // Company found, show the price in a dialog
            Float stockPrice = GlobalObj.currDataMap.get(companyName);
            ShowAlert.Information("Prix des actions", "Le prix d'une action de " + companyName + " est : " + stockPrice);
        } else {
            // Company not found, show an alert
            ShowAlert.Warning("Société non trouvée", "La société " + companyName + " n'a pas été trouvée.");
        }
    }

    @FXML
    private void onPurchaseButtonClick() {
        String companyName = companyNameField.getText().trim();
        String amountText = amountField.getText().trim();
        String password = passwordField.getText().trim();

        if (GlobalObj.currDataMap.containsKey(companyName)) {
            Float stockPrice = GlobalObj.currDataMap.get(companyName);

            try {
                float amount = Float.parseFloat(amountText);

                if (amount > 0) {
                    float totalCost = stockPrice * amount;

                    if (totalCost <= UserObj.account.currency) {
                        // Sufficient funds, proceed with the purchase logic
                        String operationInfo = "buyStock:" + UserObj.username + " " + companyName + " " + amountText + " " + Password2Hash.hashPassword(password);
                        JavaClient client = new JavaClient();
                        String resInfo = client.sendAndReceive(operationInfo);
                        client.close();
                        if (resInfo.equals("Wrong Password")) {
                            ShowAlert.Warning("Mot de passe incorrect", "Le mot de passe saisi est incorrect.");
                        } else if (resInfo.equals("Failed")) {
                            ShowAlert.Warning("Transaction échouée", "La transaction d'achat d'actions a échoué.");
                        } else {
                            // Successful transaction, update UserObj.account.currency
                            UserObj.account.currency = Float.parseFloat(resInfo);
                            updateCurrencyLabel();
                            JavaClient callBank = new JavaClient();
                            // Retrieve updated bank account information
                            String accInfo = callBank.sendAndReceive("getBank:" + UserObj.username);
                            callBank.close();
                            updateBankAcc(accInfo);
                            ShowAlert.Information("Transaction réussie", "Actions achetées avec succès. Devise mise à jour.");
                        }

                    } else {
                        ShowAlert.Warning("Fonds insuffisants", "Vous n'avez pas assez d'argent pour effectuer cet achat.");
                    }
                } else {
                    ShowAlert.Error("Erreur", "Format de montant invalide. Veuillez entrer un montant positif valide.");
                }
            } catch (NumberFormatException e) {
                ShowAlert.Error("Erreur", "Format de montant invalide. Veuillez entrer un nombre valide.");
            }
        } else {
            ShowAlert.Warning("Société non trouvée", "La société " + companyName + " n'a pas été trouvée.");
        }
    }

    @FXML
    private void onSellButtonClick() {
        String companyName = companyNameField.getText().trim();
        String amountText = amountField.getText().trim();
        String password = passwordField.getText().trim();

        if (GlobalObj.currDataMap.containsKey(companyName)) {
            try {
                float amountToSell = Float.parseFloat(amountText);

                if (amountToSell > 0) {
                    // Check if the user has the stock in the investment map
                    Float userStocks = UserObj.account.investment.investMap.getOrDefault(companyName, 0.0f);

                    if (amountToSell <= userStocks) {
                        // Sufficient stocks to sell, proceed with the selling logic
                        String operationInfo = "sellStock:" + UserObj.username + " " + companyName + " " + amountText + " " + Password2Hash.hashPassword(password);
                        JavaClient client = new JavaClient();
                        String resInfo = client.sendAndReceive(operationInfo);
                        client.close();

                        if (resInfo.equals("Wrong Password")) {
                            ShowAlert.Warning("Mot de passe incorrect", "Le mot de passe saisi est incorrect.");
                        } else if (resInfo.equals("Failed")) {
                            ShowAlert.Warning("Transaction échouée", "La transaction de vente d'actions a échoué.");
                        } else {
                            // Successful transaction, update UserObj.account.currency
                            UserObj.account.currency = Float.parseFloat(resInfo);
                            updateCurrencyLabel();
                            JavaClient callBank = new JavaClient();
                            // Retrieve updated bank account information
                            String accInfo = callBank.sendAndReceive("getBank:" + UserObj.username);
                            callBank.close();
                            updateBankAcc(accInfo);
                            ShowAlert.Information("Transaction réussie", "Actions vendues avec succès. Devise mise à jour.");
                        }
                    } else {
                        ShowAlert.Warning("Stocks insuffisants", "Vous n'avez pas assez de stocks pour effectuer cette vente.");
                    }
                } else {
                    ShowAlert.Error("Erreur", "Format de montant invalide. Veuillez entrer un montant positif valide.");
                }
            } catch (NumberFormatException e) {
                ShowAlert.Error("Erreur", "Format de montant invalide. Veuillez entrer un nombre valide.");
            }
        } else {
            ShowAlert.Warning("Société non trouvée", "La société " + companyName + " n'a pas été trouvée.");
        }
    }

    @FXML
    private void onClearButtonClick() {
        // Clear all input fields
        companyNameField.clear();
        amountField.clear();
        passwordField.clear();
    }

    private void updateCurrencyLabel() {
        currencyLabel.setText("Vous avez " + UserObj.account.currency + " en liquide.");
    }

    // Update bank account infos
    private void updateBankAcc(String accInfo){
        LoginController.UpdateAccInfo(accInfo);
    }
}
