import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;

public class Product implements Comparable<Product> {
	private SimpleStringProperty productId, name, description;
	private SimpleDoubleProperty unitPrice, discount;
	private SimpleIntegerProperty totalQuantity;
	private Manufacturer manufacturer;
	private Category category;

	public Product(String productId, String name, String descirption, double unitPrice, int totalQuantity,
			double discount, Manufacturer manufacturer, Category category) throws IllegalArgumentException {
		this.productId = new SimpleStringProperty(productId);
		this.name = new SimpleStringProperty(name);
		this.description = new SimpleStringProperty(descirption);
		this.unitPrice = new SimpleDoubleProperty(unitPrice);
		this.totalQuantity = new SimpleIntegerProperty(totalQuantity);
		this.discount = new SimpleDoubleProperty(discount);
		this.manufacturer = manufacturer;
		this.category = category;

	}

	public Product(String name, String descirption, double unitPrice, int totalQuantity, double discount,
			Manufacturer manufacturer, Category category) throws IllegalArgumentException {
		this.productId = new SimpleStringProperty();
		this.name = new SimpleStringProperty();
		this.description = new SimpleStringProperty();
		this.unitPrice = new SimpleDoubleProperty();
		this.totalQuantity = new SimpleIntegerProperty();
		this.discount = new SimpleDoubleProperty();

		setName(name);
		setUnitPrice(unitPrice);
		setTotalQuantity(totalQuantity);
		setDiscount(discount);
		setManufacturer(manufacturer);
		setCategory(category);
	}

	// Getters and Setters

	public String getProductId() {
		return productId.get();
	}

	public String getName() {
		return name.get();
	}

	public void setName(String name) throws IllegalArgumentException {
		if (isNull(name)) {
			throw new IllegalArgumentException("Name cannot be empty");
		}
		if (!validation(name, "^[a-zA-Z0-9 ]+$")) {
			throw new IllegalArgumentException("Name must contain only letters, numbers, and spaces");
		}
		this.name.set(Character.toUpperCase(name.charAt(0)) + name.substring(1));
	}

	public String getDescription() {
		return description.get();
	}

	public void setDescription(String description) throws IllegalArgumentException {
		if (isNull(description)) {
			throw new IllegalArgumentException("Description cannot be empty");
		}
		this.description.set(description);
	}

	public double getUnitPrice() {
		return unitPrice.get();
	}

	public void setUnitPrice(double unitPrice) throws IllegalArgumentException {
		if (unitPrice < 0) {
			throw new IllegalArgumentException("Unit price must be non-negative");
		}
		if (unitPrice > 100000) {
			throw new IllegalArgumentException("Unit price is too high");
		}
		this.unitPrice.set(unitPrice);
	}

	public int getTotalQuantity() {
		return totalQuantity.get();
	}

	public void setTotalQuantity(int totalQuantity) throws IllegalArgumentException {
		if (totalQuantity < 0) {
			throw new IllegalArgumentException("Total quantity must be non-negative");
		}
		this.totalQuantity.set(totalQuantity);
	}

	public double getDiscount() {
		return discount.get();
	}

	public void setDiscount(double discount) throws IllegalArgumentException {
		if (discount < 0 || discount > 100) {
			throw new IllegalArgumentException("Discount must be between 0 and 100");
		}
		this.discount.set(discount);
	}

	public Manufacturer getManufacturer() {
		return manufacturer;
	}

	public void setManufacturer(Manufacturer manufacturer) throws IllegalArgumentException {
		if (manufacturer == null) {
			throw new IllegalArgumentException("Manufacturer cannot be null");
		}
		this.manufacturer = manufacturer;
	}

	public Category getCategory() {
		return category;
	}

	public void setCategory(Category category) throws IllegalArgumentException {
		if (category == null) {
			throw new IllegalArgumentException("Category cannot be null");
		}
		this.category = category;
	}

	private boolean isNull(String value) {
		return value == null || value.trim().isEmpty();
	}

	private boolean validation(String val, String regex) {
		return val.matches(regex);
	}

	@Override
	public int compareTo(Product product) {
		return this.getName().compareTo(product.getName());
	}

	@Override
	public String toString() {
		return getName();
	}

}