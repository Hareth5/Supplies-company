import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextArea;
import javafx.scene.layout.BorderPane;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class DisplayStatistical {

	@FXML
	private TabPane tabPane;

	@FXML
	private TextArea productsShipmentsArea;

	@FXML
	private TextArea ordersCustomersArea;

	public BorderPane main() {
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource("files/DisplayStatistical.fxml"));
			loader.setController(this);
			BorderPane root = loader.load();

			productsShipmentsArea
					.setText("Display all available products in the system, sorted in ascending order by expiry date:\n"
							+ "-- Answer:\n" + Querey1() + "\n\n" +

							"Retrieve products categorized 'Drinks', sorted by price in descending order:\n"
							+ "-- Answer:\n" + Querey2() + "\n\n" +

							"Display a list of expired products (quantity = 0):\n" + "-- Answer:\n" + Querey3() + "\n\n"
							+

							"Display a list of expired products :\n" + "-- Answer:\n" + Querey4() + "\n\n" +

							"products that have a discount:\n" + "-- Answer:\n" + Querey5() + "\n\n" +

							"categories with number of products in each category:\n" + "-- Answer:\n" + Querey6()
							+ "\n\n");
			return root;
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}

	public String Querey1() {
		String sql = "SELECT distinct p.product_id, p.product_name,  t.shipment_id, t.expiry_date FROM Product p , Entry t where p.product_id = t.product_id and t.quantity > 0  ORDER BY t.expiry_date ";

		StringBuilder result = new StringBuilder();

		try (Connection conn = DBConnection.connect();
				PreparedStatement stmt = conn.prepareStatement(sql);
				ResultSet rs = stmt.executeQuery()) {

			while (rs.next()) {
				String id = rs.getString("product_id");
				String name = rs.getString("product_name");
				String ship_id = rs.getString("shipment_id");
				String date = rs.getString("expiry_date");
				result.append("Product ID: ").append(id).append(" Product Name: ").append(name).append(" Shipment ID :")
						.append(ship_id).append(" Expiry Date ").append(date).append("\n");
			}

		} catch (SQLException e) {
			result.append("Error: ").append(e.getMessage());
		}

		return result.toString();
	}

	public String Querey2() {
		String sql = "select distinct p.product_id, p.product_name,p.unit_price,c.category_name from product p ,category c where \r\n"
				+ "c.category_name = 'Drinks' and p.category_id and c.category_id ORDER BY p.unit_price DESC";

		StringBuilder result = new StringBuilder();

		try (Connection conn = DBConnection.connect();
				PreparedStatement stmt = conn.prepareStatement(sql);
				ResultSet rs = stmt.executeQuery()) {

			while (rs.next()) {
				String id = rs.getString("product_id");
				String name = rs.getString("product_name");
				String price = rs.getString("unit_price");
				String catName = rs.getString("category_name");
				result.append("Product ID: ").append(id).append(" Product Name: ").append(name).append(" Unit Price :")
						.append(price).append(" Category Name ").append(catName).append("\n");
			}

		} catch (SQLException e) {
			result.append("Error: ").append(e.getMessage());
		}

		return result.toString();
	}

	public String Querey3() {
		String sql = "select distinct p.product_id , p.product_name , t.expiry_date from product p , Entry t where \r\n"
				+ "p.product_id = t.product_id and  t.expiry_date < CURDATE() and p.total_quantity = 0";

		StringBuilder result = new StringBuilder();

		try (Connection conn = DBConnection.connect();
				PreparedStatement stmt = conn.prepareStatement(sql);
				ResultSet rs = stmt.executeQuery()) {

			while (rs.next()) {
				String id = rs.getString("product_id");
				String name = rs.getString("product_name");
				String date = rs.getString("expiry_date");
				result.append("Product ID: ").append(id).append(" Product Name: ").append(name).append(" Expiry date :")
						.append(date).append("\n");
			}

		} catch (SQLException e) {
			result.append("Error: ").append(e.getMessage());
		}

		return result.toString();
	}

	public String Querey4() {
		String sql = "select distinct p.product_id , p.product_name , t.expiry_date from product p , Entry t where \r\n"
				+ "p.product_id = t.product_id and t.expiry_date < CURDATE() ";

		StringBuilder result = new StringBuilder();

		try (Connection conn = DBConnection.connect();
				PreparedStatement stmt = conn.prepareStatement(sql);
				ResultSet rs = stmt.executeQuery()) {

			while (rs.next()) {
				String id = rs.getString("product_id");
				String name = rs.getString("product_name");
				String date = rs.getString("expiry_date");
				result.append("Product ID: ").append(id).append(" Product Name: ").append(name).append(" Expiry date :")
						.append(date).append("\n");
			}

		} catch (SQLException e) {
			result.append("Error: ").append(e.getMessage());
		}

		return result.toString();
	}

	public String Querey5() {
		String sql = " select distinct p.product_id , p.product_name from product p where p.discount > 0 ";

		StringBuilder result = new StringBuilder();

		try (Connection conn = DBConnection.connect();
				PreparedStatement stmt = conn.prepareStatement(sql);
				ResultSet rs = stmt.executeQuery()) {

			while (rs.next()) {
				String id = rs.getString("product_id");
				String name = rs.getString("product_name");
				result.append("Product ID: ").append(id).append(" Product Name: ").append(name).append("\n");
			}

		} catch (SQLException e) {
			result.append("Error: ").append(e.getMessage());
		}

		return result.toString();
	}

	public String Querey6() {
		String sql = " select count(p.product_id) , c.category_name from product p , Category c where p.category_id = c.category_id group by c.category_name ";

		StringBuilder result = new StringBuilder();

		try (Connection conn = DBConnection.connect();
				PreparedStatement stmt = conn.prepareStatement(sql);
				ResultSet rs = stmt.executeQuery()) {

			while (rs.next()) {
				String name = rs.getString("category_name");
				int count = rs.getInt("count(p.product_id)");
				result.append(" Category Name: ").append(name).append(" Number of Products: ").append(count)
						.append("\n");
			}

		} catch (SQLException e) {
			result.append("Error: ").append(e.getMessage());
		}

		return result.toString();
	}
}
