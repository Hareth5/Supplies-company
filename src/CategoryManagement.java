import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.layout.BorderPane;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;

public class CategoryManagement {
	@FXML
	private Label title;
	@FXML
	private ComboBox<String> sortComboBox;
	@FXML
	private Button cancel;
	@FXML
	private TextField search;
	@FXML
	private TableView<Category> categoryTable;

	@FXML
	private TableColumn<Category, Integer> idColumn;
	@FXML
	private TableColumn<Category, String> nameColumn;
	@FXML
	private TableColumn<Category, String> descriptionColumn;

	@FXML
	private Button updateButton;

	@FXML
	private Button deleteButton;

	public BorderPane main() {
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource("files/CategoryManagement.fxml"));
			loader.setController(this);
			BorderPane root = loader.load();

			Styling.setSmallButtonsStyle(cancel);
			Styling.setSearchTxtStyle(search);

			idColumn.setCellValueFactory(new PropertyValueFactory<>("categoryID"));
			nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
			descriptionColumn.setCellValueFactory(new PropertyValueFactory<>("description"));

			sortComboBox.getItems().addAll("Sort by Category Name ASC", "Sort by Category Name DESC");

			categoryTable.setItems(Catalog.getCategoryList());
			actions();
			return root;
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}

	public ObservableList<Category> searchOnCategory(String search) {
		ObservableList<Category> resultofSearch = FXCollections.observableArrayList();
		if (search == null || search.trim().isEmpty()) {
			MyAlert.alert("Error", "Please Enter Category ID or Category Name to Search", AlertType.ERROR);
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
			sql = "select * from Category where category_id = ?";
		} else {
			sql = "select * from Category where category_name like ?";
		}
		try (Connection conn = DBConnection.connect(); PreparedStatement stmt = conn.prepareStatement(sql)) {
			if (Num) {
				stmt.setString(1, search);
			} else {
				stmt.setString(1, "%" + search + "%");
			}
			ResultSet rs = stmt.executeQuery();
			while (rs.next()) {
				String id = rs.getString("category_id");
				String name = rs.getString("category_name");
				String description = rs.getString("category_description");
				resultofSearch.add(new Category(id, name, description));
			}
		} catch (Exception e) {
			e.printStackTrace();

		}
		return resultofSearch;
	}

	public void deleteButton() {
		Category selected = categoryTable.getSelectionModel().getSelectedItem();

		if (selected == null) {
			MyAlert.alert("Error", "Please select a Category to delete", AlertType.ERROR);
			return;
		}
		boolean confirmation = MyAlert.alert("Confirmation", "Are you sure you need to Delete this Category ? ",
				AlertType.CONFIRMATION);
		if (!confirmation) {
			return;
		}
		String sql = "DELETE FROM category WHERE category_id = ?";
		try (Connection conn = DBConnection.connect(); PreparedStatement stmt = conn.prepareStatement(sql)) {
			stmt.setString(1, selected.getCategoryID());
			stmt.executeUpdate();
			Catalog.getCategoryList().remove(selected);
			MyAlert.alert("Success", "Category Deleted Successfully ", AlertType.INFORMATION);
			categoryTable.setItems(Catalog.getCategoryList());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void actions() {
		updateButton.setOnAction(e -> {
			Category updateCategory = categoryTable.getSelectionModel().getSelectedItem();
			if (updateCategory == null) {
				MyAlert.alert("Error", "Please select a Category to Update", AlertType.ERROR);
				return;
			}
			Main.setMain(new UpdateCategory(updateCategory).main());
		});
		deleteButton.setOnAction(e -> deleteButton());
		cancel.setOnAction(x -> {
			search.clear();
			categoryTable.setItems(Catalog.getCategoryList());
		});
		search.setOnAction(x -> {
			ObservableList<Category> resultofSearch = searchOnCategory(search.getText());

			if (resultofSearch.isEmpty() && search.getText() != null && !search.getText().trim().isEmpty()) {
				MyAlert.alert("Error", "No Category Found with this ID or Name,Try Again", AlertType.ERROR);
				categoryTable.setItems(Catalog.getCategoryList());
				return;
			} else {
				categoryTable.setItems(resultofSearch);
			}
		});
		sortComboBox.setOnAction(x -> {
			String selected = sortComboBox.getValue();
			String sql = null;

			if (selected.equals("Sort by Category Name ASC")) {
				sql = "SELECT * FROM Category ORDER BY category_name ASC";
			} else if (selected.equals("Sort by Category Name DESC")) {
				sql = "SELECT * FROM Category ORDER BY category_name DESC";
			}

			if (sql != null) {
				ObservableList<Category> sortedList = FXCollections.observableArrayList();

				try (Connection conn = DBConnection.connect(); PreparedStatement stmt = conn.prepareStatement(sql)) {
					ResultSet rs = stmt.executeQuery();
					while (rs.next()) {
						String id = rs.getString("category_id");
						String name = rs.getString("category_name");
						String description = rs.getString("category_description");

						sortedList.add(new Category(id, name, description));
					}
				} catch (Exception ex) {
					ex.printStackTrace();
				}

				categoryTable.setItems(sortedList);
			}
		});

	}
}