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

public class AddPhoneNumber {
    @FXML
    private Label title;
    @FXML
    private TextField name;
    @FXML
    private Button cancel;
    @FXML
    private Button add;
    private Customer customer;

    public AddPhoneNumber(Customer customer) {
        this.customer = customer;
    }

    public BorderPane main() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("files/AddPhoneNUmber.fxml"));
            loader.setController(this);
            BorderPane root = loader.load();

            Styling.setTitlesStyle(title);
            actions();
            return root;

        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    private void actions() {
        cancel.setOnAction(e -> Main.setMain(new PhoneNumberManagement().main()));

        add.setOnAction(e -> {
            String phone = name.getText().trim();
            if (phone.isEmpty() || !phone.matches("[0-9+-]+")) {
                MyAlert.alert("Error", "You must enter valid phone number",
                        Alert.AlertType.ERROR);
                return;
            }

            try (Connection connection = DBConnection.connect();
                 PreparedStatement statement = connection.prepareStatement("insert into phone" +
                         " (customer_phone, customer_id) values (?,?)")) {
                statement.setString(1, phone);
                statement.setInt(2, customer.getId());
                statement.executeUpdate();
                Catalog.getPhoneNumbersList().add(new PhoneNumber(phone, customer));

                MyAlert.alert("Success", "Phone number added successfully", Alert.AlertType.INFORMATION);
                Main.setMain(new CustomerManagement().main());

            } catch (SQLException ex) {
                MyAlert.alert("Error", ex.getMessage(), Alert.AlertType.ERROR);
            }
        });
    }
}
