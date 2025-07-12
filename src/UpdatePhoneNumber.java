import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UpdatePhoneNumber {
    @FXML
    private Label title;
    @FXML
    private TextField phoneNum;
    @FXML
    private Button cancel;
    @FXML
    private Button update;
    private PhoneNumber phoneNumber;

    public UpdatePhoneNumber(PhoneNumber phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public BorderPane main() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("files/UpdatePhoneNumber.fxml"));
            loader.setController(this);
            BorderPane root = loader.load();

            Styling.setTitlesStyle(title);
            loadData();
            actions();
            return root;

        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    private void loadData() {
        phoneNum.setText(phoneNumber.getPhoneNumber());
    }

    private void actions() {
        cancel.setOnAction(e -> Main.setMain(new PhoneNumberManagement().main()));

        update.setOnAction(e -> {
            String phone = phoneNum.getText().trim();
            if (phone.isEmpty() || !phone.matches("[0-9+-]+")) {
                MyAlert.alert("Error", "You must enter valid phone number",
                        Alert.AlertType.ERROR);
                return;
            }

            try (Connection connection = DBConnection.connect();
                 PreparedStatement statement = connection.prepareStatement("select count(*) from phone where " +
                         "customer_phone = ?")) {

                statement.setString(1, phone);
                ResultSet rs = statement.executeQuery();

                if (rs.next()) {
                    int count = rs.getInt(1);
                    if (count > 0) {
                        MyAlert.alert("Error", "This phone number already exist", Alert.AlertType.ERROR);
                        return;
                    }
                }

            } catch (SQLException ex) {
                MyAlert.alert("Error", ex.getMessage(), Alert.AlertType.ERROR);
            }

            try (Connection connection = DBConnection.connect();
                 PreparedStatement statement = connection.prepareStatement("update phone set customer_phone = ? " +
                         "where customer_phone = ?")) {
                statement.setString(1, phone);
                statement.setString(2, phoneNumber.getPhoneNumber());
                statement.executeUpdate();

                phoneNumber.setPhoneNumber(phone);

                MyAlert.alert("Success", "Phone number updated successfully", Alert.AlertType.INFORMATION);
                Main.setMain(new PhoneNumberManagement().main());

            } catch (SQLException ex) {
                MyAlert.alert("Error", ex.getMessage(), Alert.AlertType.ERROR);
            }
        });
    }
}
