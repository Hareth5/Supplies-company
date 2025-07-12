import javafx.beans.property.SimpleIntegerProperty;

public class Sale {
    private SimpleIntegerProperty saleId;
    private Order order;
    private Product product;
    private SimpleIntegerProperty quantity;

    public Sale(Order order, Product product, int quantity) throws IllegalArgumentException {
        this.saleId = new SimpleIntegerProperty();
        this.quantity = new SimpleIntegerProperty();

        setOrder(order);
        setProduct(product);
        setQuantity(quantity);
    }

    public Sale(Product product, int quantity) throws IllegalArgumentException {
        this.saleId = new SimpleIntegerProperty();
        this.quantity = new SimpleIntegerProperty();

        setProduct(product);
        setQuantity(quantity);
    }

    public Sale(int saleId, Order order, Product product, int quantity) throws IllegalArgumentException {
        this.saleId = new SimpleIntegerProperty(saleId);
        this.quantity = new SimpleIntegerProperty();

        setOrder(order);
        setProduct(product);
        setQuantity(quantity);
    }

    public int getSaleId() {
        return saleId.get();
    }

    public Order getOrder() {
        return order;
    }

    public void setOrder(Order order) throws IllegalArgumentException {
        if (order == null)
            throw new IllegalArgumentException("Order cannot be null");

        this.order = order;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) throws IllegalArgumentException {
        if (product == null)
            throw new IllegalArgumentException("Product cannot be null");

        this.product = product;
    }

    public int getQuantity() {
        return quantity.get();
    }

    public void setQuantity(int quantity) throws IllegalArgumentException {
        if (quantity <= 0)
            throw new IllegalArgumentException("Quantity must be positive");

        this.quantity.set(quantity);
    }
}