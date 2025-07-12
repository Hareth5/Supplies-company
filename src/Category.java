import javafx.beans.property.SimpleStringProperty;

public class Category { // a class for category
    private SimpleStringProperty categoryID, name, description;

    public Category(String id, String name, String description) {
        this.categoryID = new SimpleStringProperty(id);
        this.name = new SimpleStringProperty(name);
        this.description = new SimpleStringProperty(description);
    }

    public Category(String name, String description) {
        this.categoryID = new SimpleStringProperty();
        this.name = new SimpleStringProperty();
        this.description = new SimpleStringProperty();

        setName(name);
        setDescription(description);
    }

    // getters and setters

    public String getCategoryID() {
        return categoryID.get();
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

    public String getDescription() {
        return description.get();
    }

    public void setDescription(String description) throws IllegalArgumentException {
        if (isNull(description))
            throw new IllegalArgumentException("Description cannot be empty");

        this.description.set(description);
    }

    private boolean isNull(String value) { // a method to check if the input is null
        return value == null || value.trim().isEmpty();
    }

    private boolean validation(String value, String regex) { // a method to check if the input has a valid expression
        return value.matches(regex);
    }

    @Override
    public String toString() { // toString method
        return getName();
    }
}