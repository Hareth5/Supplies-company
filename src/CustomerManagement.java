import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.BorderPane;

import java.io.IOException;
import java.sql.*;

public class CustomerManagement {
    @FXML
    private TableView<Customer> customerTable;
    @FXML
    private TableColumn<Customer, Number> cID;
    @FXML
    private TableColumn<Customer, String> cName;
    @FXML
    private TableColumn<Customer, String> cLocation;
    @FXML
    private TableColumn<Customer, Number> cDebts;
    @FXML
    private ComboBox<String> sort;
    @FXML
    private Button cancel;
    @FXML
    private TextField search;
    @FXML
    private Button remove;
    @FXML
    private Button update;
    @FXML
    private Button add;
    @FXML
    private Button phones;

    public BorderPane main() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("files/CustomerManagement.fxml"));
            loader.setController(this);
            BorderPane root = loader.load();

            sort.getItems().addAll("ID", "Name", "Location");
            setTableColumns();
            actions();
            Styling.setSmallButtonsStyle(cancel);
            Styling.setSearchTxtStyle(search);
            return root;

        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    private void setTableColumns() {
        cID.setCellValueFactory(new PropertyValueFactory<>("id"));
        cName.setCellValueFactory(new PropertyValueFactory<>("name"));
        cLocation.setCellValueFactory(new PropertyValueFactory<>("location"));
        cDebts.setCellValueFactory(new PropertyValueFactory<>("debts"));

        customerTable.setItems(Catalog.getCustomerList());
    }

    private void actions() {
        add.setOnAction(e -> {
            Customer customer = customerTable.getSelectionModel().getSelectedItem();
            if (customer == null) {
                MyAlert.alert("Error", "You must choose a customer to add phone number", Alert.AlertType.ERROR);
                return;
            }

            Main.setMain(new AddPhoneNumber(customer).main());
        });

        phones.setOnAction(e -> {
            Main.setMain(new PhoneNumberManagement().main());
        });

        remove.setOnAction(e -> {
            Customer customer = customerTable.getSelectionModel().getSelectedItem();
            if (customer == null) {
                MyAlert.alert("Error", "You must choose a customer to remove", Alert.AlertType.ERROR);
                return;
            }

            try {
                if (!MyAlert.alert("CONFIRMATION", "Are you sure you want to remove this customer?",
                        Alert.AlertType.CONFIRMATION))
                    return;

                Connection connection = DBConnection.connect();
                PreparedStatement statement = connection.prepareStatement("delete from customer where customer_id = ?");
                statement.setInt(1, customer.getId());
                statement.executeUpdate();
                Catalog.getCustomerList().remove(customer);

            } catch (SQLException ex) {
                MyAlert.alert("Error", ex.getMessage(), Alert.AlertType.ERROR);
            }
        });

        update.setOnAction(e -> {
            Customer customer = customerTable.getSelectionModel().getSelectedItem();
            if (customer == null) {
                MyAlert.alert("Error", "You must choose a customer to update", Alert.AlertType.ERROR);
                return;
            }
            Main.setMain(new UpdateCustomer(customer).main());
        });

        search.setOnKeyPressed(e -> {
            if (e.getCode() == KeyCode.ENTER) {
                String searchBy = search.getText().trim();
                if (searchBy.equals("") || (!searchBy.matches("^[0-9]+$") && !searchBy.matches("^[a-zA-Z ]+$"))) {
                    MyAlert.alert("Warning", "You should enter a valid ID or username to search", Alert.AlertType.WARNING);
                    return;
                }

                ObservableList<Customer> customersList = FXCollections.observableArrayList();
                boolean isID = Character.isDigit(searchBy.charAt(0));
                String sql = "select * from customer where ";

                if (isID)
                    sql += "customer_id = ?";
                else
                    sql += "customer_name like ?";

                try (Connection connection = DBConnection.connect();
                     PreparedStatement statement = connection.prepareStatement(sql)) {

                    if (isID) {
                        try {
                            statement.setInt(1, Integer.parseInt(searchBy));

                        } catch (NumberFormatException ex) {
                            MyAlert.alert("Error", "Invalid ID", Alert.AlertType.ERROR);
                            return;
                        }

                    } else
                        statement.setString(1, "%" + searchBy + "%");

                    ResultSet rs = statement.executeQuery();
                    while (rs.next()) {
                        int id = rs.getInt("customer_id");
                        String name = rs.getString("customer_name");
                        String location = rs.getString("customer_location");
                        double debts = rs.getDouble("customer_debts");

                        Customer customer = new Customer(id, name, location, debts);
                        customersList.add(customer);
                    }

                    if (customersList.isEmpty()) {
                        MyAlert.alert("INFORMATION", "There are no customers with this search",
                                Alert.AlertType.INFORMATION);
                        return;
                    }
                    customerTable.setItems(customersList);

                } catch (SQLException ex) {
                    MyAlert.alert("Error", ex.getMessage(), Alert.AlertType.ERROR);
                }
            }
        });

        cancel.setOnAction(e -> {
            search.clear();
            customerTable.setItems(Catalog.getCustomerList());
        });
    }
}

