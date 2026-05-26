import java.io.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.InputMismatchException;

class Product {
    private String id;
    private String name;
    private int stock;
    private double price;

    public Product(String id, String name, int stock, double price) {
        this.id = id;
        this.name = name;
        this.stock = stock;
        this.price = price;
    }

    public String getId() { return id; }
    public String getName() { return name; }
    public int getStock() { return stock; }
    public void setStock(int stock) { this.stock = stock; }
    public double getPrice() { return price; }

    public String toFileRecord() {
        return id + "," + name + "," + stock + "," + price;
    }

    @Override
    public String toString() {
        return "ID: " + id + " | Name: " + name + " | Stock: " + stock + " | Price: INR " + price;
    }
}

public class Main {
    private Map<String, Product> inventory = new HashMap<>();
    private final String FILE_NAME = "inventory.txt";

    public Main() {
        loadDataFromFile();
        autoSeedIfEmpty(); // Completely handles automatic initialization securely
    }

    private void autoSeedIfEmpty() {
        if (inventory.isEmpty()) {
            System.out.println("SYSTEM NOTICE: No existing data records detected in local storage.");
            System.out.println("Initializing database with Sample Data Initialization data entries...");
            
            addProductQuietly("P001", "Enterprise Server", 12, 145000.00);
            addProductQuietly("P002", "Firewall Router", 25, 34000.00);
            addProductQuietly("P003", "Mechanical Keyboard", 110, 4500.00);
            addProductQuietly("P004", "FHD IPS Monitor", 45, 18500.00);
            
            System.out.println("Database auto-initialization complete! System ready.");
        }
    }

    // Secondary addition helper to seed items without printing spam during setup
    private void addProductQuietly(String id, String name, int stock, double price) {
        if (!inventory.containsKey(id)) {
            inventory.put(id, new Product(id, name, stock, price));
            saveDataToFile();
        }
    }

    public void addProduct(String id, String name, int stock, double price) {
        if (inventory.containsKey(id)) {
            System.out.println("Error: Product with ID " + id + " already exists!");
        } else {
            Product newProduct = new Product(id, name, stock, price);
            inventory.put(id, newProduct);
            saveDataToFile();
            System.out.println("Product " + name + " written to database storage layer!");
        }
    }

    public void displayInventory() {
        if (inventory.isEmpty()) {
            System.out.println("DATABASE ERROR: NO RECORDS FOUND. PLEASE INSERT DATA FIRST.");
            return;
        }
        System.out.println("CURRENT INVENTORY RECORDS:");
        for (Product p : inventory.values()) {
            System.out.println(p);
        }
    }

    public void searchProduct(String id) {
        if (inventory.containsKey(id)) {
            System.out.println("Execution Query Found Target Record:");
            System.out.println(inventory.get(id));
        } else {
            System.out.println("Query Exception: Product ID " + id + " doesn't exist in storage arrays.");
        }
    }

    public void updateStock(String id, int newStock) {
        if (inventory.containsKey(id)) {
            inventory.get(id).setStock(newStock);
            saveDataToFile();
            System.out.println("Mutation Complete: Database synchronized state changes to file.");
        } else {
            System.out.println("Query Exception: Product ID not found.");
        }
    }

    public void deleteProduct(String id) {
        if (inventory.containsKey(id)) {
            String name = inventory.get(id).getName();
            inventory.remove(id);
            saveDataToFile();
            System.out.println("Record Deleted Successfully: " + name + " cleanly removed from storage.");
        } else {
            System.out.println("Query Exception: Product ID not found.");
        }
    }

