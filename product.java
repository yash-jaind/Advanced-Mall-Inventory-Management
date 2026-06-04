public class Product {
    private String id;
    private String name;
    private int stock;
    private double price;

    // Constructor to initialize variables
    public Product(String id, String name, int stock, double price) {
        this.id = id;
        this.name = name;
        this.stock = stock;
        this.price = price;
    }

    // Getters (Exposing data safely via methods)
    public String getId() { return id; }
    public String getName() { return name; }
    public int getStock() { return stock; }
    public double getPrice() { return price; }
}
