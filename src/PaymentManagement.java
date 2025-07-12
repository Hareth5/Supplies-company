import javafx.fxml.FXMLLoader;
import javafx.scene.layout.BorderPane;

import java.io.IOException;

public class PaymentManagement {

    public BorderPane main() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("files/PaymentManagement.fxml"));
            loader.setController(this);
            BorderPane root = loader.load();

            return root;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}