import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;

import java.io.IOException;
import java.sql.*;

public class PhoneNumberManagement {
    @FXML
    private TableView<PhoneNumber> phoneNumbersTable;
    @FXML
    private TableColumn<PhoneNumber, String> phoneNumber;
    @FXML
    private TableColumn<PhoneNumber, String> id;

    @FXML
    private Button back;
    @FXML
    private Button update;
    @FXML
    private Button remove;
    private Customer customer;

    public BorderPane main() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("files/PhoneNumbers.fxml"));
            loader.setController(this);
            BorderPane root = loader.load();
            setColumns();
            loadData();
            actions();
            return root;

        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    private void setColumns() {
        phoneNumber.setCellValueFactory(new PropertyValueFactory<>("phoneNumber"));
        id.setCellValueFactory(cellData -> new SimpleStringProperty(String.valueOf(cellData.getValue().getCustomer().getId())));

        phoneNumbersTable.setItems(Catalog.getPhoneNumbersList());
    }

    private void loadData() {
        ObservableList<PhoneNumber> phoneNumbersList = FXCollections.observableArrayList();
        try (Connection connection = DBConnection.connect();
             Statement statement = connection.createStatement();
             ResultSet rs = statement.executeQuery("select customer_phone,customer_id FROM phone")) {

            while (rs.next()) {
                String phone = rs.getString("customer_phone");
                int id = rs.getInt("customer_id");
                Customer customer2 = getCustomer(id);
                phoneNumbersList.add(new PhoneNumber(phone, customer2));
            }

            phoneNumbersTable.setItems(phoneNumbersList);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static Customer getCustomer(int customerId) {
        Customer customer = null;
        try (Connection connection = DBConnection.connect();
             PreparedStatement statement = connection.prepareStatement("select customer_id, customer_name," +
                     " customer_location, customer_debts from customer where customer_id = ?")) {

            statement.setInt(1, customerId);
            ResultSet rs = statement.executeQuery();
            if (rs.next()) {
                int id = rs.getInt("customer_id");
                String name = rs.getString("customer_name");
                String location = rs.getString("customer_location");
                double debts = rs.getDouble("customer_debts");

                return new Customer(id, name, location, debts);
            }


        } catch (SQLException e) {
            MyAlert.alert("Error", e.getMessage(), Alert.AlertType.ERROR);
            return null;
        }
        return null;
    }

    private void actions() {
        back.setOnAction(e -> Main.setMain(new CustomerManagement().main()));

        remove.setOnAction(e -> {
            PhoneNumber phoneNum = phoneNumbersTable.getSelectionModel().getSelectedItem();
            if (phoneNum == null) {
                MyAlert.alert("Error", "You must choose a phone number to remove", Alert.AlertType.ERROR);
                return;
            }

            if (!MyAlert.alert("CONFIRMATION", "Are you sure you want to remove this phone number?",
                    Alert.AlertType.CONFIRMATION))
                return;

            try (Connection connection = DBConnection.connect();
                 PreparedStatement statement = connection.prepareStatement("delete from phone where customer_phone = ?")) {
                statement.setString(1, phoneNum.getPhoneNumber());
                statement.executeUpdate();
                loadData();

            } catch (SQLException ex) {
                MyAlert.alert("Error", ex.getMessage(), Alert.AlertType.ERROR);
            }
        });

        update.setOnAction(e -> {
            PhoneNumber phoneNum = phoneNumbersTable.getSelectionModel().getSelectedItem();
            if (phoneNum == null) {
                MyAlert.alert("Error", "You must choose a phone number to update", Alert.AlertType.ERROR);
                return;
            }

            Main.setMain(new UpdatePhoneNumber(phoneNum).main());
        });
    }
}
