package com.example.partjava;

import Data.UserObj;
import Tools.ChartGenerator;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;

public class UsersInterfaceController {

    @FXML
    private TextArea usernameTextArea;

    @FXML
    private TextArea fullNameTextArea;

    @FXML
    private TextArea teleTextArea;

    @FXML
    private TextArea emailTextArea;

    @FXML
    private TextArea currencyTextArea;

    @FXML
    private TextArea depositTextArea;

    @FXML
    private TextArea debtTextArea;

    @FXML
    private TextArea investmentTextArea;

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
        System.out.println(UserObj.toStringUserObj());
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
        usernameTextArea.setText(UserObj.username);
        fullNameTextArea.setText(UserObj.first_name + " " + UserObj.family_name);
        emailTextArea.setText(UserObj.e_mail);
        teleTextArea.setText(UserObj.telephone);
        depositTextArea.setText(String.valueOf(UserObj.account.deposit));
        debtTextArea.setText(String.valueOf(UserObj.account.debt));
        currencyTextArea.setText(String.valueOf(UserObj.account.currency));
        investmentTextArea.setText(UserObj.account.investment.toString().replace("Financial Products", "Actions"));
    }
}
