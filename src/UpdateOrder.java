import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;

import java.io.IOException;
import java.sql.*;
import java.time.LocalDate;

public class UpdateOrder extends NewOrder {
    private Order order;

    public UpdateOrder(Order order) {
        super();
        this.order = order;
    }

    @Override
    public BorderPane main() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("files/NewOrder.fxml"));
            loader.setController(this);
            BorderPane root = loader.load();

            setTableColumns();
            loadCustomers();
            loadOrderDate();
            getSales(order.getId());
            salesTable.setItems(salesList);
            actions();
            return root;

        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    private void loadOrderDate() {
        confirm.setText("Update");
        customersComboBox.setValue(order.getCustomer());
        orderDate.setValue(Order.parseDate(order.getOrderDate()));
        deliveryDate.setValue((order.getDeliveryDate() != null) ? Order.parseDate(order.getDeliveryDate()) : null);
        price.setText(String.valueOf(order.getTotalPrice()));
    }

    private void getSales(int orderId) {
        salesList.clear();
        try (Connection connection = DBConnection.connect();
             PreparedStatement statement = connection.prepareStatement("select product_id, quantity from sale where order_id = ?")) {

            statement.setInt(1, orderId);
            ResultSet rs = statement.executeQuery();

            while (rs.next()) {
                String id = rs.getString("product_id");
                int quantity = rs.getInt("quantity");
                Product product = Catalog.getProduct(id);
                if (product != null) {
                    salesList.add(new Sale(product, quantity));
                }
            }

        } catch (SQLException e) {
            MyAlert.alert("Error", e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    @Override
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
                 PreparedStatement deleteStatement = connection.prepareStatement("delete from sale where order_id = ?")) {

                deleteStatement.setInt(1, order.getId());
                deleteStatement.executeUpdate();

            } catch (SQLException ex) {
                MyAlert.alert("Error", ex.getMessage(), Alert.AlertType.ERROR);
            }

            double price2 = 0;
            for (Sale sale : salesList) {
                try (Connection connection = DBConnection.connect();
                     PreparedStatement statement = connection.prepareStatement("insert into sale (order_id, product_id, quantity) values (?, ?, ?)")) {

                    statement.setInt(1, order.getId());
                    statement.setInt(2, Integer.parseInt(sale.getProduct().getProductId()));
                    statement.setDouble(3, sale.getQuantity());
                    statement.executeUpdate();

                    price2 += sale.getQuantity() * sale.getProduct().getUnitPrice();

                } catch (SQLException ex) {
                    MyAlert.alert("Error", ex.getMessage(), Alert.AlertType.ERROR);
                }
            }

            try (Connection connection = DBConnection.connect();
                 PreparedStatement statement = connection.prepareStatement("update orders set order_date = ?, delivery_date = ?, total_price = ?, customer_id = ? where order_id = ?")) {

                statement.setDate(1, Date.valueOf(orderDate.getValue()));
                statement.setDate(2, (deliveryDate.getValue() != null) ? Date.valueOf(deliveryDate.getValue()) : null);
                statement.setDouble(3, price2);
                statement.setInt(4, customersComboBox.getValue().getId());
                statement.setInt(5, order.getId());
                statement.executeUpdate();

            } catch (SQLException ex) {
                MyAlert.alert("Error", ex.getMessage(), Alert.AlertType.ERROR);
            }

            order.setOrderDate(orderDate.getValue().toString());
            order.setDeliveryDate(deliveryDate.getValue() != null ? deliveryDate.getValue().toString() : null);
            order.setCustomer(customersComboBox.getValue());
            order.setTotalPrice(price2);

            MyAlert.alert("Success", "Order updated successfully", Alert.AlertType.INFORMATION);
            Main.setMain(new OrderManagement().main());
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