package com.example.partjava;

import Tools.ChartGenerator;
import javafx.fxml.FXML;
import javafx.scene.control.Button;

public class InvestmentController {

    public Button returnButton;

    // functions to show charts with the nearest 30 days
    @FXML
    private void onViewGlobalButtonClick() {
        ChartGenerator.chartGlobal();
    }

    @FXML
    private void onViewSelfButtonClick() {
        ChartGenerator.chartSelf();
    }

    // Method of calling another window to realize investments (buy Financial Products)
    @FXML
    private void onInvestButtonClick() {
        SceneNavigator.openNewScene("Invest.fxml", "Invest");
    }

    @FXML
    private void onReturnButtonClick() {
        SceneNavigator.getToInterface("UsersInterface.fxml", returnButton);
    }

}
