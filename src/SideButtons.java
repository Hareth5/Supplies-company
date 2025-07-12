

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;

public class SideButtons {
    @FXML
    private Button home;
    @FXML
    private Button shipment;
    @FXML
    private Button order;
    @FXML
    private Button payment;
    @FXML
    private Button product;
    @FXML
    private Button category;
    @FXML
    private Button manufacturer;
    @FXML
    private Button customer;
    @FXML
    private Button returned;
    @FXML
    private Button exit;
    private Stage stage;

    public SideButtons(Stage stage) {
        this.stage = stage;
    }

    public VBox main() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("files/SideButtons.fxml"));
            loader.setController(this);
            VBox root = loader.load();
            actions();

            Styling.setSideButtonsStyle(home);
            Styling.setSideButtonsStyle(shipment);
            Styling.setSideButtonsStyle(order);
            Styling.setSideButtonsStyle(product);
            Styling.setSideButtonsStyle(category);
            Styling.setSideButtonsStyle(manufacturer);
            Styling.setSideButtonsStyle(customer);
            Styling.setSideButtonsStyle(payment);
            Styling.setSideButtonsStyle(returned);
            Styling.setSideButtonsStyle(exit);

            return root;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    private void actions() {
        home.setOnAction(e -> Main.setMain(new ProductManagement().main()));
        shipment.setOnAction(e -> Main.setMain(new NewShipment().main()));
        order.setOnAction(e -> Main.setMain(new NewOrder().main()));
        payment.setOnAction(e -> Main.setMain(new AddPayment().main()));
        product.setOnAction(e -> Main.setMain(new AddProduct().main()));
        category.setOnAction(e -> Main.setMain(new AddCategory().main()));
        manufacturer.setOnAction(e -> Main.setMain(new AddManufacturer().main()));
        customer.setOnAction(e -> Main.setMain(new AddCustomer().main()));
//        returned.setOnAction(e -> Main.setMain());
        exit.setOnAction(e -> stage.close());
    }

}