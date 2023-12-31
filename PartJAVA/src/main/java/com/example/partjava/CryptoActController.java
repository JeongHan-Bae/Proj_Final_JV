package com.example.partjava;

import Data.*;

import Tools.JavaClient;
import Tools.Password2Hash;
import Tools.ShowAlert;
import javafx.fxml.FXML;

import javafx.scene.control.TextField;

public class CryptoActController {

    @FXML
    private TextField clientTextField;

    @FXML
    private TextField passwordField;

    @FXML
    private void sellAction() {
        String Client = clientTextField.getText();
        int hash_password = Password2Hash.hashPassword(passwordField.getText().trim());
        JavaClient coinGetter = new JavaClient();
        GlobalObj.coin = Float.parseFloat(coinGetter.sendAndReceive("getCoinVal:"));
        coinGetter.close();

        // Check if the Client is the current user
        if (Client.equals(UserObj.username)) {
            ShowAlert.Warning("Warning", "Can't transfer to self.");
            return;
        }

        // Check if the Client exists in the buyCoins map
        if (!GlobalObj.buyCoins.containsKey(Client)) {
            ShowAlert.Warning("Warning", "Client doesn't exist.");
            return;
        }

        // Retrieve the coin amount for the Client
        float currencyAmount = GlobalObj.buyCoins.get(Client);

        // Check if UserObj.account.investment.coins is sufficient for the sale
        if (currencyAmount > UserObj.account.investment.coins * GlobalObj.coin) {
            ShowAlert.Warning("Warning", "Not enough coins.");
            return;
        }

        JavaClient client = new JavaClient();
        String attempt = client.sendAndReceive("sellCoins:" + UserObj.username + " " + Client + " " + hash_password);
        client.close();


        if ("Wrong password".equals(attempt) || "Client doesn't exist".equals(attempt)) {
            ShowAlert.Error("Error", attempt);
        } else {
            String[] responseParts = attempt.split(" ");
            // Handle cases where there are two parts in the response (coin and currency)
            float coins = Float.parseFloat(responseParts[0]);
            float currency = Float.parseFloat(responseParts[1]);

            // Update UserObj.account.investment.coins
            UserObj.account.investment.coins = coins;

            // Update UserObj.account.currency
            UserObj.account.currency = currency;

            // Remove the entry from GlobalObj.buyCoins
            GlobalObj.buyCoins.remove(Client);

            // Show success message or perform additional actions
            ShowAlert.Information("Sale successful", currencyAmount/GlobalObj.coin + " Coins sold Successfully.");
        }
    }

    @FXML
    private void buyAction() {
        String Client = clientTextField.getText();
        int hash_password = Password2Hash.hashPassword(passwordField.getText().trim());
        JavaClient coinGetter = new JavaClient();
        GlobalObj.coin = Float.parseFloat(coinGetter.sendAndReceive("getCoinVal:"));
        coinGetter.close();

        // Check if the Client is the current user
        if (Client.equals(UserObj.username)) {
            ShowAlert.Warning("Warning", "Can't transfer to self.");
            return;
        }

        // Check if the Client exists in the saleCoins map
        if (!GlobalObj.saleCoins.containsKey(Client)) {
            ShowAlert.Warning("Warning", "Client doesn't exist.");
            return;
        }

        // Retrieve the coin amount for the Client
        float coinAmount = GlobalObj.saleCoins.get(Client);

        // Check if UserObj.account.currency is sufficient for the purchase
        if (coinAmount * GlobalObj.coin > UserObj.account.currency) {
            ShowAlert.Warning("Warning", "Not enough liquid.");
            return;
        }

        JavaClient client = new JavaClient();
        String attempt = client.sendAndReceive("buyCoins:" + UserObj.username + " " + Client + " " + hash_password);
        client.close();


        if ("Wrong password".equals(attempt) || "Client doesn't exist".equals(attempt)) {
            ShowAlert.Error("Error", attempt);
        } else {
            String[] responseParts = attempt.split(" ");
            // Handle cases where there are two parts in the response (coin and currency)
            float coins = Float.parseFloat(responseParts[0]);
            float currency = Float.parseFloat(responseParts[1]);

            // Update UserObj.account.investment.coins
            UserObj.account.investment.coins = coins;

            // Update UserObj.account.currency
            UserObj.account.currency = currency;

            // Remove the entry from GlobalObj.buyCoins
            GlobalObj.saleCoins.remove(Client);

            // Show success message or perform additional actions
            ShowAlert.Information("Purchase successful", coinAmount + " Coins Purchased Successfully.");
        }
    }
}

