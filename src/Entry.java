import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;

public class Entry {
	private SimpleIntegerProperty quantity;
	private SimpleStringProperty startDate, endDate, shipmentId, productId, productName, entryId;

	public Entry(String entryId, String startDate, String endDate, int quantity, String shipmentId, String productId) {

		this.entryId = new SimpleStringProperty(entryId);
		this.startDate = new SimpleStringProperty(startDate);
		this.endDate = new SimpleStringProperty(endDate);
		this.quantity = new SimpleIntegerProperty(quantity);
		this.shipmentId = new SimpleStringProperty(shipmentId);
		this.productId = new SimpleStringProperty(productId);
	}

	public Entry(String startDate, String endDate, int quantity, String shipmentId, String productId,
			String productName) {

		this.entryId = new SimpleStringProperty();
		this.startDate = new SimpleStringProperty();
		this.endDate = new SimpleStringProperty();
		this.quantity = new SimpleIntegerProperty();
		this.shipmentId = new SimpleStringProperty();
		this.productId = new SimpleStringProperty(productId);
		this.productName = new SimpleStringProperty(productName);

		setQuantity(quantity);
		setStartDate(startDate);
		setEndDate(endDate);
	}

	public String getEntryId() {
		return entryId.get();
	}

	public String getShipmentId() {
		return shipmentId.get();
	}

	public String getProductId() {
		return productId.get();
	}

	public String getProductName() {
		return productName.get();
	}

	public int getQuantity() {
		return quantity.get();
	}

	public void setQuantity(int quantity) {
		if (quantity < 0) {
			throw new IllegalArgumentException("Total quantity must be non-negative");
		}
		this.quantity.set(quantity);

	}

	public String getStartDate() {
		return startDate.get();
	}

	public void setStartDate(String startDate) {

		try {
			LocalDate inputDate = LocalDate.parse(startDate, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
			LocalDate oneMonthAgo = LocalDate.now().minusMonths(1);

			if (inputDate.isAfter(oneMonthAgo)) {
				throw new IllegalArgumentException("Start date must be at Least one month before today.");
			}
			this.startDate.set(startDate);

		} catch (DateTimeParseException e) {
			throw new IllegalArgumentException("Invalid start date format. Expected: yyyy-MM-dd");
		}
	}

	public String getEndDate() {
		return endDate.get();
	}

	public void setEndDate(String endDate) {
		try {
			LocalDate inputDate = LocalDate.parse(endDate, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
			LocalDate oneMonthLater = LocalDate.now().plusMonths(1);

			if (inputDate.isBefore(oneMonthLater)) {
				throw new IllegalArgumentException("Expiry date must be at Least one month after today.");
			}
			this.endDate.set(endDate);

		} catch (DateTimeParseException e) {
			throw new IllegalArgumentException("Invalid expiry date format. Expected: yyyy-MM-dd");
		}
	}

}
