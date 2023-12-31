package com.example.partjava;

import Data.GlobalObj;
import Data.UserObj;
import Tools.JavaClient;
import Tools.Password2Hash;
import Tools.ShowAlert;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;

public class CryptoOrderController {
    // class to put purchase or sale order and register the money or the crypto-coins

    @FXML
    public TextField amountTextField;

    @FXML
    private TextField passwordField;

    @FXML
    private void sellOrder() {
        String amount = amountTextField.getText();
        int hash_password = Password2Hash.hashPassword(passwordField.getText().trim());
        String operationInfo;
        // Special case: "-1"
        if ("-1".equals(amount)) {
            // withdraw the existing order
            operationInfo = "pushOrder:" + UserObj.username + " " + "~Sale" + "0.0" + " " + hash_password;
        } else {
            operationInfo = "pushOrder:" + UserObj.username + " " + "Sale" + amount + " " + hash_password;
        }

        double amountVal = Double.parseDouble(amount);

        // Check if amount is invalid
        if (amountVal <= 0) {
            ShowAlert.Error("Invalid Amount", "Amount must be greater than zero.");
            return;
        }

        // Check if amount is greater than available currency
        if (amountVal > UserObj.account.currency) {
            ShowAlert.Error("Not Enough Currency", "Insufficient currency to place the order.");
            return;
        }

        // Check if the user has already ordered
        if (GlobalObj.saleCoins.containsKey(UserObj.username)) {
            ShowAlert.Error("Already Ordered", "You have already placed an order.");
            return;
        }

        JavaClient client = new JavaClient();
        String resInfo = client.sendAndReceive(operationInfo);
        client.close();

        if ("1".equals(resInfo)) {
            // Sale order successful
            UserObj.account.investment.coins -= (float) amountVal;
            GlobalObj.saleCoins.put(UserObj.username, (float) amountVal);
            ShowAlert.Information("Sale Order Placed","Sale order has been successfully placed.");
        } else {
            // Sale order failed
            ShowAlert.Information("Sale Order Failed", "Failed to place the sale order. Please try again.");
        }
    }

    @FXML
    private void buyOrder() {
        String amount = amountTextField.getText();
        int hash_password = Password2Hash.hashPassword(passwordField.getText().trim());
        String operationInfo;
        // Special case: "-1"
        if ("-1".equals(amount)) {
            // withdraw the existing order
            operationInfo = "pushOrder:" + UserObj.username + " " + "~Purchase" + "0.0" + " " + hash_password;
        } else {
            operationInfo = "pushOrder:" + UserObj.username + " " + "Purchase" + amount + " " + hash_password;
        }

        double amountVal = Double.parseDouble(amount);

        // Check if amount is invalid
        if (amountVal <= 0) {
            ShowAlert.Error("Invalid Amount", "Amount must be greater than zero.");
            return;
        }

        // Check if amount is greater than available currency
        if (amountVal > UserObj.account.investment.coins) {
            ShowAlert.Error("Not Enough Currency", "Insufficient coins to place the order.");
            return;
        }

        // Check if the user has already ordered
        if (GlobalObj.buyCoins.containsKey(UserObj.username)) {
            ShowAlert.Error("Already Ordered", "You have already placed an order.");
            return;
        }

        JavaClient client = new JavaClient();
        String resInfo = client.sendAndReceive(operationInfo);
        client.close();

        if ("1".equals(resInfo)) {
            UserObj.account.investment.coins -= (float) amountVal;
            GlobalObj.saleCoins.put(UserObj.username, (float) amountVal);
            ShowAlert.Information("Purchase Order Placed", "Purchase order has been successfully placed.");
        } else {
            ShowAlert.Error("Purchase Order Failed", "Failed to place the purchase order. Please try again.");
        }
    }
}

