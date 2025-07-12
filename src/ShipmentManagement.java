import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.Scene;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.time.LocalDate;

public class ShipmentManagement {

	@FXML
	private TableView<Shipment> shipmentTable;

	@FXML
	private TableColumn<Shipment, Integer> shipmentIdColumn;

	@FXML
	private TableColumn<Shipment, String> shipmentDateColumn;

	@FXML
	private TableColumn<Shipment, String> manufacturerColumn;

	@FXML
	private ComboBox<String> manufacturerComboBox;

	@FXML
	private Button updateButton;

	@FXML
	private Button removeButton;

	@FXML
	private Button showDetailsButton;

	static Shipment selected1;

	public BorderPane main() {
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource("files/ShipmentManagement.fxml"));
			loader.setController(this);
			BorderPane root = loader.load();

			return root;
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}

	public void initializeColumns() {
		shipmentIdColumn.setCellValueFactory(new PropertyValueFactory<>("shipmentID"));
		manufacturerColumn.setCellValueFactory(new PropertyValueFactory<>("manufacturer"));
		shipmentDateColumn.setCellValueFactory(new PropertyValueFactory<>("date"));
		shipmentTable.setItems(Catalog.getShipmentList());
		actions();

	}

	public void deleteButton() {
		Shipment selected = shipmentTable.getSelectionModel().getSelectedItem();

		if (selected == null) {
			MyAlert.alert("Error", "Please select a Shipment to delete", AlertType.ERROR);
			return;
		}
		boolean confirmation = MyAlert.alert("Confirmation", "Are you sure you need to Delete this Shipment ? ",
				AlertType.CONFIRMATION);
		if (!confirmation) {
			return;
		}
		String sql = "DELETE FROM Shipment WHERE shipment_id = ?";
		try (Connection conn = DBConnection.connect(); PreparedStatement stmt = conn.prepareStatement(sql)) {
			stmt.setString(1, selected.getShipmentID());
			stmt.executeUpdate();
			Catalog.getShipmentList().remove(selected);
			MyAlert.alert("Success", "Shipment Deleted Successfully ", AlertType.INFORMATION);
			shipmentTable.setItems(Catalog.getShipmentList());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void actions() {
		updateButton.setOnAction(x -> {
			try {
				Shipment selected = shipmentTable.getSelectionModel().getSelectedItem();

				if (selected == null) {
					MyAlert.alert("Error", "Please select a Shipment to Update", AlertType.ERROR);
					return;
				}

				FXMLLoader loader = new FXMLLoader(getClass().getResource("UpdateShipment.fxml"));
				BorderPane root = loader.load();

				UpdateShipment controller = loader.getController();
				controller.setShipment(selected);

				Stage stage = new Stage();
				stage.setTitle("Update Shipment");
				stage.setScene(new Scene(root));
				stage.setResizable(false);
				stage.initModality(Modality.APPLICATION_MODAL);
				stage.showAndWait();
			} catch (IOException e) {
				e.printStackTrace();
			}
		});
		removeButton.setOnAction(x -> {
			deleteButton();
		});
		showDetailsButton.setOnAction(x -> {
			try {
				selected1 = shipmentTable.getSelectionModel().getSelectedItem();

				if (selected1 == null) {
					MyAlert.alert("Error", "Please select a Shipment to Show Details", AlertType.ERROR);
					return;
				}

				FXMLLoader loader = new FXMLLoader(getClass().getResource("ShipmentDetails.fxml"));
				ShipmentDetails controller = new ShipmentDetails();
				loader.setController(controller);
				BorderPane root = loader.load();

				Main.setMain(new ShipmentDetails().main());

			} catch (IOException e) {
				e.printStackTrace();
			}
		});
	}
}