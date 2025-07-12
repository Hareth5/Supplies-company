import javafx.scene.control.*;

import java.util.Optional;


public class MyAlert { // a class for show alerts
    public static boolean alert(String head, String message, Alert.AlertType type) { // a method to show normal alert
        Alert alert = new Alert(type);
        alert.setTitle(head);
        alert.setContentText(message);
        Optional<ButtonType> result = alert.showAndWait();
        return result.isPresent() && result.get() == ButtonType.OK;
    }
}
