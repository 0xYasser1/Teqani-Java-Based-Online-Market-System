
public class Product {
    private int productId;
    private String name;
    private double price;
    private int stock;

    public Product(int productId, String name, double price, int stock) {
        this.productId = productId;
        this.name = name;
        this.price = price;
        this.stock = stock;
    }

    public int getProductId() {
        return productId;
    }

    public String getName() {
        return name;
    }

    public double getPrice() {
        return price;
    }

    public int getStock() {
        return stock;
    }


    //setter for stock because it's the only one meant to change
    public void setStock(int stock) {
        this.stock = stock;
    }

    //equals()  to make the products with same ID are equal
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Product)) return false;
        Product p = (Product) o;
        return productId == p.productId;
    }

    // hashCode() without it it the HashMap stores same product in different spots , with it  HashMap finds and updates correctly
    @Override
    public int hashCode() {
        return Integer.hashCode(productId);
    }
}

