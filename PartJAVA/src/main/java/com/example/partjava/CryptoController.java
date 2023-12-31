package com.example.partjava;

import Data.GlobalObj;
import Data.UserObj;
import Tools.JavaClient;
import javafx.fxml.FXML;


import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;

import java.util.Map;

public class CryptoController {

    @FXML
    private Label currencyLabel;

    @FXML
    private Label coinValueLabel;

    @FXML
    private Label coinAmountLabel;

    @FXML
    private TextArea outputArea;

    public Button returnButton;

    @FXML
    private void showSaleList() {
        // Show the list of each Order which sells
        StringBuilder saleList = new StringBuilder();
        saleList.append("with registered coins:\n");
        for (Map.Entry<String, Float> entry : GlobalObj.saleCoins.entrySet()) {
            saleList.append(entry.getKey()).append(": ").append(entry.getValue()).append("\n");
        }
        outputArea.setText(saleList.toString());
    }

    @FXML
    private void showPurchaseList() {
        // Show the list of each Order which buys
        StringBuilder purchaseList = new StringBuilder();
        purchaseList.append("with registered currency:\n");
        for (Map.Entry<String, Float> entry : GlobalObj.buyCoins.entrySet()) {
            purchaseList.append(entry.getKey()).append(": ").append(entry.getValue()).append("\n");
        }
        outputArea.setText(purchaseList.toString());
    }

    // Open sub windows to react with existing orders or creating an order
    @FXML
    private void action() {
        SceneNavigator.openNewScene("CryptoAct.fxml", "Action");
    }

    @FXML
    private void order() {
        SceneNavigator.openNewScene("CryptoOrder.fxml", "Order");
    }

    @FXML
    private void refresh() {
        initialize();
    }

    @FXML
    private void onReturnButtonClick() {
        SceneNavigator.getToInterface("UsersInterface.fxml", returnButton);
    }

    @FXML
    private void initialize() {
        JavaClient client = new JavaClient();
        String coinStr = client.sendAndReceive("getCoinVal:");
        GlobalObj.coin = Float.parseFloat(coinStr);
        client.close();
        currencyLabel.setText("Currency: " + UserObj.account.currency);
        coinValueLabel.setText("Coin Value: " + GlobalObj.coin);
        coinAmountLabel.setText("Coins Owned: " + UserObj.account.investment.coins);
    }
}
