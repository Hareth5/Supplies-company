import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;

import java.io.IOException;

public class OrdersDetails {
    @FXML
    private Button back;

    private Order order;

    public OrdersDetails(Order order) {
        this.order = order;
    }

    public BorderPane main() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("files/ProductsDetails.fxml"));
            loader.setController(this);
            BorderPane root = loader.load();
            back.setOnAction(e -> {
                Main.setMain(new OrderManagement().main());
            });

            return root;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}