import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;


public class UpdateCustomer {
    @FXML
    private Label title;
    @FXML
    private TextField name;
    @FXML
    private TextField tLocation;
    @FXML
    private Button cancel;
    @FXML
    private Button action;
    private Customer customer;

    public UpdateCustomer(Customer customer) {
        this.customer = customer;
    }

    public BorderPane main() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("files/UpdateCustomer.fxml"));
            loader.setController(this);
            BorderPane root = loader.load();

            Styling.setTitlesStyle(title);
            loadData();
            actions();
            return root;

        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    private void loadData() {
        if (customer != null) {
            name.setText(customer.getName());
            tLocation.setText(customer.getLocation());
        }
    }

    private void actions() {
        cancel.setOnAction(e -> Main.setMain(new CustomerManagement().main()));

        action.setOnAction(e -> {
            try (Connection connection = DBConnection.connect();
                 PreparedStatement statement = connection.prepareStatement("update customer set customer_name = ?, " +
                         "customer_location = ?, customer_debts = ? where customer_id = ?")) {

                String tName = name.getText().trim();
                String tLocation = this.tLocation.getText().trim();

                if (tName.isEmpty() || tLocation.isEmpty()) {
                    MyAlert.alert("Error", "Name and location cannot be empty", Alert.AlertType.ERROR);
                    return;
                }

                statement.setString(1, tName);
                statement.setString(2, tLocation);
                statement.setDouble(3, customer.getDebts());
                statement.setInt(4, customer.getId());
                statement.executeUpdate();

                customer.setName(tName);
                customer.setLocation(tLocation);

                Main.setMain(new CustomerManagement().main());
                MyAlert.alert("Success", "Customer updated successfully", Alert.AlertType.INFORMATION);

            } catch (SQLException ex) {
                MyAlert.alert("Error", ex.getMessage(), Alert.AlertType.ERROR);
            }
        });
    }
}
