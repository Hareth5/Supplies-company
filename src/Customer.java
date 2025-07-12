import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;

public class Customer {
    private SimpleIntegerProperty id;
    private SimpleStringProperty name, location;
    private SimpleDoubleProperty debts;

    public Customer(String name, String location, double debts) throws IllegalArgumentException {
        this.id = new SimpleIntegerProperty();
        this.name = new SimpleStringProperty();
        this.location = new SimpleStringProperty();
        this.debts = new SimpleDoubleProperty();

        setName(name.trim());
        setLocation(location.trim());
        setDebts(debts);
    }

    public Customer(int id, String name, String location, double debts) throws IllegalArgumentException {
        this.id = new SimpleIntegerProperty(id);
        this.name = new SimpleStringProperty();
        this.location = new SimpleStringProperty();
        this.debts = new SimpleDoubleProperty();

        setName(name.trim());
        setLocation(location.trim());
        setDebts(debts);
    }

    public int getId() {
        return id.get();
    }

    public void setId(int id) {
        this.id.set(id);
    }

    public String getName() {
        return name.get();
    }

    public void setName(String name) throws IllegalArgumentException {
        if (isNull(name))
            throw new IllegalArgumentException("Name cannot be empty");

        if (!validation(name, "^[a-zA-Z- ]+$"))
            throw new IllegalArgumentException("Name must contain only letters and spaces");

        this.name.set(name);
    }

    public String getLocation() {
        return location.get();
    }

    public void setLocation(String location) throws IllegalArgumentException {
        if (isNull(location))
            throw new IllegalArgumentException("Location cannot be empty");

        if (!validation(location, "^[a-zA-Z ]+$"))
            throw new IllegalArgumentException("Location contain only letters and spaces");

        this.location.set(location);
    }

    public double getDebts() {
        return debts.get();
    }

    public void setDebts(double debts) throws IllegalArgumentException {
        if (debts < 0)
            throw new IllegalArgumentException("Debts cannot be negative");

        this.debts.set(debts);
    }

    private boolean isNull(String value) {
        return value == null || value.trim().isEmpty();
    }

    private boolean validation(String value, String regex) {
        return value.matches(regex);
    }

    @Override
    public String toString() {
        return getName() + ", " + getLocation();
    }
}