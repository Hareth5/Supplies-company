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

public class ProductManagement {
	@FXML
	private ComboBox<Category> category;
	@FXML
	private ComboBox<Manufacturer> manufacturer;
	@FXML
	private ComboBox<String> sortBy;
	@FXML
	private Button cancel;
	@FXML
	private TextField search;
	@FXML
	private TableView<Product> productTable;
	@FXML
	private TableColumn<Product, String> idColumn;
	@FXML
	private TableColumn<Product, String> nameColumn;
	@FXML
	private TableColumn<Product, Double> unitPriceColumn;
	@FXML
	private TableColumn<Product, String> descriptionColumn;
	@FXML
	private TableColumn<Product, String> manufacturerColumn;
	@FXML
	private TableColumn<Product, String> categoryColumn;
	@FXML
	private TableColumn<Product, Integer> quantityColumn;
	@FXML
	private TableColumn<Product, Double> discountColumn;
	@FXML
	private Button categoryManagement;
	@FXML
	private Button manufacturerManagement;
	@FXML
	private Button customerManagement;
	@FXML
	private Button shipmentManagement;
	@FXML
	private Button orderManagement;
	@FXML
	private Button payment;
	@FXML
	private Button returnedProducts;
	@FXML
	private Button update;
	@FXML
	private Button remove;
	@FXML
	private Button displayStatistical;

	public BorderPane main() {
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource("files/ProductManagement.fxml"));
			loader.setController(this);
			BorderPane root = loader.load();

			Styling.setSmallButtonsStyle(cancel);
			Styling.setSearchTxtStyle(search);

			idColumn.setCellValueFactory(new PropertyValueFactory<>("productId"));
			nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
			unitPriceColumn.setCellValueFactory(new PropertyValueFactory<>("unitPrice"));
			descriptionColumn.setCellValueFactory(new PropertyValueFactory<>("description"));
			manufacturerColumn.setCellValueFactory(new PropertyValueFactory<>("manufacturer"));
			categoryColumn.setCellValueFactory(new PropertyValueFactory<>("category"));
			quantityColumn.setCellValueFactory(new PropertyValueFactory<>("totalQuantity"));
			discountColumn.setCellValueFactory(new PropertyValueFactory<>("discount"));

			productTable.setItems(Catalog.getProductList());

			category.getItems().addAll(Catalog.getCategoryList());
			manufacturer.getItems().addAll(Catalog.getManufacturerList());
			sortBy.setPromptText("Sort By : ");
			sortBy.getItems().addAll("Sort By Name", "Sort By Price", "Sort By Quantity");
			actions();

