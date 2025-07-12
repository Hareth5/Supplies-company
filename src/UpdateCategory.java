import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.layout.BorderPane;

public class UpdateCategory {
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

    Category category;

    public UpdateCategory(Category category) {
        this.category = category;
    }

    public void loadIndex(Category category) {
        if (category != null) {
            name.setText(category.getName());
            description.setText(category.getDescription());

        }
    }

    public void updateButton() {
        String nameCat = name.getText().trim();
        String descripCat = description.getText().trim();
        try {
            category.setName(nameCat);
            category.setDescription(descripCat);
        } catch (IllegalArgumentException e) {
            MyAlert.alert("Error", e.getMessage(), AlertType.ERROR);
            return;
        }
        boolean confirmation = MyAlert.alert("Confirmation", "Are you sure you need to Update this Category",
                AlertType.CONFIRMATION);
        if (!confirmation) {
            return;
        }
        String sql = "UPDATE category SET category_name = ?, category_description = ? WHERE category_id = ?";
        try (Connection conn = DBConnection.connect(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, nameCat);
            stmt.setString(2, descripCat);
            stmt.setString(3, category.getCategoryID());
            stmt.executeUpdate();
            MyAlert.alert("Success", "Category Updated Successfully ", AlertType.INFORMATION);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public BorderPane main() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("files/UpdateCategory.fxml"));
            loader.setController(this);
            BorderPane root = loader.load();
            Styling.setTitlesStyle(title);
            loadIndex(category);
            actions();
            return root;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    private void actions() {
        action.setOnAction(e -> updateButton());
        cancel.setOnAction(e -> Main.setMain(new CategoryManagement().main()));
    }
}
