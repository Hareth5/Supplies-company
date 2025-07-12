import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.*;
import java.time.LocalDate;

public class NewOrder {
    @FXML
    protected TableView<Sale> salesTable;
    @FXML
    protected TableColumn<Sale, String> id;
    @FXML
    protected TableColumn<Sale, String> name;
    @FXML
    protected TableColumn<Sale, String> amount;
    @FXML
    protected Button remove;
    @FXML
    protected Button add;
    @FXML
    protected Button confirm;
    @FXML
    protected DatePicker orderDate;
    @FXML
    protected DatePicker deliveryDate;
    @FXML
    protected ComboBox<Customer> customersComboBox;
    @FXML
    protected Label price;
    protected ObservableList<Sale> salesList;

    public NewOrder() {
        salesList = FXCollections.observableArrayList();
    }

    public BorderPane main() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("files/NewOrder.fxml"));
            loader.setController(this);
            BorderPane root = loader.load();

            salesTable.setItems(salesList);
            orderDate.setValue(LocalDate.now());
            loadCustomers();
            setTableColumns();
            actions();
            return root;

        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    protected void loadCustomers() {
        customersComboBox.setItems(Catalog.getCustomerList());
        if (!customersComboBox.getItems().isEmpty())
            customersComboBox.setValue(customersComboBox.getItems().getFirst());
    }

    protected void setTableColumns() {
        id.setCellValueFactory(cellData -> new SimpleStringProperty(String.valueOf(cellData.getValue().getProduct().getProductId())));
        name.setCellValueFactory(cellData -> new SimpleStringProperty(String.valueOf(cellData.getValue().getProduct().getName())));
        amount.setCellValueFactory(cellData -> new SimpleStringProperty(String.valueOf(cellData.getValue().getQuantity())));
    }


    protected void actions() {
        add.setOnAction(e -> {
            new NewProductsStage().showStage();
        });

        confirm.setOnAction(e -> {
            if (salesList.isEmpty()) {
                MyAlert.alert("Error", "You should add at least one product to the order", Alert.AlertType.ERROR);
                return;
            }

            if (orderDate.getValue() == null) {
                MyAlert.alert("Error", "You should enter the order date", Alert.AlertType.ERROR);
                return;
            }

            LocalDate date = orderDate.getValue();
            if (deliveryDate.getValue() != null && date.isAfter(deliveryDate.getValue())) {
                MyAlert.alert("Error", "Delivery date cannot be before order date", Alert.AlertType.ERROR);
                return;
            }

            try (Connection connection = DBConnection.connect();
                 PreparedStatement statement = connection.prepareStatement("insert into orders (order_id, order_date, " +
                         "delivery_date, total_price, customer_id) values (?, ?, ?, ?, ?)")) {

                statement.setInt(1, Catalog.ordersCounter);
                statement.setDate(2, Date.valueOf(orderDate.getValue()));
                statement.setDate(3, (deliveryDate.getValue() != null) ? Date.valueOf(deliveryDate.getValue()) : null);
                statement.setDouble(4, Double.parseDouble(price.getText()));
                statement.setInt(5, customersComboBox.getValue().getId());

                statement.executeUpdate();

                Catalog.getOrderList().add(new Order(Catalog.ordersCounter, orderDate.getValue().toString(),
                        (deliveryDate.getValue() != null) ? deliveryDate.getValue().toString() : null,
                        Double.parseDouble(price.getText()), customersComboBox.getValue()));

            } catch (SQLException ex) {
                MyAlert.alert("Error", ex.getMessage(), Alert.AlertType.ERROR);
            }

            for (Sale sale : salesList) {
                try (Connection connection = DBConnection.connect();
                     PreparedStatement statement = connection.prepareStatement("insert into sale (order_id, product_id," +
                             " quantity) values (?, ?, ?)")) {

                    statement.setInt(1, Catalog.ordersCounter);
                    statement.setInt(2, Integer.parseInt(sale.getProduct().getProductId()));
                    statement.setDouble(3, sale.getQuantity());
                    statement.executeUpdate();

                    PreparedStatement check = connection.prepareStatement("select total_quantity from product" +
                            " where product_id = ?");
                    check.setString(1, sale.getProduct().getProductId());
                    ResultSet rs = check.executeQuery();

                    if (rs.next()) {
                        int available = rs.getInt("total_quantity");
                        int ordered = sale.getQuantity();
                        int quantity = Math.min(ordered, available);

                        if (quantity > 0) {
                            PreparedStatement statement2 = connection.prepareStatement("update product set total_quantity" +
                                    " = total_quantity - ? where product_id = ?");
                            statement2.setInt(1, quantity);
                            statement2.setString(2, sale.getProduct().getProductId());
                            statement2.executeUpdate();
                            sale.setQuantity(quantity);
                            sale.getProduct().setTotalQuantity(sale.getProduct().getTotalQuantity() - quantity);
                        } else {
                            MyAlert.alert("Error", "No quantity available", Alert.AlertType.ERROR);
                        }
                    }

                } catch (SQLException ex) {
                    MyAlert.alert("Error", ex.getMessage(), Alert.AlertType.ERROR);
                }
            }

            double totalPrice = 0;
            for (Sale sale : salesList) {
                totalPrice += sale.getQuantity() * sale.getProduct().getUnitPrice();
            }
            price.setText(String.valueOf(totalPrice));

            Catalog.ordersCounter++;
            MyAlert.alert("Success", "Order added successfully", Alert.AlertType.INFORMATION);
            Main.setMain(new ProductManagement().main());
        });
    }

    protected class NewProductsStage {
        @FXML
        protected ComboBox<Product> productComboBox;
        @FXML
        protected TextField amount;
        @FXML
        protected Button add;
        @FXML
        protected Button cancel;
        protected Stage stage;

        public void showStage() {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("files/NewProductStage.fxml"));
                loader.setController(this);
                BorderPane root = loader.load();

                defaultValues();
                actions();

                stage = new Stage();
                stage.setScene(new Scene(root, 400, 300));
                stage.setTitle("New Product");
                stage.centerOnScreen();
                stage.show();

            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        protected void defaultValues() {
            productComboBox.setItems(Catalog.getProductList());
            if (!productComboBox.getItems().isEmpty()) {
                productComboBox.setValue(productComboBox.getItems().getFirst());
            }
        }

        protected void actions() {
            cancel.setOnAction(e -> stage.close());

            add.setOnAction(e -> {
                if (productComboBox.getValue() == null || amount.getText().isEmpty())
                    return;

                try {
                    int amount2 = Integer.parseInt(amount.getText().trim());
                    if (amount2 <= 0) {
                        MyAlert.alert("Error", "Amount must be positive integer", Alert.AlertType.ERROR);
                        return;
                    }

                    double totalPrice = Double.parseDouble(price.getText());
                    totalPrice += amount2 * productComboBox.getValue().getUnitPrice();
                    price.setText(totalPrice + "");
                    salesList.add(new Sale(productComboBox.getValue(), amount2));

                    stage.close();

                } catch (NumberFormatException ex) {
                    MyAlert.alert("Error", "Invalid amount format", Alert.AlertType.ERROR);
                }
            });

            remove.setOnAction(e -> {
                Sale sale = salesTable.getSelectionModel().getSelectedItem();
                if (sale == null) {
                    MyAlert.alert("Error", "You must choose a product to remove", Alert.AlertType.ERROR);
                    return;
                }

                price.setText(String.valueOf(Double.parseDouble(price.getText()) - sale.getProduct().getUnitPrice() * sale.getQuantity()));
                salesList.remove(sale);
            });
        }
    }
}