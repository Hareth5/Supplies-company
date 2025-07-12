import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.layout.BorderPane;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;

public class AddCategory {
    @FXML
    protected Label title;

    @FXML
    protected TextField name;

    @FXML
    protected TextArea description;

    @FXML
    protected Button action;

    @FXML
    private Button cancel;

    public void addCategory() {
        String nameCat = name.getText().trim();
        String descripCat = description.getText().trim();
        Category newCategory;
        try {
            newCategory = new Category(nameCat, descripCat);
        } catch (IllegalArgumentException e) {
            MyAlert.alert("Error", e.getMessage(), AlertType.ERROR);
            return;
        }
        boolean confirmation = MyAlert.alert("Confirmation", "Are you sure you need to add this Category",
                AlertType.CONFIRMATION);
        if (!confirmation) {
            return;
        }

        String sql = "INSERT INTO category (category_name, category_description) VALUES (?, ?)";
        try (Connection conn = DBConnection.connect(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, nameCat);
            stmt.setString(2, descripCat);
            stmt.executeUpdate();
            Catalog.getCategoryList().add(newCategory);
            MyAlert.alert("Success", "Category Added Successfully", AlertType.INFORMATION);
            name.clear();
            description.clear();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public BorderPane main() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("files/AddCategory.fxml"));
            loader.setController(this);
            BorderPane root = loader.load();
            Styling.setTitlesStyle(title);
            actions();
            return root;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    private void actions() {
        action.setOnAction(e -> addCategory());
        cancel.setOnAction(e -> Main.setMain(new ProductManagement().main()));
    }

}
