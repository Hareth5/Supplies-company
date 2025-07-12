import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;

public class ManufacturerManagement {
	@FXML
	private TableView<Manufacturer> manufacturerTable;
	@FXML
	private TableColumn<Manufacturer, String> idColumn;
	@FXML
	private TableColumn<Manufacturer, String> nameColumn;
	@FXML
	private TableColumn<Manufacturer, String> locationColumn;
	@FXML
	private TableColumn<Manufacturer, String> emailColumn;

	@FXML
	private TextField search;
	@FXML
	private ComboBox<String> sortComboBox;
	@FXML
	private Button cancel;
	@FXML
	private Button updateButton;
	@FXML
	private Button removeButton;

	public BorderPane main() {
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource("files/ManufacturerManagement.fxml"));
			loader.setController(this);
			BorderPane root = loader.load();

			Styling.setSmallButtonsStyle(cancel);
			Styling.setSearchTxtStyle(search);

			idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
			nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
			locationColumn.setCellValueFactory(new PropertyValueFactory<>("location"));
			emailColumn.setCellValueFactory(new PropertyValueFactory<>("email"));

			sortComboBox.getItems().addAll("Sort by Manufacturer Name ASC", "Sort by Manufacturer Name DESC");
			Catalog.getManufacturerList().clear();
			manufacturerTable.setItems(Catalog.getManufacturerList());
			actions();

			return root;
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}

	public ObservableList<Manufacturer> searchOnManufacturer(String search) {
		ObservableList<Manufacturer> resultofSearch = FXCollections.observableArrayList();
		if (search == null || search.trim().isEmpty()) {
			MyAlert.alert("Error", "Please Enter Manufacturer ID or Manufacturer Name to Search", AlertType.ERROR);
			return FXCollections.observableArrayList();
		}
		boolean Num = true;
		for (int i = 0; i < search.length(); i++) {
			if (!Character.isDigit(search.charAt(i))) {
				Num = false;
				break;
			}
		}
		String sql;
		if (Num) {
			sql = "select * from Manufacturer where manufacturer_id = ?";
		} else {
			sql = "select * from Manufacturer where manufacturer_name like ?";
		}
		try (Connection conn = DBConnection.connect(); PreparedStatement stmt = conn.prepareStatement(sql)) {
			if (Num) {
				stmt.setString(1, search);
			} else {
				stmt.setString(1, "%" + search + "%");
			}
			ResultSet rs = stmt.executeQuery();
			while (rs.next()) {
				String id = rs.getString("manufacturer_id");
				String name = rs.getString("manufacturer_name");
				String location = rs.getString("manufacturer_location");
				String email = rs.getString("manufacturer_email");
				resultofSearch.add(new Manufacturer(id, name, location, email));
			}
		} catch (Exception e) {
			e.printStackTrace();

		}
		return resultofSearch;
	}

	public void deleteButton() {
		Manufacturer selected = manufacturerTable.getSelectionModel().getSelectedItem();

		if (selected == null) {
			MyAlert.alert("Error", "Please select a Manufacturer to delete", AlertType.ERROR);
			return;
		}
		boolean confirmation = MyAlert.alert("Confirmation", "Are you sure you need to Delete this Manufacturer ? ",
				AlertType.CONFIRMATION);
		if (!confirmation) {
			return;
		}
		String sql = "DELETE FROM manufacturer WHERE manufacturer_id = ?";
		try (Connection conn = DBConnection.connect(); PreparedStatement stmt = conn.prepareStatement(sql)) {
			stmt.setString(1, selected.getId());
			stmt.executeUpdate();
			Catalog.getManufacturerList().remove(selected);
			MyAlert.alert("Success", "Manufacturer Deleted Successfully ", AlertType.INFORMATION);
			manufacturerTable.setItems(Catalog.getManufacturerList());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void actions() {
		updateButton.setOnAction(e -> {
			Manufacturer updateManufacturer = manufacturerTable.getSelectionModel().getSelectedItem();
			if (updateManufacturer == null) {
				MyAlert.alert("Error", "Please select a Manufacturer to Update", AlertType.ERROR);
				return;
			}
			Main.setMain(new UpdateManufacturer(updateManufacturer).main());
		});
		removeButton.setOnAction(e -> deleteButton());
		cancel.setOnAction(x -> {
			search.clear();
			manufacturerTable.setItems(Catalog.getManufacturerList());
		});
		search.setOnAction(x -> {
			ObservableList<Manufacturer> resultofSearch = searchOnManufacturer(search.getText());

			if (resultofSearch.isEmpty() && search.getText() != null && !search.getText().trim().isEmpty()) {
				MyAlert.alert("Error", "No Manufacturer Found with this ID or Name,Try Again", AlertType.ERROR);
				manufacturerTable.setItems(Catalog.getManufacturerList());
				return;
			} else {
				manufacturerTable.setItems(resultofSearch);
			}
		});
		sortComboBox.setOnAction(x -> {
			String selected = sortComboBox.getValue();
			String sql = null;

			if (selected.equals("Sort by Manufacturer Name ASC")) {
				sql = "SELECT * FROM Manufacturer ORDER BY manufacturer_name ASC";
			} else if (selected.equals("Sort by Manufacturer Name DESC")) {
				sql = "SELECT * FROM Manufacturer ORDER BY manufacturer_name DESC";
			}

			if (sql != null) {
				ObservableList<Manufacturer> sortedList = FXCollections.observableArrayList();

				try (Connection conn = DBConnection.connect(); PreparedStatement stmt = conn.prepareStatement(sql)) {
					ResultSet rs = stmt.executeQuery();
					while (rs.next()) {
						String id = rs.getString("manufacturer_id");
						String name = rs.getString("manufacturer_name");
						String location = rs.getString("manufacturer_location");
						String email = rs.getString("manufacturer_email");

						sortedList.add(new Manufacturer(id, name, location, email));
					}
				} catch (Exception ex) {
					ex.printStackTrace();
				}

				manufacturerTable.setItems(sortedList);
			}
		});

	}

}