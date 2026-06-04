import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class AppWindow extends Application {

    private static final String URL = "jdbc:mysql://localhost:3306/mall_inventory";
    private static final String USER = "root";
    private static final String PASSWORD = "yj123"; 

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Mall Inventory Management System");

        GridPane grid = new GridPane();
        grid.setPadding(new Insets(20, 20, 20, 20));
        grid.setVgap(10);
        grid.setHgap(10);

        // UI Form Layout Elements
        Label lblId = new Label("Product ID:");
        GridPane.setConstraints(lblId, 0, 0);
        TextField txtId = new TextField();
        GridPane.setConstraints(txtId, 1, 0);

        Label lblName = new Label("Product Name:");
        GridPane.setConstraints(lblName, 0, 1);
        TextField txtName = new TextField();
        GridPane.setConstraints(txtName, 1, 1);

        Label lblStock = new Label("Stock:");
        GridPane.setConstraints(lblStock, 0, 2);
        TextField txtStock = new TextField();
        GridPane.setConstraints(txtStock, 1, 2);

        Label lblPrice = new Label("Price:");
        GridPane.setConstraints(lblPrice, 0, 3);
        TextField txtPrice = new TextField();
        GridPane.setConstraints(txtPrice, 1, 3);

        // Buttons Layout
        Button btnDelete = new Button("Delete Product");
        GridPane.setConstraints(btnDelete, 0, 4);

        Button btnAdd = new Button("Add Product");
        GridPane.setConstraints(btnAdd, 1, 4);

        Button btnUpdate = new Button("Update Stock");
        GridPane.setConstraints(btnUpdate, 0, 5); 

        Button btnViewOne = new Button("View Inventory (ID)");
        GridPane.setConstraints(btnViewOne, 1, 5);

        Button btnClear = new Button("Clear Form");
        GridPane.setConstraints(btnClear, 0, 6);

        Button btnViewAll = new Button("View All Inventory");
        GridPane.setConstraints(btnViewAll, 1, 6);

        Label lblStatus = new Label("");
        GridPane.setConstraints(lblStatus, 1, 7);

        // ⚡ FEATURE 2: Auto-Load on pressing Enter inside Product ID field
        txtId.setOnAction(e -> btnViewOne.fire());

        // ⚡ FEATURE 1: Clear fields button logic
        btnClear.setOnAction(e -> {
            txtId.clear(); txtName.clear(); txtStock.clear(); txtPrice.clear();
            lblStatus.setText("Form Cleared.");
        });

        // Click Event: Insert Data (with ⚡ FEATURE 3: Input Validation)
        btnAdd.setOnAction(e -> {
            String id = txtId.getText().trim();
            String name = txtName.getText().trim();
            String stockStr = txtStock.getText().trim();
            String priceStr = txtPrice.getText().trim();

            if (id.isEmpty() || name.isEmpty() || stockStr.isEmpty() || priceStr.isEmpty()) {
                lblStatus.setText("Error: All fields must be filled!");
                return;
            }

            String query = "INSERT INTO products (id, name, stock, price) VALUES (?, ?, ?, ?)";
            try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
                 PreparedStatement pstmt = conn.prepareStatement(query)) {

                pstmt.setString(1, id);
                pstmt.setString(2, name);
                // Validation parsing
                pstmt.setInt(3, Integer.parseInt(stockStr));
                pstmt.setDouble(4, Double.parseDouble(priceStr));

                int rowsInserted = pstmt.executeUpdate();
                if (rowsInserted > 0) {
                    lblStatus.setText("Success: Saved '" + name + "'!");
                    btnClear.fire();
                }
            } catch (NumberFormatException ex) {
                lblStatus.setText("Error: Stock must be integer & Price a decimal!");
            } catch (Exception ex) {
                lblStatus.setText("Error: " + ex.getMessage());
            }
        });

        // Click Event: Delete Data
        btnDelete.setOnAction(e -> {
            String id = txtId.getText().trim();
            if (id.isEmpty()) {
                lblStatus.setText("Error: Enter Product ID to delete!");
                return;
            }

            String query = "DELETE FROM products WHERE id = ?";
            try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
                 PreparedStatement pstmt = conn.prepareStatement(query)) {
                pstmt.setString(1, id);

                int rowsDeleted = pstmt.executeUpdate();
                if (rowsDeleted > 0) {
                    lblStatus.setText("Success: Product ID " + id + " deleted!");
                    btnClear.fire();
                } else {
                    lblStatus.setText("Error: Product ID not found!");
                }
            } catch (Exception ex) {
                lblStatus.setText("Error: " + ex.getMessage());
            }
        });

        // Click Event: Update Stock Only
        btnUpdate.setOnAction(e -> {
            String id = txtId.getText().trim();
            String stockStr = txtStock.getText().trim();

            if (id.isEmpty() || stockStr.isEmpty()) {
                lblStatus.setText("Error: Enter Product ID and new Stock value!");
                return;
            }

            String query = "UPDATE products SET stock = ? WHERE id = ?";
            try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
                 PreparedStatement pstmt = conn.prepareStatement(query)) {
                pstmt.setInt(1, Integer.parseInt(stockStr));
                pstmt.setString(2, id);

                int rowsUpdated = pstmt.executeUpdate();
                if (rowsUpdated > 0) {
                    lblStatus.setText("Success: Stock updated for ID " + id + "!");
                } else {
                    lblStatus.setText("Error: Product ID not found!");
                }
            } catch (NumberFormatException ex) {
                lblStatus.setText("Error: Stock must be a valid whole number!");
            } catch (Exception ex) {
                lblStatus.setText("Error: " + ex.getMessage());
            }
        });

        // Click Event: View Particular Inventory by entering ID
        btnViewOne.setOnAction(e -> {
            String id = txtId.getText().trim();
            if (id.isEmpty()) {
                lblStatus.setText("Error: Enter a Product ID to fetch!");
                return;
            }

            String query = "SELECT name, stock, price FROM products WHERE id = ?";
            try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
                 PreparedStatement pstmt = conn.prepareStatement(query)) {
                pstmt.setString(1, id);

                try (ResultSet rs = pstmt.executeQuery()) {
                    if (rs.next()) {
                        txtName.setText(rs.getString("name"));
                        txtStock.setText(String.valueOf(rs.getInt("stock")));
                        txtPrice.setText(String.valueOf(rs.getDouble("price")));
                        lblStatus.setText("Success: Loaded details for ID " + id);
                    } else {
                        lblStatus.setText("Error: Product ID not found!");
                    }
                }
            } catch (Exception ex) {
                lblStatus.setText("Error: " + ex.getMessage());
            }
        });

        // Click Event: View All Inventory in a Pop-up Text window area box
        btnViewAll.setOnAction(e -> {
            String query = "SELECT * FROM products";
            StringBuilder inventoryList = new StringBuilder();
            inventoryList.append(String.format("%-10s %-25s %-10s %-10s\n", "ID", "Name", "Stock", "Price"));
            inventoryList.append("------------------------------------------------------------\n");

            try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
                 PreparedStatement pstmt = conn.prepareStatement(query);
                 ResultSet rs = pstmt.executeQuery()) {

                boolean hasData = false;
                while (rs.next()) {
                    hasData = true;
                    inventoryList.append(String.format("%-10s %-25s %-10d %-10.2f\n",
                            rs.getString("id"), rs.getString("name"), rs.getInt("stock"), rs.getDouble("price")));
                }

                if (!hasData) {
                    inventoryList.append("No items found in inventory database.");
                }

                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Current Inventory");
                alert.setHeaderText("Full Database Table Records");

                TextArea textArea = new TextArea(inventoryList.toString());
                textArea.setEditable(false);
                textArea.setStyle("-fx-font-family: 'Courier New';"); 
                alert.getDialogPane().setContent(textArea);
                alert.showAndWait();

            } catch (Exception ex) {
                lblStatus.setText("DB View Error: " + ex.getMessage());
            }
        });

        grid.getChildren().addAll(lblId, txtId, lblName, txtName, lblStock, txtStock, lblPrice, txtPrice, 
                                  btnAdd, btnDelete, btnUpdate, btnViewOne, btnClear, btnViewAll, lblStatus);
        
        Scene scene = new Scene(grid, 450, 520);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
