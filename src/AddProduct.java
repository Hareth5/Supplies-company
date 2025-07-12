import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.layout.BorderPane;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;

public class AddProduct {
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
	private Button cancel;
	@FXML
	private Button action;

	public void addProduct() {
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

		double priceProduct = Double.parseDouble(priceTemp);
		String discountTemp = discount.getText();
		if (discountTemp == null || discountTemp.trim().isEmpty()) {
			MyAlert.alert("Error", "Discount cant be Empty", AlertType.ERROR);
			return;
		}
		if (!discountTemp.matches("^\\d+(\\.\\d+)?$")) {
			MyAlert.alert("Error", "Discount must be numbers only", AlertType.ERROR);
			return;
		}
		if (manufacturer.getValue() == null || category.getValue() == null) {
			MyAlert.alert("Error", "Please select both Manufacturer and Category", AlertType.ERROR);
			return;
		}
		String descripProduct = description.getText().trim();
		Category categoryy = category.getValue();
		String cat_id = categoryy.getCategoryID();
		Manufacturer manu = manufacturer.getValue();
		String manu_id = manu.getId();
		int totalQuantity = 0;
		double discount1 = Double.parseDouble(discountTemp);

		Product newProduct;
		try {
			newProduct = new Product(nameProduct, descripProduct, priceProduct, 0, discount1, manu, categoryy);
		} catch (IllegalArgumentException e) {
			MyAlert.alert("Error", e.getMessage(), AlertType.ERROR);
			return;
		}
		if (manufacturer.getValue() == null || category.getValue() == null) {
			MyAlert.alert("Error", "Please select both Manufacturer and Category", AlertType.ERROR);
			return;
		}
		boolean confirmation = MyAlert.alert("Confirmation", "Are you sure you need to add this Product ?",
				AlertType.CONFIRMATION);
		if (!confirmation) {
			return;
		}

		String sql = "INSERT INTO product (product_name, unit_price, product_description,manufacturer_id,category_id,total_quantity, discount) VALUES (?, ?, ?, ?, ?, ?, ?)";
		try (Connection conn = DBConnection.connect(); PreparedStatement stmt = conn.prepareStatement(sql)) {
			stmt.setString(1, nameProduct);
			stmt.setDouble(2, priceProduct);
			stmt.setString(3, descripProduct);
			stmt.setString(4, manu_id);
			stmt.setString(5, cat_id);
			stmt.setInt(6, totalQuantity);
			stmt.setDouble(7, discount1);

			stmt.executeUpdate();
			Catalog.getProductList().add(newProduct);
			MyAlert.alert("Success", "Product Added Succesfully", AlertType.INFORMATION);
			name.clear();
			description.clear();
			unitPrice.clear();
			category.setValue(null);
			manufacturer.setValue(null);
			discount.clear();

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public BorderPane main() {
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource("files/AddProduct.fxml"));
			loader.setController(this);
			BorderPane root = loader.load();
			Styling.setTitlesStyle(title);
			category.setItems(Catalog.getCategoryList());
			manufacturer.setItems(Catalog.getManufacturerList());
			actions();
			return root;
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}

	private void actions() {
		action.setOnAction(e -> addProduct());
		cancel.setOnAction(e -> Main.setMain(new ProductManagement().main()));
	}
}
