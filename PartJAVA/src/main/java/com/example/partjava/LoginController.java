package com.example.partjava;

import Data.BankAccount;
import Data.GlobalObj;
import Data.UserObj;
import Tools.JavaClient;
import Tools.Password2Hash;
import Tools.ShowAlert;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

public class LoginController {

    public Button connectButton;
    public Button newAccButton;
    public Label alertLabel;
    @FXML
    private TextField usrNameField;

    @FXML
    private PasswordField pwField;

    @FXML
    private void connexionClick(){
        String userName = usrNameField.getText();
        String password = pwField.getText();
        String loginInfo = "userLogin:" + userName + " " + Password2Hash.hashPassword(password);
        String bankInfo = "getBank:" + userName;
        // Commands of login and get bank info
        System.out.println(loginInfo);
        JavaClient client = new JavaClient();
        String userInfo = client.sendAndReceive(loginInfo);

        if ("Invalid user data".equals(userInfo)) {
            ShowAlert.Warning("Invalid User Data", "Invalid user data. Please check your credentials.");
            client.close();
        } else {
            String[] userInfoParts = userInfo.split(" ");

            userInfoParts[0] = userInfoParts[0].replace("<", " ");
            userInfoParts[1] = userInfoParts[1].replace("<", " ");
            // As spaces in the names are replaced with "<"s and "<" is surely not part of anyone's name, we replace them with spaces

            UserObj.username = userName;
            UserObj.first_name = userInfoParts[0];
            UserObj.family_name = userInfoParts[1];
            UserObj.telephone = userInfoParts[2];
            UserObj.e_mail = userInfoParts[3];
            String accInfo = client.sendAndReceive(bankInfo);

            UpdateAccInfo(accInfo);
            client.close();
            JavaClient globalCheck = new JavaClient();
            String globInfos = globalCheck.sendAndReceive("getGlobal:");
            // command of getting global infos
            GlobalObj.initializeClass(globInfos);
            System.out.println(UserObj.toStringUserObj());
            System.out.println(GlobalObj.toStringGlobalObj());
            globalCheck.close();

            SceneNavigator.getToInterface("UsersInterface.fxml", connectButton);
        }
    }

    static void UpdateAccInfo(String accInfo) {
        // The global func to connect the server and update the user's bank infos
        String[] dailyAccInfos = accInfo.split(";");
        UserObj.accountMap.clear();

        for (String dailyAccInfo : dailyAccInfos) {
            String[] parts = dailyAccInfo.split(":");
            String date = parts[0];
            String infos = parts[1];

            if (date.equals("curr")) {
                UserObj.account = new BankAccount(infos);
            } else {
                UserObj.accountMap.put(date, new BankAccount(infos));
            }
        }
    }

    @FXML
    private void createNewAcc() {
        SceneNavigator.getToInterface("NewAccount.fxml", newAccButton, "New Account");
    }
}