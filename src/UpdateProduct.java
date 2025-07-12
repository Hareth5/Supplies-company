import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;

public class UpdateProduct {
	Product product;
	@FXML
	private Label title;
	@FXML
	private TextField name;
	@FXML
	private TextField unitPrice;
	@FXML
	private TextArea description;
	@FXML
	private ComboBox<Manufacturer> manufacturer;
	@FXML
	private ComboBox<Category> category;
	@FXML
	private TextField discount;
	@FXML
	private TextField quantity;
	@FXML
	private Button cancel;
	@FXML
	private Button action;

	public UpdateProduct(Product product) {
		this.product = product;
	}

	public void loadIndex(Product product) {
		if (product != null) {
			name.setText(product.getName());
			unitPrice.setText(String.valueOf(product.getUnitPrice()));
			description.setText(product.getDescription());
			manufacturer.setValue(product.getManufacturer());
			category.setValue(product.getCategory());
			quantity.setText(String.valueOf(product.getTotalQuantity()));
			discount.setText(String.valueOf(product.getDiscount()));

		}
	}

	public void updateButton() {
		String nameProduct = name.getText().trim();
		String priceTemp = unitPrice.getText().trim();
		if (priceTemp == null || priceTemp.trim().isEmpty()) {
			MyAlert.alert("Error", "Unit price cant be Empty", AlertType.ERROR);
			return;

		}
		if (!priceTemp.matches("^\\d+(\\.\\d+)?$")) {
			MyAlert.alert("Error", "Unit price must be numbers only", AlertType.ERROR);
			return;
		}
		double unitPrice1 = Double.parseDouble(priceTemp);
		String descripProduct = description.getText();
		Manufacturer manuName = manufacturer.getValue();
		Category catName = category.getValue();
		int quantity1 = Integer.parseInt(quantity.getText());
		String discountTemp = discount.getText();
		if (discountTemp == null || discountTemp.trim().isEmpty()) {
			MyAlert.alert("Error", "Discount cant be Empty", AlertType.ERROR);
			return;
		}
		if (!discountTemp.matches("^\\d+(\\.\\d+)?$")) {
			MyAlert.alert("Error", "Discount must be numbers only", AlertType.ERROR);
			return;
		}
		double discount1 = Double.parseDouble(discountTemp);
		if (manufacturer.getValue() == null || category.getValue() == null) {
			MyAlert.alert("Error", "Please select both Manufacturer and Category", AlertType.ERROR);
			return;
		}

		try {
			product.setName(nameProduct);
			product.setUnitPrice(unitPrice1);
			product.setDescription(descripProduct);
			product.setManufacturer(manuName);
			product.setCategory(catName);
			product.setTotalQuantity(quantity1);
			product.setDiscount(discount1);
		} catch (IllegalArgumentException e) {
			MyAlert.alert("Error", e.getMessage(), AlertType.ERROR);
			return;
		}
		boolean confirmation = MyAlert.alert("Confirmation", "Are you sure you need to Update this Product ?",
				AlertType.CONFIRMATION);
		if (!confirmation) {
			return;
		}
		String sql = "UPDATE product SET product_name = ?, unit_price = ? , product_description = ? ,manufacturer_id = ? ,category_id =  ?,total_quantity = ? , discount =  ? WHERE product_id = ?";
		try (Connection conn = DBConnection.connect(); PreparedStatement stmt = conn.prepareStatement(sql)) {
			stmt.setString(1, nameProduct);
			stmt.setDouble(2, unitPrice1);
			stmt.setString(3, descripProduct);
			stmt.setString(4, manuName.getId());
			stmt.setString(5, catName.getCategoryID());
			stmt.setInt(6, quantity1);
			stmt.setDouble(7, discount1);
			stmt.setString(8, product.getProductId());
			stmt.executeUpdate();
			MyAlert.alert("Success", "Product Updated Successfully ", AlertType.INFORMATION);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public BorderPane main() {
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource("files/UpdateProduct.fxml"));
			loader.setController(this);
			BorderPane root = loader.load();
			Styling.setTitlesStyle(title);
			category.setItems(Catalog.getCategoryList());
			manufacturer.setItems(Catalog.getManufacturerList());
			loadIndex(product);
			actions();
			return root;
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}

	private void actions() {
		action.setOnAction(e -> updateButton());
		cancel.setOnAction(e -> Main.setMain(new ProductManagement().main()));
	}
}