    private void saveDataToFile() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_NAME))) {
            for (Product p : inventory.values()) {
                writer.write(p.toFileRecord());
                writer.newLine();
            }
        } catch (IOException e) {
            System.out.println("Disk Exception: Stream failure writing back to text repository.");
        }
    }

    private void loadDataFromFile() {
        File file = new File(FILE_NAME);
        if (!file.exists()) return;

        try (BufferedReader reader = new BufferedReader(new FileReader(FILE_NAME))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] tokens = line.split(",");
                if (tokens.length == 4) {
                    String id = tokens[0];
                    String name = tokens[1];
                    int stock = Integer.parseInt(tokens[2]);
                    double price = Double.parseDouble(tokens[3]);
                    inventory.put(id, new Product(id, name, stock, price));
                }
            }
        } catch (IOException | NumberFormatException e) {
            System.out.println("Recovery Notice: Structural mapping data errors found. Clearing buffer.");
        }
    }

    private static String getNonEmptyInput(Scanner scanner, String prompt) {
        String input = "";
        while (true) {
            System.out.print(prompt);
            input = scanner.nextLine().trim();
            if (input.isEmpty()) {
                System.out.println("❌ Validation Error: This field cannot be left blank! Please try again.");
                continue;
            }
            break;
        }
        return input;
    }

    public static void main(String[] args) {
        Main system = new Main();
        Scanner scanner = new Scanner(System.in);

        while (true) {
            System.out.println("\nPERSISTENT FILE-BASED INVENTORY ENGINE");
            System.out.println("1. View All Records (SELECT)");
            System.out.println("2. Insert New Record (INSERT)");
            System.out.println("3. Search Record by ID (QUERY)");
            System.out.println("4. Update Record Stock (UPDATE)");
            System.out.println("5. Delete a Record (DELETE)");
            System.out.println("6. Exit System");
            System.out.print("Selection (1-6): ");
            
            int choice = 0;
            try {
                choice = scanner.nextInt();
                scanner.nextLine(); 
            } catch (InputMismatchException e) {
                System.out.println("Parsing Exception: Selection fields process integer flags only.");
                scanner.nextLine(); 
                continue;
            }

            switch (choice) {
                case 1:
                    system.displayInventory();
                    break;
                case 2:
                    String id = getNonEmptyInput(scanner, "Enter Product ID: ");
                    String name = getNonEmptyInput(scanner, "Enter Product Name: ");
                    
                    int stock = 0;
                    while (true) {
                        try {
                            System.out.print("Enter Initial Stock: ");
                            stock = scanner.nextInt();
                            if (stock < 0) {
                                System.out.println("Constraints: Stock metrics cannot fall below zero bounds!");
                                continue;
                            }
                            break;
                        } catch (InputMismatchException e) {
                            System.out.println("DataType Mismatch: Non-numeric strings rejected for stock mapping.");
                            scanner.nextLine();
                        }
                    }

                    double price = 0.0;
                    while (true) {
                        try {
                            System.out.print("Enter Price (INR): ");
                            price = scanner.nextDouble();
                            if (price < 0) {
                                System.out.println("Constraints: Pricing tables require values above zero.");
                                continue;
                            }
                            break;
                        } catch (InputMismatchException e) {
                            System.out.println("DataType Mismatch: Decimal numeric signatures required.");
                            scanner.nextLine();
                        }
                    }
                    system.addProduct(id, name, stock, price);
                    break;
                case 3:
                    String searchId = getNonEmptyInput(scanner, "Enter Target Query ID: ");
                    system.searchProduct(searchId);
                    break;
                case 4:
                    String updateId = getNonEmptyInput(scanner, "Enter Target Mutation ID: ");
                    int newStock = 0;
                    while (true) {
                        try {
                            System.out.print("Enter Updated Allocation Volume: ");
                            newStock = scanner.nextInt();
                            if (newStock < 0) {
                                System.out.println("Constraints: Modification vectors must be positive integers.");
                                continue;
                            }
                            break;
                        } catch (InputMismatchException e) {
                            System.out.println("Parsing Error: Expected whole integer allocations.");
                            scanner.nextLine();
                        }
                    }
                    system.updateStock(updateId, newStock);
                    break;
                case 5:
                    String deleteId = getNonEmptyInput(scanner, "Enter Target Purge ID: ");
                    system.deleteProduct(deleteId);
                    break;
                case 6:
                    System.out.println("Exiting system. Data saved successfully.");
                    scanner.close();
                    System.exit(0);
                default:
                    System.out.println("Out-Of-Bounds Exception: Try keys matching indices 1 through 6.");
            }
        }
    }
}
