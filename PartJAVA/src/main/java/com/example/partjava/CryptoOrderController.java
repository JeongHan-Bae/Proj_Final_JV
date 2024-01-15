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
            ShowAlert.Error("Invalid Amount", "Amount must be greater than zero.");
            return;
        }

        // Check if amount is greater than available currency
        if (!withdraw && amountVal > UserObj.account.investment.coins) {
            ShowAlert.Error("Not Enough Coins", "Insufficient coins to place the sale order.");
            return;
        }

        // Check if the user has already ordered
        if (GlobalObj.saleCoins.containsKey(UserObj.username)) {
            if (!withdraw) {
                ShowAlert.Error("Already Ordered", "You have already placed a sale order.");
                return;
            } else {
                amountVal = GlobalObj.saleCoins.get(UserObj.username);
            }
        }

        if (!GlobalObj.saleCoins.containsKey(UserObj.username) && withdraw) {
            ShowAlert.Error("Not yet Ordered", "You have no sale order to withdraw.");
            return;
        }

        JavaClient client = new JavaClient();
        String resInfo = client.sendAndReceive(operationInfo);
        client.close();

        if ("1".equals(resInfo)) {
            if (withdraw) {
                UserObj.account.investment.coins -= (float) amountVal;
                GlobalObj.saleCoins.remove(UserObj.username);
                ShowAlert.Information("Sale Order Withdrawn", "Sale order has been successfully withdrawn.");
            } else {
                UserObj.account.investment.coins += (float) amountVal;
                GlobalObj.saleCoins.put(UserObj.username, (float) amountVal);
                ShowAlert.Information("Sale Order Placed", "Sale order has been successfully placed.");
            }
        } else {
            ShowAlert.Error("Sale Order Failed", "Failed to place the sale order. Please try again.");
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
                ShowAlert.Error("Invalid Amount", "Amount must be greater than zero.");
                return;
            }

            // Check if amount is greater than available currency
            if (amountVal > UserObj.account.investment.coins) {
                ShowAlert.Error("Not Enough Currency", "Insufficient coins to place the order.");
                return;
            }
        }

        // Check if the user has already ordered
        if (GlobalObj.buyCoins.containsKey(UserObj.username)) {
            if(!withdraw){
                ShowAlert.Error("Already Ordered", "You have already placed an order.");
                return;
            } else {
                amountVal = GlobalObj.buyCoins.get(UserObj.username);
            }

        }

        if (!GlobalObj.buyCoins.containsKey(UserObj.username) && withdraw) {
            ShowAlert.Error("Not yet Ordered", "You have no order to withdraw.");
            return;
        }

        JavaClient client = new JavaClient();
        String resInfo = client.sendAndReceive(operationInfo);
        client.close();

        if ("1".equals(resInfo)) {
            if(withdraw){
                UserObj.account.investment.coins += (float) amountVal;
                GlobalObj.buyCoins.remove(UserObj.username);
                ShowAlert.Information("Purchase Order Withdrawn", "Purchase order has been successfully withdrawn.");

            } else {
                UserObj.account.investment.coins -= (float) amountVal;
                GlobalObj.buyCoins.put(UserObj.username, (float) amountVal);
                ShowAlert.Information("Purchase Order Placed", "Purchase order has been successfully placed.");
            }
        } else {
            ShowAlert.Error("Purchase Order Failed", "Failed to place the purchase order. Please try again.");
        }
    }
}

