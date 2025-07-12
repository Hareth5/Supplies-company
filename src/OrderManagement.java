import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class OrderManagement {
    @FXML
    private TableView<Order> ordersTable;
    @FXML
    private TableColumn<Order, String> id;
    @FXML
    private TableColumn<Order, String> orderDate;
    @FXML
    private TableColumn<Order, String> deliveryDate;
    @FXML
    private TableColumn<Order, String> customer;
    @FXML
    private TableColumn<Order, String> price;
    @FXML
    private ComboBox<Customer> customersComboBox;
    @FXML
    private Button remove;
    @FXML
    private Button update;
    @FXML
    private Button products;

    public BorderPane main() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("files/OrderManagement.fxml"));
            loader.setController(this);
            BorderPane root = loader.load();

            setTableColumns();
            actions();
            return root;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    private void setTableColumns() {
        id.setCellValueFactory(new PropertyValueFactory<>("id"));
        orderDate.setCellValueFactory(new PropertyValueFactory<>("orderDate"));
        deliveryDate.setCellValueFactory(new PropertyValueFactory<>("deliveryDate"));
        price.setCellValueFactory(cellData -> new SimpleStringProperty(String.valueOf(cellData.getValue().getTotalPrice())));
        customer.setCellValueFactory(cellData -> new SimpleStringProperty(String.valueOf(cellData.getValue().getCustomer().getName())));

        ordersTable.setItems(Catalog.getOrderList());
    }

    private void actions() {
        update.setOnAction(e -> {
            Order order = ordersTable.getSelectionModel().getSelectedItem();

            if (order == null) {
                MyAlert.alert("Error", "You must choose an order to update", Alert.AlertType.ERROR);
                return;
            }
            Main.setMain(new UpdateOrder(order).main());
        });

        remove.setOnAction(e -> {
            Order order = ordersTable.getSelectionModel().getSelectedItem();

            if (order == null) {
                MyAlert.alert("Error", "You must choose an order to remove", Alert.AlertType.ERROR);
                return;
            }

            try (Connection connection = DBConnection.connect()) {
                PreparedStatement deleteSales = connection.prepareStatement("delete from sale where order_id = ?");
                deleteSales.setInt(1, order.getId());
                deleteSales.executeUpdate();

                PreparedStatement deleteOrder = connection.prepareStatement("delete from orders where order_id = ?");
                deleteOrder.setInt(1, order.getId());
                deleteOrder.executeUpdate();

                Catalog.getOrderList().remove(order);
                Catalog.getSalesList().removeIf(sale -> sale.getOrder().getId() == order.getId());

                MyAlert.alert("Success", "Order deleted successfully", Alert.AlertType.INFORMATION);
                Main.setMain(new OrderManagement().main());

            } catch (SQLException ex) {
                MyAlert.alert("Error", ex.getMessage(), Alert.AlertType.ERROR);
            }
        });
    }
}