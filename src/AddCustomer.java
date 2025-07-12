import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class AddCustomer {
    @FXML
    private Label title;
    @FXML
    private TextField name;
    @FXML
    private TextField tLocation;
    @FXML
    private TextField phoneNumber;
    @FXML
    private VBox phoneNumbers;
    @FXML
    private Button addPhoneNumber;
    @FXML
    private Button cancel;
    @FXML
    private Button action;
    private List<TextField> textFields;

    public AddCustomer() {
        textFields = new ArrayList<>(5);
    }

    public BorderPane main() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("files/AddCustomer.fxml"));
            loader.setController(this);
            BorderPane root = loader.load();

            textFields.add(phoneNumber);
            Styling.setTitlesStyle(title);
            Styling.setAddPhoneNumberButtonStyle(addPhoneNumber);
            actions();
            return root;

        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    protected void actions() {
        addPhoneNumber.setOnAction(e -> {
            TextField newPhoneNumber = new TextField();
            phoneNumbers.getChildren().add(newPhoneNumber);
            textFields.add(newPhoneNumber);
        });

        cancel.setOnAction(e -> Main.setMain(new ProductManagement().main()));

        action.setOnAction(e -> {
            List<String> phones = new ArrayList<>();
            for (TextField field : textFields) {
                if (!field.getText().isEmpty() && field.getText().trim().matches("[0-9+-]+")) {
                    try (Connection connection = DBConnection.connect();
                         PreparedStatement statement = connection.prepareStatement("select count(*) from phone where " +
                                 "customer_phone = ?")) {

                        String phone = field.getText().trim();
                        statement.setString(1, phone);
                        ResultSet rs = statement.executeQuery();

                        if (rs.next()) {
                            int count = rs.getInt(1);
                            if (count == 0)
                                phones.add(phone);
                        }

                    } catch (SQLException ex) {
                        MyAlert.alert("Error", ex.getMessage(), Alert.AlertType.ERROR);
                    }
                }
            }

            try {
                String name2 = name.getText().trim();
                String location2 = tLocation.getText().trim();

                if (phones.size() == 0) {
                    MyAlert.alert("Error", "Phone number is invalid or already exist", Alert.AlertType.ERROR);
                    return;
                }
                Customer newCustomer = new Customer(name2, location2, 0.0);

                Connection connection = DBConnection.connect();
                PreparedStatement statement = connection.prepareStatement("insert into customer (customer_name, " +
                        "customer_location, customer_debts) values (?, ?, ?)", Statement.RETURN_GENERATED_KEYS);

                statement.setString(1, name2);
                statement.setString(2, location2);
                statement.setDouble(3, 0.0);
                statement.executeUpdate();

                int customerId = 0;
                ResultSet generatedKeys = statement.getGeneratedKeys();
                if (generatedKeys.next()) {
                    customerId = generatedKeys.getInt(1);
                    newCustomer.setId(customerId);
                }

                PreparedStatement statement2 = connection.prepareStatement("insert into phone (customer_id, " +
                        "customer_phone) values (?, ?)");
                for (String phone : phones) {
                    statement2.setInt(1, customerId);
                    statement2.setString(2, phone);
                    statement2.executeUpdate();
                    new PhoneNumber(phone, newCustomer);
                }

                Catalog.getCustomerList().add(newCustomer);

                MyAlert.alert("Success", "Customer added successfully", Alert.AlertType.INFORMATION);
                Main.setMain(new AddCustomer().main());

            } catch (IllegalArgumentException ex) {
                MyAlert.alert("Error", ex.getMessage(), Alert.AlertType.ERROR);

            } catch (SQLException ex2) {
                MyAlert.alert("Error", ex2.getMessage(), Alert.AlertType.ERROR);
            }
        });
    }
}