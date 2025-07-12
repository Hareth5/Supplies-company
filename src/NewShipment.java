import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.time.LocalDate;

public class NewShipment {
	@FXML
	private TableView<Entry> EntryTable;

	@FXML
	private TableColumn<Entry, String> colProductId;

	@FXML
	private TableColumn<Entry, String> colProductName;

	@FXML
	private TableColumn<Entry, Integer> colAmount;

	@FXML
	private TableColumn<Entry, String> colStartDate;

	@FXML
	private TableColumn<Entry, String> colExpiryDate;

	@FXML
	private Button removeButton;

	@FXML
	private Button addProductButton;

	@FXML
	private Button confirmButton;

	@FXML
	private ComboBox<Manufacturer> manufacturerComboBox;

	@FXML
	private DatePicker shipmentDatePicker;
	public static int currentShipmentId = 1;

	public BorderPane main() {
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource("files/NewShipment.fxml"));
			loader.setController(this);
			BorderPane root = loader.load();
			manufacturerComboBox.setItems(Catalog.getManufacturerList());

			colProductId.setCellValueFactory(new PropertyValueFactory<>("productId"));
			colProductName.setCellValueFactory(new PropertyValueFactory<>("productName"));
			colAmount.setCellValueFactory(new PropertyValueFactory<>("quantity"));
			colStartDate.setCellValueFactory(new PropertyValueFactory<>("startDate"));
			colExpiryDate.setCellValueFactory(new PropertyValueFactory<>("endDate"));
			EntryTable.setItems(EntryManagement.entryListTemp);

			actions();
			return root;
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}

	public static void addShipment() {
		String sql = "INSERT INTO shipment (shipment_id,manufacturer_id, shipment_date) VALUES (?, ?, ?)";
		try (Connection conn = DBConnection.connect(); PreparedStatement stmt = conn.prepareStatement(sql)) {
			Statement s = conn.createStatement();
			ResultSet rsId = s.executeQuery("SELECT MAX(shipment_id) FROM shipment");
			if (rsId.next()) {
				currentShipmentId = rsId.getInt(1) + 1;
			}
			stmt.setInt(1, currentShipmentId);
			stmt.setInt(2, 1);
			stmt.setDate(3, java.sql.Date.valueOf(java.time.LocalDate.now()));
			stmt.executeUpdate();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void actions() {
		addProductButton.setOnAction(x -> {
			try {
				FXMLLoader loader = new FXMLLoader(getClass().getResource("files/EntryManagement.fxml"));
				BorderPane root2 = loader.load();
				Scene scene = new Scene(root2);
				Stage stage = new Stage();
				stage.setTitle("Add Product to Shipment");
				stage.setScene(scene);
				stage.setResizable(false);
				stage.initModality(Modality.APPLICATION_MODAL);
				stage.showAndWait();
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		});
		confirmButton.setOnAction(x -> {
			int newQty = 0;
			if (EntryManagement.entryListTemp == null || EntryManagement.entryListTemp.isEmpty()) {
				MyAlert.alert("Error", "Please add Product Before Confirmation", AlertType.ERROR);
				return;
			}
			Manufacturer selected = manufacturerComboBox.getValue();
			if (selected == null) {
				MyAlert.alert("Error", "Please select a Manufacturer", AlertType.ERROR);
				return;
			}
			if (shipmentDatePicker.getValue() == null) {
				MyAlert.alert("Error", "Date Cant be Empty", AlertType.ERROR);
				return;
			}
			String today = shipmentDatePicker.getValue().toString();
			if (LocalDate.parse(today).isAfter(LocalDate.now())) {
				MyAlert.alert("Error", "Invalid date, must be today or before", AlertType.ERROR);
				return;
			}

			NewShipment.addShipment();
			for (Entry e : EntryManagement.entryListTemp) {
				String sql = "INSERT INTO entry (start_date, expiry_date, quantity ,Shipment_id,product_id) VALUES (?, ?, ?, ?, ?)";
				try (Connection conn = DBConnection.connect(); PreparedStatement stmt = conn.prepareStatement(sql)) {
					stmt.setString(1, e.getStartDate());
					stmt.setString(2, e.getEndDate());
					stmt.setInt(3, e.getQuantity());
					stmt.setString(4, String.valueOf(currentShipmentId));
					stmt.setString(5, e.getProductId());

					stmt.executeUpdate();
					Catalog.getEntryList().add(e);
					for (Product p : Catalog.getProductList()) {
						if (p.getProductId().equals(e.getProductId())) {
							newQty = p.getTotalQuantity() + e.getQuantity();
							p.setTotalQuantity(newQty);
						}
					}
					String updateSql = "UPDATE product SET total_quantity = ? WHERE product_id = ?";
					PreparedStatement updateStmt = conn.prepareStatement(updateSql);
					updateStmt.setInt(1, newQty);
					updateStmt.setString(2, e.getProductId());
					updateStmt.executeUpdate();

				} catch (Exception e1) {
					e1.printStackTrace();
				}

			}
			MyAlert.alert("Success", "Shipment Confirmed Successfully", AlertType.INFORMATION);
			EntryManagement.entryListTemp.clear();

			String sql1 = "UPDATE shipment SET manufacturer_id = ?, shipment_date = ? WHERE shipment_id = ?";
			try (Connection conn = DBConnection.connect(); PreparedStatement stmt = conn.prepareStatement(sql1)) {

				stmt.setInt(1, Integer.parseInt(selected.getId()));
				stmt.setDate(2, java.sql.Date.valueOf(java.time.LocalDate.now()));
				stmt.setInt(3, currentShipmentId);
				stmt.executeUpdate();

				currentShipmentId++;

			} catch (Exception e) {
				e.printStackTrace();
			}
		});
		removeButton.setOnAction(x -> {
			Entry selected = EntryTable.getSelectionModel().getSelectedItem();

			if (selected == null) {
				MyAlert.alert("Error", "Please select a Product to delete", AlertType.ERROR);
				return;
			}
			EntryManagement.entryListTemp.remove(selected);

		});
	}

}