import javafx.beans.property.SimpleStringProperty;

public class PhoneNumber {
    private SimpleStringProperty phoneNumber;
    private Customer customer;

    public PhoneNumber(String phoneNumber, Customer customer) {
        this.phoneNumber = new SimpleStringProperty(phoneNumber);
        this.customer = customer;
    }

    public String getPhoneNumber() {
        return phoneNumber.get();
    }

    public void setPhoneNumber(String phoneNumber) throws IllegalArgumentException {
        this.phoneNumber.set(phoneNumber);
    }

    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }
}