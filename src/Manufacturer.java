import javafx.beans.property.SimpleStringProperty;

public class Manufacturer {
    private SimpleStringProperty id, name, location, email;

    public Manufacturer(String id, String name, String location, String email) {
        this.id = new SimpleStringProperty(id);
        this.name = new SimpleStringProperty(name);
        this.location = new SimpleStringProperty(location);
        this.email = new SimpleStringProperty(email);

    }

    public Manufacturer(String name, String location, String email) {
        this.id = new SimpleStringProperty();
        this.name = new SimpleStringProperty();
        this.location = new SimpleStringProperty();
        this.email = new SimpleStringProperty();

        setName(name);
        setLocation(location);
        setEmail(email);
    }

    public String getId() {
        return id.get();
    }

    public SimpleStringProperty idProperty() {
        return id;
    }

    public String getName() {
        return name.get();
    }

    public void setName(String name) throws IllegalArgumentException {
        if (isNull(name))
            throw new IllegalArgumentException("Name cannot be empty");

        if (!validation(name, "^[a-zA-Z ]+$"))
            throw new IllegalArgumentException("Name must contain only letters and spaces");

        this.name.set(name);
    }

    public String getLocation() {
        return location.get();
    }

    public void setLocation(String location) {
        if (isNull(location))
            throw new IllegalArgumentException("Location cannot be empty");

        if (!validation(location, "^[a-zA-Z ]+$"))
            throw new IllegalArgumentException("Location must contain only letters and spaces");
        this.location.set(location);
    }

    public String getEmail() {
        return email.get();
    }

    public void setEmail(String email) {
        if (isNull(email))
            throw new IllegalArgumentException("Email cannot be empty");
        if (!email.matches("^[\\w.-]+@[\\w.-]+\\.\\w{2,}$"))
            throw new IllegalArgumentException("Invalid email format");
        this.email.set(email);
    }

    private boolean isNull(String value) { // a method to check if the input is null
        return value == null || value.trim().isEmpty();
    }

    private boolean validation(String value, String regex) { // a method to check if the input has a valid expression
        return value.matches(regex);
    }

    @Override
    public String toString() {
        return getName();
    }
}