import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.Alert;

import java.sql.*;
import java.time.LocalDate;

public class Catalog { // a class contain the main project data structures
    private static ObservableList<Category> categoryList = FXCollections.observableArrayList();
    private static ObservableList<Manufacturer> manufacturerList = FXCollections.observableArrayList();
    private static ObservableList<Product> productList = FXCollections.observableArrayList();
    private static ObservableList<Entry> entryList = FXCollections.observableArrayList();
    private static ObservableList<Shipment> shipmentList = FXCollections.observableArrayList();

    private static ObservableList<Customer> customerList;
    private static ObservableList<PhoneNumber> phoneNumbersList;
    private static ObservableList<Order> orderList;
    private static ObservableList<Sale> salesList;
    public static int ordersCounter = getGreaterOrderID();

    public Catalog() { // initialize the data structures
        customerList = FXCollections.observableArrayList();
        phoneNumbersList = FXCollections.observableArrayList();
        orderList = FXCollections.observableArrayList();
        salesList = FXCollections.observableArrayList();

        loadCustomers();
        loadPhoneNumbers();
        loadOrders();
    }

    public static ObservableList<Customer> getCustomerList() {
        return customerList;
    }

    public static ObservableList<PhoneNumber> getPhoneNumbersList() {
        return phoneNumbersList;
    }

    public static ObservableList<Order> getOrderList() {
        return orderList;
    }

    public static ObservableList<Sale> getSalesList() {
        return salesList;
    }

    public static ObservableList<Category> getCategoryList() {
        categoryList.clear();
        try (Connection conn = DBConnection.connect();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt
                     .executeQuery("SELECT category_id,category_name, category_description FROM category")) {

            while (rs.next()) {
                String id = rs.getString("category_id");
                String name = rs.getString("category_name");
                String description = rs.getString("category_description");
                Category cat = new Category(id, name, description);
                categoryList.add(cat);
            }

        } catch (SQLException e) {
            MyAlert.alert("Error", e.getMessage(), Alert.AlertType.ERROR);
        }
        return categoryList;
    }

    public static ObservableList<Manufacturer> getManufacturerList() {
        manufacturerList.clear();
        try (Connection conn = DBConnection.connect();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(
                     "SELECT manufacturer_id,manufacturer_name, manufacturer_location, manufacturer_email FROM manufacturer")) {

            while (rs.next()) {
                String id = rs.getString("manufacturer_id");
                String name = rs.getString("manufacturer_name");
                String location = rs.getString("manufacturer_location");
                String email = rs.getString("manufacturer_email");
                Manufacturer manu = new Manufacturer(id, name, location, email);
                manufacturerList.add(manu);
            }

        } catch (SQLException e) {
            MyAlert.alert("Error", e.getMessage(), Alert.AlertType.ERROR);
        }
        return manufacturerList;
    }

    public static ObservableList<Product> getProductList() {
        productList.clear();
        getManufacturerList();
        getCategoryList();

        try (Connection conn = DBConnection.connect();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(
                     "SELECT product_id,product_name, unit_price, product_description,manufacturer_id,category_id,total_quantity,discount FROM product")) {

            while (rs.next()) {
                String id = rs.getString("product_id");
                String name = rs.getString("product_name");
                double unitPrice = rs.getDouble("unit_price");
                String descripProduct = rs.getString("product_description");
                String manu_id = rs.getString("manufacturer_id");
                Manufacturer man = null;
                for (Manufacturer man1 : manufacturerList) {
                    if (man1.getId().equals(manu_id)) {
                        man = man1;
                    }
                }

                Category cat = null;
                String cat_id = rs.getString("category_id");
                for (Category cat1 : categoryList) {
                    if (cat1.getCategoryID().equals(cat_id)) {
                        cat = cat1;
                    }
                }

                int totalQuantity = rs.getInt("total_quantity");
                double discount1 = rs.getDouble("discount");

                Product product = new Product(id, name, descripProduct, unitPrice, totalQuantity, discount1, man, cat);
                productList.add(product);
            }

        } catch (SQLException e) {
            MyAlert.alert("Error", e.getMessage(), Alert.AlertType.ERROR);
        }

        return productList;
    }

    public static ObservableList<Entry> getEntryList() {
        return entryList;
    }

