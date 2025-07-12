import java.sql.Connection;
import java.sql.PreparedStatement;
import java.time.LocalDate;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Alert.AlertType;
import javafx.stage.Stage;

public class UpdateShipment {
	@FXML
	private ComboBox<Manufacturer> manufacturerCombo;

	@FXML
	private DatePicker shipmentDatePicker;

	@FXML
	private Button updateButton2;

	@FXML
	private Button cancelButton;

	Shipment shipment;

	public void setShipment(Shipment shipment) {
		this.shipment = shipment;
		indexDetails(shipment);
		manufacturerCombo.setItems(Catalog.getManufacturerList());
		actions();
	}

	public void indexDetails(Shipment shipment) {
		if (shipment != null) {
			manufacturerCombo.setValue(shipment.getManufacturer());
			shipmentDatePicker.setValue(LocalDate.parse(shipment.getDate()));
		}
	}

	public void actions() {
		cancelButton.setOnAction(x -> {
			Stage stage = (Stage) cancelButton.getScene().getWindow();
			stage.close();

		});
		updateButton2.setOnAction(x -> {
			Manufacturer man = manufacturerCombo.getValue();
			String date = shipmentDatePicker.getValue().toString();
			try {
				shipment.setManufacturer(man);
				shipment.setDate(date);
			} catch (IllegalArgumentException e) {
				MyAlert.alert("Error", e.getMessage(), AlertType.ERROR);
				return;
			}
			boolean confirmation = MyAlert.alert("Confirmation", "Are you sure you need to Update this Shipment ?",
					AlertType.CONFIRMATION);
			if (!confirmation) {
				return;
			}
			String sql = "UPDATE shipment SET shipment_date = ?, manufacturer_id = ? WHERE shipment_id = ?";
			try (Connection conn = DBConnection.connect(); PreparedStatement stmt = conn.prepareStatement(sql)) {
				stmt.setString(1, date);
				stmt.setString(2, man.getId());
				stmt.setString(3, shipment.getShipmentID());
				stmt.executeUpdate();
				MyAlert.alert("Success", "Shipment Updated Successfully ", AlertType.INFORMATION);
				Stage stage = (Stage) updateButton2.getScene().getWindow();
				stage.close();

			} catch (Exception e) {
				e.printStackTrace();
			}
		});
	}
}
