import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import java.io.IOException;

public class ShipmentDetails {
	@FXML
	private TableView<Entry> entryTable;
	@FXML
	private TableColumn<Entry, Integer> EntryIdColumn;
	@FXML
	private TableColumn<Entry, String> productIdColumn;
	@FXML
	private TableColumn<Entry, String> productNameColumn;
	@FXML
	private TableColumn<Entry, String> shipmentIdd;
	@FXML
	private TableColumn<Entry, Integer> amountColumn;
	@FXML
	private TableColumn<Entry, String> startDateColumn;
	@FXML
	private TableColumn<Entry, String> expiryDateColumn;
	@FXML
	private Button backButton;

	public BorderPane main() {
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource("files/ShipmentDetails.fxml"));
			loader.setController(this);
			BorderPane root = loader.load();

			EntryIdColumn.setCellValueFactory(new PropertyValueFactory<>("entryId"));
			productIdColumn.setCellValueFactory(new PropertyValueFactory<>("productId"));
			shipmentIdd.setCellValueFactory(new PropertyValueFactory<>("shipmentId"));
			amountColumn.setCellValueFactory(new PropertyValueFactory<>("quantity"));
			startDateColumn.setCellValueFactory(new PropertyValueFactory<>("startDate"));
			expiryDateColumn.setCellValueFactory(new PropertyValueFactory<>("endDate"));
			entryTable.setItems(Catalog.getEntryList1());
			entryTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

			actions();
			return root;
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}

	public void actions() {
		backButton.setOnAction(e -> {
			Main.setMain(new ShipmentManagement().main());
		});
	}
}