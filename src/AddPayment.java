import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;

import java.io.IOException;

public class AddPayment {
    @FXML
    private Label title;

    public BorderPane main() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("files/AddPayment.fxml"));
            loader.setController(this);
            BorderPane root = loader.load();
            Styling.setTitlesStyle(title);
            return root;

        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}