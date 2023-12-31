package Tools;

import javafx.scene.control.Alert;

public class ShowAlert {
    private static void showAlert(String title, String message, char type) {
        Alert alert;
        if (type == 'I'){
            alert = new Alert(Alert.AlertType.INFORMATION);
        } else if (type == 'W'){
            alert = new Alert(Alert.AlertType.WARNING);
        } else {
            alert = new Alert(Alert.AlertType.ERROR);
        }
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
    public static void Information(String title, String message){
        showAlert(title, message, 'I');
    }

    public static void Warning(String title, String message){
        showAlert(title, message, 'W');
    }

    public static void Error(String title, String message){
        showAlert(title, message, 'E');
    }
}
