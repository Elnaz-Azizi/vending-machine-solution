package se.lexicon.model;

/**
 * Abstract base class for all products. A plain Product cannot be created:
 * every product must describe itself through {@link #describe()}.
 * Fields are private and validated in the constructor (encapsulation).
 */
public abstract class Product {

    private final int id;
    private final String name;
    private final int price;
    private int quantity;

    /**
     * Validates and stores the data shared by all products.
     *
     * @param id       unique id, must be positive
     * @param name     display name, must not be blank
     * @param price    price in kr, must be positive
     * @param quantity stock count, must not be negative
     * @throws IllegalArgumentException if any value is invalid
     */
    protected Product(int id, String name, int price, int quantity) {
        if (id <= 0) throw new IllegalArgumentException("Id must be positive");
        if (name == null || name.isBlank()) throw new IllegalArgumentException("Name must not be empty");
        if (price <= 0) throw new IllegalArgumentException("Price must be positive");
        if (quantity < 0) throw new IllegalArgumentException("Quantity must not be negative");
        this.id = id;
        this.name = name;
        this.price = price;
        this.quantity = quantity;
    }

    /**
     * @return the type name, e.g. "Snack"
     */
    public abstract String getType();

    /**
     * @return the type-specific detail, e.g. "130g"
     */
    public abstract String getDetail();

    /**
     * Describes the product as {@code [name] ([type], [specific detail])}.
     * Subclasses supply the type and detail, so no {@code instanceof} checks are needed (polymorphism).
     *
     * @return e.g. "Chips (Snack, 130g)"
     */
    public String describe() {
        return name + " (" + getType() + ", " + getDetail() + ")";
    }

    /**
     * @return true if at least one item is left
     */
    public boolean isInStock() {
        return quantity > 0;
    }

    /**
     * Reduces stock by one.
     *
     * @throws IllegalStateException if the product is already out of stock
     */
    public void decreaseQuantity() {
        if (quantity == 0) throw new IllegalStateException(name + " is out of stock");
        quantity--;
    }

    /** @return the unique product id */
    public int getId() {
        return id;
    }

    /** @return the display name */
    public String getName() {
        return name;
    }

    /** @return the price in kr */
    public int getPrice() {
        return price;
    }

    /** @return the number of items in stock */
    public int getQuantity() {
        return quantity;
    }
}
