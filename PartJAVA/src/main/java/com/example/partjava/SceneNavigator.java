package com.example.partjava;

import Data.UserObj;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class SceneNavigator {
    private static final List<Stage> openStages = new ArrayList<>();

    // Method of launching a FXML file without turning off the current one
    public static void openNewScene(String fxmlFileName, String title) {
        try {
            FXMLLoader loader = new FXMLLoader(SceneNavigator.class.getResource(fxmlFileName));
            Parent root = loader.load();

            Stage stage = new Stage();
            stage.setTitle(title);
            stage.setScene(new Scene(root));

            openStages.add(stage); // Add the new stage to the list of open stages

            stage.show();
        } catch (IOException ignored) {
        }
    }

    // Method of navigating to a certain FXML file and get the current scene by attaching the corresponding button and turn off the scene
    public static void getToInterface(String fxmlFileName, Button sourceButton, String title) {
        try {
            FXMLLoader loader = new FXMLLoader(SceneNavigator.class.getResource(fxmlFileName));
            Parent root = loader.load();

            Stage stage = new Stage();
            stage.setTitle(title);
            stage.setScene(new Scene(root));

            closeAllStagesExcept(stage); // Close all stages except the target stage

            openStages.add(stage); // Add the new stage to the list of open stages

            Stage currentStage = (Stage) sourceButton.getScene().getWindow();
            currentStage.close();

            stage.show();
        } catch (IOException ignored) {
        }
    }

    // Default method, set the Title as the user's full name
    public static void getToInterface(String fxmlFileName, Button sourceButton) {
        getToInterface(fxmlFileName, sourceButton, UserObj.first_name + " " + UserObj.family_name);
    }

    // Close all open stages except the target stage
    private static void closeAllStagesExcept(Stage targetStage) {
        for (Stage stage : openStages) {
            if (stage != targetStage) {
                stage.close();
            }
        }
        openStages.clear(); // Clear the list of open stages
    }
}
