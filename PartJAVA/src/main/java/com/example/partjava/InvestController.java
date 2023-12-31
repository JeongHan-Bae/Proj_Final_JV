package com.example.partjava;

import Data.*;
import Tools.*;
import javafx.fxml.FXML;

import javafx.scene.control.Label;
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
    private TextField passwordField;

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
            ShowAlert.Information("Stock Price", "The price of one stock of " + companyName + " is: " + stockPrice);
        } else {
            // Company not found, show an alert
            ShowAlert.Warning("Company Not Found", "The company " + companyName + " was not found.");

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
                            ShowAlert.Warning("Wrong Password", "The password entered is incorrect.");
                        } else if (resInfo.equals("Failed")) {
                            ShowAlert.Warning("Transaction Failed", "The stock buying transaction failed.");
                        } else {
                            // Successful transaction, update UserObj.account.currency
                            UserObj.account.currency = Float.parseFloat(resInfo);
                            updateCurrencyLabel();
                            JavaClient callBank = new JavaClient();
                            // Retrieve updated bank account information
                            String accInfo = callBank.sendAndReceive("getBank:" + UserObj.username);
                            callBank.close();
                            updateBankAcc(accInfo);
                            ShowAlert.Information("Transaction Successful", "Stocks bought successfully. Currency updated.");
                        }


                    } else {
                        ShowAlert.Warning("Not Enough Money", "You do not have enough money to make this purchase.");
                    }
                } else {
                    ShowAlert.Error("Error", "Invalid Amount Format. Please enter a valid positive amount.");
                }
            } catch (NumberFormatException e) {
                ShowAlert.Error("Error", "Invalid Amount Format. Please enter a valid number.");
            }
        } else {
            ShowAlert.Warning("Company Not Found", "The company " + companyName + " was not found.");
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
                            ShowAlert.Warning("Wrong Password", "The password entered is incorrect.");
                        } else if (resInfo.equals("Failed")) {
                            ShowAlert.Warning("Transaction Failed", "The stock selling transaction failed.");
                        } else {
                            // Successful transaction, update UserObj.account.currency
                            UserObj.account.currency = Float.parseFloat(resInfo);
                            updateCurrencyLabel();
                            JavaClient callBank = new JavaClient();
                            // Retrieve updated bank account information
                            String accInfo = callBank.sendAndReceive("getBank:" + UserObj.username);
                            callBank.close();
                            updateBankAcc(accInfo);
                            ShowAlert.Information("Transaction Successful", "Stocks sold successfully. Currency updated.");
                        }
                    } else {
                        ShowAlert.Warning("Not Enough Stocks", "You do not have enough stocks to make this sale.");
                    }
                } else {
                    ShowAlert.Error("Error", "Invalid Amount Format. Please enter a valid positive amount.");
                }
            } catch (NumberFormatException e) {
                ShowAlert.Error("Error", "Invalid Amount Format. Please enter a valid number.");
            }
        } else {
            ShowAlert.Warning("Company Not Found", "The company " + companyName + " was not found.");
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
        currencyLabel.setText("You have " + UserObj.account.currency + " as liquid");
    }

    // Update bank account infos
    private void updateBankAcc(String accInfo){
        LoginController.UpdateAccInfo(accInfo);
    }

}
