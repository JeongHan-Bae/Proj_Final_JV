package com.example.partjava;

import Data.GlobalObj;
import Data.UserObj;
import Tools.JavaClient;
import Tools.Password2Hash;
import Tools.ShowAlert;
import javafx.fxml.FXML;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

public class CryptoOrderController {
    // class to put purchase or sale order and register the money or the crypto-coins

    @FXML
    public TextField amountTextField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private void sellOrder() {
        String amount = amountTextField.getText();
        int hash_password = Password2Hash.hashPassword(passwordField.getText().trim());
        String operationInfo;
        boolean withdraw = false;

        // Special case: "-1"
        if ("-1".equals(amount)) {
            // withdraw the existing order
            operationInfo = "pushOrder:" + UserObj.username + " " + "~Sale " + "0.0" + " " + hash_password;
            withdraw = true;
        } else {
            operationInfo = "pushOrder:" + UserObj.username + " " + "Sale " + amount + " " + hash_password;
        }

        double amountVal = Double.parseDouble(amount);

        // Check if amount is invalid
        if (!withdraw && amountVal <= 0) {
            ShowAlert.Error("Montant invalide", "Le montant doit être supérieur à zéro.");
            return;
        }

        // Check if amount is greater than available currency
        if (!withdraw && amountVal > UserObj.account.investment.coins) {
            ShowAlert.Error("Pas assez de Coins", "Nombre de coins insuffisant pour les mettre en vente.");
            return;
        }

        // Check if the user has already ordered
        if (GlobalObj.saleCoins.containsKey(UserObj.username)) {
            if (!withdraw) {
                ShowAlert.Error("Offre déjà formulée", "Vous avez déjà effectué une mise en vente.");
                return;
            } else {
                amountVal = GlobalObj.saleCoins.get(UserObj.username);
            }
        }

        if (!GlobalObj.saleCoins.containsKey(UserObj.username) && withdraw) {
            ShowAlert.Error("Pas d'offre formulée", "Vous n'avez pas effectué de mise en vente.");
            return;
        }

        JavaClient client = new JavaClient();
        String resInfo = client.sendAndReceive(operationInfo);
        client.close();

        if ("1".equals(resInfo)) {
            if (withdraw) {
                UserObj.account.investment.coins -= (float) amountVal;
                GlobalObj.saleCoins.remove(UserObj.username);
                ShowAlert.Information("Offre retirée de la vente", "L'offre mise en vente a été retirée avec succès.");
            } else {
                UserObj.account.investment.coins += (float) amountVal;
                GlobalObj.saleCoins.put(UserObj.username, (float) amountVal);
                ShowAlert.Information("Offre mise en vente", "L'offre a été mise en vente avec succès.");
            }
        } else {
            ShowAlert.Error("Echec de la mise en vente", "Échec de la mise en vente de votre offre. Veuillez réessayer.");
        }
    }


    @FXML
    private void buyOrder() {
        String amount = amountTextField.getText();
        int hash_password = Password2Hash.hashPassword(passwordField.getText().trim());
        String operationInfo;
        boolean withdraw = false;
        // Special case: "-1"
        if ("-1".equals(amount)) {
            // withdraw the existing order
            operationInfo = "pushOrder:" + UserObj.username + " " + "~Purchase " + "0.0" + " " + hash_password;
            withdraw = true;
        } else {
            operationInfo = "pushOrder:" + UserObj.username + " " + "Purchase " + amount + " " + hash_password;
        }

        double amountVal = Double.parseDouble(amount);
        if(!withdraw) {
            if (amountVal <= 0) {
                ShowAlert.Error("Montant invalide", "Le montant doit être supérieur à zéro.");
                return;
            }

            // Check if amount is greater than available currency
            if (amountVal > UserObj.account.investment.coins) {
                ShowAlert.Error("Pas assez d'argent courrant", "Pas assez de coins pour réaliser l'achat.");
                return;
            }
        }

        // Check if the user has already ordered
        if (GlobalObj.buyCoins.containsKey(UserObj.username)) {
            if(!withdraw){
                ShowAlert.Error("Achat déjà réalisé", "Vous avez déjà réalisé l'achat.");
                return;
            } else {
                amountVal = GlobalObj.buyCoins.get(UserObj.username);
            }

        }

        if (!GlobalObj.buyCoins.containsKey(UserObj.username) && withdraw) {
            ShowAlert.Error("Achat non effectué", "Vous ne pouvez pas réaliser de retrait.");
            return;
        }

        JavaClient client = new JavaClient();
        String resInfo = client.sendAndReceive(operationInfo);
        client.close();

        if ("1".equals(resInfo)) {
            if(withdraw){
                UserObj.account.investment.coins += (float) amountVal;
                GlobalObj.buyCoins.remove(UserObj.username);
                ShowAlert.Information("Achat retiré", "L'achat a été retirée avec succès.");

            } else {
                UserObj.account.investment.coins -= (float) amountVal;
                GlobalObj.buyCoins.put(UserObj.username, (float) amountVal);
                ShowAlert.Information("Achat effectué", "L'achat a été effectué avec succès.");
            }
        } else {
            ShowAlert.Error("Echec de l'achat", "Échec de l'achat. Veuillez réessayer");
        }
    }
}