    public static ObservableList<Entry> getEntryList1() {
        entryList.clear();
        getShipmentList();
        getProductList();

        Shipment s = ShipmentManagement.selected1;
        String sql = "SELECT entry_id, start_date, expiry_date, quantity, shipment_id, product_id FROM Entry WHERE shipment_id = ?";

        try (Connection conn = DBConnection.connect(); PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, s.getShipmentID());
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                String id = rs.getString("entry_id");
                String startDate = rs.getString("start_date");
                String expiryDate = rs.getString("expiry_date");
                int quantity = rs.getInt("quantity");
                String shipId = rs.getString("shipment_id");
                String productId = rs.getString("product_id");
                Entry entry = new Entry(id, startDate, expiryDate, quantity, shipId, productId);
                entryList.add(entry);

            }

        } catch (SQLException e) {
            MyAlert.alert("Error", e.getMessage(), Alert.AlertType.ERROR);
        }
        return entryList;
    }

    public static ObservableList<Shipment> getShipmentList() {
        shipmentList.clear();
        getManufacturerList();
        try (Connection conn = DBConnection.connect();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT shipment_id,shipment_date, manufacturer_id FROM shipment")) {

            while (rs.next()) {
                String id = rs.getString("shipment_id");
                String manu_id = rs.getString("manufacturer_id");
                Manufacturer man = null;
                for (Manufacturer man1 : manufacturerList) {
                    if (man1.getId().equals(manu_id)) {
                        man = man1;
                    }
                }
                String date = rs.getString("shipment_date");
                Shipment ship = new Shipment(id, man, date);
                shipmentList.add(ship);

            }

        } catch (SQLException e) {
            MyAlert.alert("Error", e.getMessage(), Alert.AlertType.ERROR);
        }
        return shipmentList;
    }


    private void loadCustomers() {
        try (Connection connection = DBConnection.connect();
             PreparedStatement statement = connection.prepareStatement("select customer_id,customer_name," +
                     " customer_location, customer_debts from customer");
             ResultSet rs = statement.executeQuery()) {

            while (rs.next()) {
                int id = rs.getInt("customer_id");
                String name = rs.getString("customer_name");
                String location = rs.getString("customer_location");
                double debts = rs.getDouble("customer_debts");

                Customer customer = new Customer(id, name, location, debts);
                customerList.add(customer);
            }

        } catch (SQLException e) {
            MyAlert.alert("Error", e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    private void loadPhoneNumbers() {
        try (Connection connection = DBConnection.connect();
             Statement statement = connection.createStatement();
             ResultSet rs = statement.executeQuery("select customer_phone,customer_id from phone")) {

            while (rs.next()) {
                String phone = rs.getString("customer_phone");
                int id = rs.getInt("customer_id");
                Customer customer2 = PhoneNumberManagement.getCustomer(id);
                phoneNumbersList.add(new PhoneNumber(phone, customer2));
            }

        } catch (SQLException e) {
            MyAlert.alert("Error", e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    private void loadOrders() {
        try (Connection connection = DBConnection.connect();
             Statement statement = connection.createStatement();
             ResultSet rs = statement.executeQuery("select order_id,order_date, delivery_date, total_price," +
                     " customer_id from orders")) {

            while (rs.next()) {
                int id = rs.getInt("order_id");
                LocalDate orderDate = rs.getDate("order_date").toLocalDate();

                Date date = rs.getDate("delivery_date");
                LocalDate deliveryDate = (date != null) ? date.toLocalDate() : null;

                double price = rs.getDouble("total_price");
                int cID = rs.getInt("customer_id");

                orderList.add(new Order(id, orderDate.toString(), (deliveryDate != null) ? deliveryDate.toString() : null, price, getCustomer(cID)));
            }

        } catch (SQLException e) {
            MyAlert.alert("Error", e.getMessage(), Alert.AlertType.ERROR);
        }
    }


    private static int getGreaterOrderID() {
        try (Connection connection = DBConnection.connect();
             Statement statement = connection.createStatement();
             ResultSet rs = statement.executeQuery("select max(order_id) as maxID from orders")) {

            if (rs.next()) {
                int id = rs.getInt("maxID");
                return id + 1;
            }

        } catch (SQLException e) {
            MyAlert.alert("Error", e.getMessage(), Alert.AlertType.ERROR);
        }
        return 1;
    }


    public static Customer getCustomer(int id) {
        try (Connection connection = DBConnection.connect();
             PreparedStatement statement = connection.prepareStatement("select customer_id, customer_name, customer_location," +
                     " customer_debts from customer where customer_id = ?")) {

            statement.setInt(1, id);
            ResultSet rs = statement.executeQuery();

            if (rs.next()) {
                int customerId = rs.getInt("customer_id");
                String name = rs.getString("customer_name");
                String location = rs.getString("customer_location");
                double debts = rs.getDouble("customer_debts");

                return new Customer(customerId, name, location, debts);
            }
        } catch (SQLException e) {
            MyAlert.alert("Error", e.getMessage(), Alert.AlertType.ERROR);
        }
        return null;
    }

    public static Product getProduct(String id) {
        for (Product p : productList) {
            if (p.getProductId().equals(id))
                return p;
        }
        return null;
    }
}
