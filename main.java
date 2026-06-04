import java.sql.*;
import java.util.InputMismatchException;
import java.util.Scanner;

public class Main {
    
    // Establishing connection channel using parametric credentials
    private Connection connect() throws SQLException {
        String url = "jdbc:mysql://localhost:3306/mall_inventory";
        String user = "root";       // Your MySQL username
        String password = "yj123";   // Your MySQL password
        return DriverManager.getConnection(url, user, password);
    }

    // 1. View All Records (Passing rows into transient Product objects)
    public void displayInventory() {
        String sql = "SELECT * FROM products";
        try (Connection conn = connect();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            System.out.println("\nCURRENT INVENTORY RECORDS:");
            boolean hasData = false;
            while (rs.next()) {
                hasData = true;
                // Instantiating the encapsulated object cleanly
                Product p = new Product(
                    rs.getString("id"),
                    rs.getString("name"),
                    rs.getInt("stock"),
                    rs.getDouble("price")
                );
                System.out.println("ID: " + p.getId() + " | Name: " + p.getName() + " | Stock: " + p.getStock() + " | Price: INR " + p.getPrice());
            }
            if (!hasData) System.out.println("No records found.");
        } catch (SQLException e) {
            System.out.println("Database Error: " + e.getMessage());
        }
    }

    // 2. Insert New Record (Accepts a Product object parameter)
    public void addProduct(Product product) {
        String sql = "INSERT INTO products (id, name, stock, price) VALUES (?, ?, ?, ?)";
        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, product.getId());
            pstmt.setString(2, product.getName());
            pstmt.setInt(3, product.getStock());
            pstmt.setDouble(4, product.getPrice());
            
            pstmt.executeUpdate();
            System.out.println("Success: Product synchronized to database layer.");
        } catch (SQLException e) {
            System.out.println("Insert Error: " + e.getMessage());
        }
    }

    // 3. Search Record by ID
    public void searchProduct(String id) {
        String sql = "SELECT * FROM products WHERE id = ?";
        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, id);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                Product p = new Product(
                    rs.getString("id"),
                    rs.getString("name"),
                    rs.getInt("stock"),
                    rs.getDouble("price")
                );
                System.out.println("Found -> ID: " + p.getId() + " | Name: " + p.getName() + " | Stock: " + p.getStock() + " | Price: INR " + p.getPrice());
            } else {
                System.out.println("Product ID not found.");
            }
        } catch (SQLException e) {
            System.out.println("Search Error: " + e.getMessage());
        }
    }

    // 4. Update Record Stock
    public void updateStock(String id, int newStock) {
        String sql = "UPDATE products SET stock = ? WHERE id = ?";
        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, newStock);
            pstmt.setString(2, id);
            
            int rows = pstmt.executeUpdate();
            if (rows > 0) System.out.println("Stock tier updated successfully.");
            else System.out.println("Product ID not found.");
        } catch (SQLException e) {
            System.out.println("Update Error: " + e.getMessage());
        }
    }

    // 5. Delete a Record
    public void deleteProduct(String id) {
        String sql = "DELETE FROM products WHERE id = ?";
        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, id);
            int rows = pstmt.executeUpdate();
            if (rows > 0) System.out.println("Product purged successfully.");
            else System.out.println("Product ID not found.");
        } catch (SQLException e) {
            System.out.println("Delete Error: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        Main system = new Main();
        Scanner scanner = new Scanner(System.in);

        while (true) {
            System.out.println("\n--- RELATIONAL INVENTORY MANAGEMENT ENGINE ---");
            System.out.println("1. View All Products (SELECT)");
            System.out.println("2. Add New Product (INSERT)");
            System.out.println("3. Search Product by ID (QUERY)");
            System.out.println("4. Update Product Stock (UPDATE)");
            System.out.println("5. Delete a Product (DELETE)");
            System.out.println("6. Exit System");
            System.out.print("Choice (1-6): ");
            
            int choice;
            try {
                choice = scanner.nextInt();
                scanner.nextLine(); 
            } catch (InputMismatchException e) {
                System.out.println("Invalid input stream. Numbers only.");
                scanner.nextLine();
                continue;
            }

            switch(choice) {
                case 1:
                    system.displayInventory();
                    break;
                case 2:
                    System.out.print("Enter Product ID: ");
                    String id = scanner.nextLine().trim();
                    System.out.print("Enter Product Name: ");
                    String name = scanner.nextLine().trim();
                    System.out.print("Enter Stock: ");
                    int stock = scanner.nextInt();
                    System.out.print("Enter Price: ");
                    double price = scanner.nextDouble();
                    
                    // Instantiating the object and passing it cleanly as a single entity
                    Product newProduct = new Product(id, name, stock, price);
                    system.addProduct(newProduct);
                    break;
                case 3:
                    System.out.print("Enter Target ID: ");
                    system.searchProduct(scanner.nextLine().trim());
                    break;
                case 4:
                    System.out.print("Enter Target ID: ");
                    String uId = scanner.nextLine().trim();
                    System.out.print("Enter New Allocation Volume: ");
                    int nStock = scanner.nextInt();
                    system.updateStock(uId, nStock);
                    break;
                case 5:
                    System.out.print("Enter Purge Target ID: ");
                    system.deleteProduct(scanner.nextLine().trim());
                    break;
                case 6:
                    System.out.println("Terminating execution loop safely.");
                    scanner.close();
                    System.exit(0);
            }
        }
    }
}
