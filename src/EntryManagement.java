import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafx.scene.control.Alert.AlertType;

public class EntryManagement {
	@FXML
	private ComboBox<Product> productComboBox;
	@FXML
	private TextField amountField;
	@FXML
	private DatePicker startDatePicker;
	@FXML
	private DatePicker expiryDatePicker;
	@FXML
	private Button addButton;
	@FXML
	private Button cancelButton;

	static ObservableList<Entry> entryListTemp = FXCollections.observableArrayList();

	public void initialize() {
		productComboBox.setItems(Catalog.getProductList());
		addButton.setOnAction(x1 -> {
			addButton();
		});
		cancelButton.setOnAction(x->{
			Stage stage = (Stage) cancelButton.getScene().getWindow();
			stage.close();
		});
		productComboBox.setPromptText("Select a product");
	}

	public void addButton() {
		if (productComboBox.getValue() == null) {
			MyAlert.alert("Error", "Product cant be Empty", AlertType.ERROR);
			return;
		}

		Product product = productComboBox.getValue();
		int shipment_id = NewShipment.currentShipmentId;
		String productId = product.getProductId();
		String productName = product.getName();
		String quanTemp = amountField.getText();

		if (quanTemp == null || quanTemp.trim().isEmpty()) {
			MyAlert.alert("Error", "Quantity cant be Empty", AlertType.ERROR);
			return;

		}
		if (!quanTemp.matches("^\\d+(\\.\\d+)?$")) {
			MyAlert.alert("Error", "Quantity must be numbers only", AlertType.ERROR);
			return;
		}
		int quantity = Integer.parseInt(quanTemp);
		String startDate = startDatePicker.getValue().toString();
		String expiryDate = expiryDatePicker.getValue().toString();
		Entry entry;
		try {
			entry = new Entry(startDate, expiryDate, quantity, String.valueOf(shipment_id), productId, productName);
		} catch (IllegalArgumentException e) {
			MyAlert.alert("Error", e.getMessage(), AlertType.ERROR);
			return;
		}
		entryListTemp.add(entry);
		Stage stage = (Stage) addButton.getScene().getWindow();
		stage.close();
	}

}
