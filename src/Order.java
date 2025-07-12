import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;

public class Order {
    private SimpleIntegerProperty id;
    private SimpleStringProperty orderDate, deliveryDate;
    private SimpleDoubleProperty totalPrice;
    private Customer customer;

    public Order(int id, String orderDate, String deliveryDate, double totalPrice, Customer customer) throws IllegalArgumentException {
        this.id = new SimpleIntegerProperty(id);
        this.orderDate = new SimpleStringProperty();
        this.deliveryDate = new SimpleStringProperty(deliveryDate);
        this.totalPrice = new SimpleDoubleProperty(totalPrice);
        this.customer = customer;

        setOrderDate(orderDate.trim());
    }

    public int getId() {
        return id.get();
    }

    public void setId(int id) {
        this.id.set(id);
    }

    public String getOrderDate() {
        return orderDate.get();
    }

    public void setOrderDate(String orderDate) throws IllegalArgumentException {
        if (isNull(orderDate))
            throw new IllegalArgumentException("Order date cannot be empty");

        LocalDate date = parseDate(orderDate);
        LocalDate today = LocalDate.now();
        if (date.isAfter(today))
            throw new IllegalArgumentException("Order date cannot be in future date");

        this.orderDate.set(date.toString());
    }

    public String getDeliveryDate() {
        return (deliveryDate != null) ? deliveryDate.get() : null;
    }

    public void setDeliveryDate(String deliveryDate) throws IllegalArgumentException {
        if (isNull(deliveryDate))
            return;

        LocalDate date = parseDate(deliveryDate.trim());
        this.orderDate.set(date.toString());
    }

    public double getTotalPrice() {
        return totalPrice.get();
    }

    public void setTotalPrice(double totalPrice) throws IllegalArgumentException {
        if (totalPrice < 0)
            throw new IllegalArgumentException("Total price cannot be negative");

        this.totalPrice.set(totalPrice);
    }

    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) throws IllegalArgumentException {
        if (customer == null)
            throw new IllegalArgumentException("Customer cannot be null");

        this.customer = customer;
    }

    public static LocalDate parseDate(String date) {
        try {
            return LocalDate.parse(date);

        } catch (DateTimeParseException e) {
            return null;
        }
    }

    private boolean isNull(String value) {
        return value == null || value.trim().isEmpty();
    }


    @Override
    public String toString() {
        return getOrderDate() + ", Expiry: " + getDeliveryDate() + ", Total: $" + getTotalPrice();
    }
}