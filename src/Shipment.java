import javafx.beans.property.SimpleStringProperty;
import javafx.scene.control.Alert.AlertType;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.Date;

public class Shipment {
	private SimpleStringProperty date, shipmentID;
	private Manufacturer manufacturer;

	public Shipment(String shipmentID, Manufacturer manufacturer, String date) {
		this.shipmentID = new SimpleStringProperty(shipmentID);
		this.manufacturer = manufacturer;
		this.date = new SimpleStringProperty(date);
	}

	public Shipment(Manufacturer manufacturer, String date) {
		this.manufacturer = manufacturer;
		this.date = new SimpleStringProperty();

		setDate(date);
	}

	public String getShipmentID() {
		return shipmentID.get();
	}

	public String getDate() {
		return date.get();
	}

	public void setDate(String date) {
		if (date == null || date.trim().isEmpty()) {
			throw new IllegalArgumentException("Date can't be null or empty");
		}

		LocalDate inputDate;
		try {
			inputDate = LocalDate.parse(date);
		} catch (Exception e) {
			throw new IllegalArgumentException("Invalid date format. Expected: yyyy-MM-dd");
		}

		if (inputDate.isAfter(LocalDate.now())) {
			throw new IllegalArgumentException("Invalid date, must be today or before");
		}

		this.date.set(date);
	}

	public Manufacturer getManufacturer() {
		return manufacturer;
	}

	public void setManufacturer(Manufacturer manufacturer) {
		this.manufacturer = manufacturer;
	}

}
