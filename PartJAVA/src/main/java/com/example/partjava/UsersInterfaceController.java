package com.example.partjava;

import Data.UserObj;
import Tools.ChartGenerator;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;

public class UsersInterfaceController {

    @FXML
    private TextArea infoTextArea;

    public Button investmentButton;

    public Button cryptoButton;

    public Button virementButton;

    public Button loanButton;

    public Button logOutButton;

    public Button myAccountButton;

    // Methods to navigate to corresponding FXML files

    @FXML
    private void onInvestmentButtonClick() {
        SceneNavigator.getToInterface("Investment.fxml", investmentButton);
    }

    @FXML
    private void onCryptoButtonClick() {
        SceneNavigator.getToInterface("Crypto.fxml", cryptoButton);
    }

    @FXML
    private void onVirementButtonClick() {
        SceneNavigator.getToInterface("Virement.fxml", virementButton);
    }

    @FXML
    private void onLoanButtonClick() {
        SceneNavigator.getToInterface("Loan.fxml", loanButton);
    }

    @FXML
    private void onLogOutButtonClick() {
        // Clear user information
        UserObj.username = null;
        UserObj.first_name = null;
        UserObj.family_name = null;
        UserObj.telephone = null;
        UserObj.e_mail = null;
        UserObj.account = null;
        UserObj.accountMap.clear();

        // Reload the Login.fxml
        SceneNavigator.getToInterface("Login.fxml", logOutButton, "Login");
    }

    @FXML
    public void initialize() {
        // Initialize the infoTextArea with UserObj information
        updateInfoTextArea();
    }
    @FXML
    private void onMyAccountButtonClick() {
        ChartGenerator chartGenerator = new ChartGenerator();
        chartGenerator.chartAccountData();
    }

    // Method to update the infoTextArea with UserObj information
    public void updateInfoTextArea() {
        String userObjInfo = "User: " + UserObj.username +
                "\n" + UserObj.first_name + " " + UserObj.family_name.toUpperCase() +
                "\nTelephone: " + UserObj.telephone +
                "\ne-mail: " + UserObj.e_mail +
                "\n\tCurrency: " + UserObj.account.currency +
                "\n\tDeposit: " + UserObj.account.deposit +
                "\n\tDebt: " + UserObj.account.debt +
                "\n\tInvestments: " + UserObj.account.investment;
        infoTextArea.setText(userObjInfo);
    }
}
