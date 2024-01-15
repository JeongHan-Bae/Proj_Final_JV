package com.example.partjava;

import Data.*;

import Tools.JavaClient;
import Tools.Password2Hash;
import Tools.ShowAlert;
import javafx.fxml.FXML;

import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

public class CryptoActController {

    @FXML
    private TextField clientTextField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private void sellAction() {
        String Client = clientTextField.getText();
        int hash_password = Password2Hash.hashPassword(passwordField.getText().trim());
        JavaClient coinGetter = new JavaClient();
        GlobalObj.coin = Float.parseFloat(coinGetter.sendAndReceive("getCoinVal:"));
        coinGetter.close();

        // Check if the Client is the current user
        if (Client.equals(UserObj.username)) {
            ShowAlert.Warning("Attention", "Impossible de transférer vers soi-même.");
            return;
        }

        // Check if the Client exists in the buyCoins map
        if (!GlobalObj.buyCoins.containsKey(Client)) {
            ShowAlert.Warning("Attention", "Le client n'existe pas.");
            return;
        }

        // Retrieve the coin amount for the Client
        float currencyAmount = GlobalObj.buyCoins.get(Client);

        // Check if UserObj.account.investment.coins is sufficient for the sale
        if (currencyAmount > UserObj.account.investment.coins * GlobalObj.coin) {
            ShowAlert.Warning("Attention", "Pas assez de coins.");
            return;
        }

        JavaClient client = new JavaClient();
        String attempt = client.sendAndReceive("sellCoins:" + UserObj.username + " " + Client + " " + hash_password);
        client.close();

        if ("Wrong password".equals(attempt) || "Client doesn't exist".equals(attempt)) {
            String errorMessage = switch (attempt) {
                case "Wrong password" -> "Mot de passe incorrect";
                case "Client doesn't exist" -> "Le client n'existe pas";
                default -> attempt;
            };
            ShowAlert.Error("Erreur", errorMessage);
        }
        else {
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
            ShowAlert.Information("Vente réussie", currencyAmount/GlobalObj.coin + " Coins vendus avec succès.");
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
            ShowAlert.Warning("Attention", "Impossible de transférer vers soi-même.");
            return;
        }

        // Check if the Client exists in the saleCoins map
        if (!GlobalObj.saleCoins.containsKey(Client)) {
            ShowAlert.Warning("Attention", "Le client n'existe pas.");
            return;
        }

        // Retrieve the coin amount for the Client
        float coinAmount = GlobalObj.saleCoins.get(Client);

        // Check if UserObj.account.currency is sufficient for the purchase
        if (coinAmount * GlobalObj.coin > UserObj.account.currency) {
            ShowAlert.Warning("Attention", "Pas assez de liquide.");
            return;
        }

        JavaClient client = new JavaClient();
        String attempt = client.sendAndReceive("buyCoins:" + UserObj.username + " " + Client + " " + hash_password);
        client.close();

        if ("Wrong password".equals(attempt) || "Client doesn't exist".equals(attempt)) {
            String errorMessage = switch (attempt) {
                case "Wrong password" -> "Mot de passe incorrect";
                case "Client doesn't exist" -> "Le client n'existe pas";
                default -> attempt;
            };
            ShowAlert.Error("Erreur", errorMessage);
        }
        else {
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
            ShowAlert.Information("Achat réussi", coinAmount + " Coins achetés avec succès.");
        }
    }
}