			return root;
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}

	private Manufacturer findManufacturerById(String id) {
		for (Manufacturer m : Catalog.getManufacturerList()) {
			if (m.getId().equals(id))
				return m;
		}
		return null;
	}

	private Category findCategoryById(String id) {
		for (Category c : Catalog.getCategoryList()) {
			if (c.getCategoryID().equals(id))
				return c;
		}
		return null;
	}

	public void deleteButton() {
		Product selected = productTable.getSelectionModel().getSelectedItem();

		if (selected == null) {
			MyAlert.alert("Error", "Please select a Product to delete", AlertType.ERROR);
			return;
		}
		boolean confirmation = MyAlert.alert("Confirmation", "Are you sure you need to Delete this Product ? ",
				AlertType.CONFIRMATION);
		if (!confirmation) {
			return;
		}
		String sql = "DELETE FROM product WHERE product_id = ?";
		try (Connection conn = DBConnection.connect(); PreparedStatement stmt = conn.prepareStatement(sql)) {
			stmt.setString(1, selected.getProductId());
			stmt.executeUpdate();
			Catalog.getProductList().remove(selected);
			MyAlert.alert("Success", "Product Deleted Successfully ", AlertType.INFORMATION);
			productTable.setItems(Catalog.getProductList());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public ObservableList<Product> searchOnProduct(String search) {
		ObservableList<Product> resultofSearch = FXCollections.observableArrayList();
		if (search == null || search.trim().isEmpty()) {
			MyAlert.alert("Error", "Please Enter Product ID or Product Name to Search", AlertType.ERROR);
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
			sql = "select * from product where product_id = ?";
		} else {
			sql = "select * from product where product_name like ?";
		}
		try (Connection conn = DBConnection.connect(); PreparedStatement stmt = conn.prepareStatement(sql)) {
			if (Num) {
				stmt.setString(1, search);
			} else {
				stmt.setString(1, "%" + search + "%");
			}
			ResultSet rs = stmt.executeQuery();
			while (rs.next()) {
				String id = rs.getString("product_id");
				String name = rs.getString("product_name");
				double priceProduct = rs.getDouble("unit_price");
				String description = rs.getString("product_description");
				String manu_id = rs.getString("manufacturer_id");
				Manufacturer man = null;
				for (Manufacturer man1 : Catalog.getManufacturerList()) {
					if (man1.getId().equals(manu_id)) {
						man = man1;
					}
				}

				Category cat = null;
				String cat_id = rs.getString("category_id");
				for (Category cat1 : Catalog.getCategoryList()) {
					if (cat1.getCategoryID().equals(cat_id)) {
						cat = cat1;
					}
				}
				int totalQuantity = rs.getInt("total_quantity");
				double discount = rs.getDouble("discount");
				resultofSearch.add(new Product(id, name, description, priceProduct, totalQuantity, discount, man, cat));
			}
		} catch (Exception e) {
			e.printStackTrace();

		}
		return resultofSearch;
	}

	private void actions() {
		categoryManagement.setOnAction(e -> Main.setMain(new CategoryManagement().main()));
		manufacturerManagement.setOnAction(e -> Main.setMain(new ManufacturerManagement().main()));
		customerManagement.setOnAction(e -> Main.setMain(new CustomerManagement().main()));
		shipmentManagement.setOnAction(e -> Main.setMain(new ShipmentManagement().main()));
		orderManagement.setOnAction(e -> Main.setMain(new OrderManagement().main()));
		payment.setOnAction(e -> Main.setMain(new PaymentManagement().main()));
		displayStatistical.setOnAction(e -> Main.setMain(new DisplayStatistical().main()));
//        returnedProducts.setOnAction(e -> Main.setMain(new CategoryManagement().main()));
		remove.setOnAction(x -> {
			deleteButton();
		});
		update.setOnAction(x -> {
			Product updateProduct = productTable.getSelectionModel().getSelectedItem();
			if (updateProduct == null) {
				MyAlert.alert("Error", "Please select a Product to Update", AlertType.ERROR);
				return;
			}
			Main.setMain(new UpdateProduct(updateProduct).main());
		});
		cancel.setOnAction(x -> {
			search.clear();
			productTable.setItems(Catalog.getProductList());
		});
		search.setOnAction(x -> {
			ObservableList<Product> resultofSearch = searchOnProduct(search.getText());

			if (resultofSearch.isEmpty() && search.getText() != null && !search.getText().trim().isEmpty()) {
				MyAlert.alert("Error", "No Product Found with this ID or Name,Try Again", AlertType.ERROR);
				productTable.setItems(Catalog.getProductList());
				return;
			} else {
				productTable.setItems(resultofSearch);
			}
		});
		category.setOnAction(x -> {
			String catId = category.getValue().getCategoryID();
			ObservableList<Product> sortedList = FXCollections.observableArrayList();

			String sql = "SELECT * FROM Product where category_id = ?";

			try (Connection conn = DBConnection.connect(); PreparedStatement stmt = conn.prepareStatement(sql)) {

				stmt.setString(1, catId);
				ResultSet rs = stmt.executeQuery();
				while (rs.next()) {
					String id = rs.getString("product_id");
					String name = rs.getString("product_name");
					double priceProduct = rs.getDouble("unit_price");
					String description = rs.getString("product_description");
					Manufacturer man = findManufacturerById(rs.getString("manufacturer_id"));
					Category cat = findCategoryById(rs.getString("category_id"));
					int totalQuantity = rs.getInt("total_quantity");
					double discount = rs.getDouble("discount");
					sortedList.add(new Product(id, name, description, priceProduct, totalQuantity, discount, man, cat));
				}
			} catch (Exception ex) {
				ex.printStackTrace();
			}

			productTable.setItems(sortedList);

		});
		manufacturer.setOnAction(x -> {
			String manuId = manufacturer.getValue().getId();
			ObservableList<Product> sortedList = FXCollections.observableArrayList();
			String sql = "SELECT * FROM Product where manufacturer_id = ?";

			try (Connection conn = DBConnection.connect(); PreparedStatement stmt = conn.prepareStatement(sql)) {

				stmt.setString(1, manuId);
				ResultSet rs = stmt.executeQuery();
				while (rs.next()) {
					String id = rs.getString("product_id");
					String name = rs.getString("product_name");
					double priceProduct = rs.getDouble("unit_price");
					String description = rs.getString("product_description");
					Manufacturer man = findManufacturerById(rs.getString("manufacturer_id"));
					Category cat = findCategoryById(rs.getString("category_id"));
					int totalQuantity = rs.getInt("total_quantity");
					double discount = rs.getDouble("discount");
					sortedList.add(new Product(id, name, description, priceProduct, totalQuantity, discount, man, cat));
				}
			} catch (Exception ex) {
				ex.printStackTrace();
			}

			productTable.setItems(sortedList);

		});
		sortBy.setOnAction(x -> {
			String selected = sortBy.getValue();
			String sql = null;

			if (selected.equals("Sort By Name")) {
				sql = "SELECT * FROM Product ORDER BY product_name ASC";
			} else if (selected.equals("Sort By Price")) {
				sql = "SELECT * FROM Product ORDER BY unit_price ASC";
			} else if (selected.equals("Sort By Quantity")) {
				sql = "SELECT * FROM Product ORDER BY total_quantity ASC";
			}

			if (sql != null) {
				ObservableList<Product> sortedList = FXCollections.observableArrayList();

				try (Connection conn = DBConnection.connect(); PreparedStatement stmt = conn.prepareStatement(sql)) {
					ResultSet rs = stmt.executeQuery();
					while (rs.next()) {
						String id = rs.getString("product_id");
						String name = rs.getString("product_name");
						double priceProduct = rs.getDouble("unit_price");
						String description = rs.getString("product_description");
						Manufacturer man = findManufacturerById(rs.getString("manufacturer_id"));
						Category cat = findCategoryById(rs.getString("category_id"));
						int totalQuantity = rs.getInt("total_quantity");
						double discount = rs.getDouble("discount");

						sortedList.add(
								new Product(id, name, description, priceProduct, totalQuantity, discount, man, cat));
					}
				} catch (Exception ex) {
					ex.printStackTrace();
				}

				productTable.setItems(sortedList);
			}
		});

	}

}