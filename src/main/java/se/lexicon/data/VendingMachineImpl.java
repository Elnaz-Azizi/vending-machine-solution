package se.lexicon.data;

import se.lexicon.exception.VendingMachineException;
import se.lexicon.model.Product;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

/** Default {@link VendingMachine}: keeps the products and the inserted balance and enforces all rules. */
public class VendingMachineImpl implements VendingMachine {

    /** Standard Swedish coin values accepted by the machine. */
    public static final Set<Integer> VALID_COINS = Set.of(1, 2, 5, 10, 20, 50);

    private final List<Product> products = new ArrayList<>();
    private int balance;
    private int lastChange;

    /**
     * Creates a machine stocked with the given products.
     *
     * @param initialProducts products to add; may be null for an empty machine
     */
    public VendingMachineImpl(List<Product> initialProducts) {
        if (initialProducts != null) {
            initialProducts.forEach(this::addProduct); // same validation as addProduct
        }
    }

    /** Creates an empty machine; add products with {@link #addProduct(Product)}. */
    public VendingMachineImpl() {
        this(null);
    }

    /**
     * Adds a product to the machine.
     *
     * @param product the product to stock
     * @throws IllegalArgumentException if the product is null or another product already has its id
     */
    public void addProduct(Product product) {
        if (product == null) throw new IllegalArgumentException("Product must not be null");
        if (findById(product.getId()) != null) {
            throw new IllegalArgumentException("A product with id " + product.getId() + " already exists");
        }
        products.add(product);
    }

    /** {@inheritDoc} */
    @Override
    public boolean insertCoin(int coin) {
        if (!VALID_COINS.contains(coin)) {
            return false;
        }
        balance += coin;
        return true;
    }

    /** {@inheritDoc} */
    @Override
    public int getBalance() {
        return balance;
    }

    /**
     * {@inheritDoc}
     * <p>
     * Checks in this order: product exists, product in stock, balance covers the price.
     * Only when all pass is anything changed: stock is reduced by 1, the price is deducted and the
     * rest of the balance is returned as change (balance becomes 0).
     */
    @Override
    public Product purchaseProduct(int id) {
        Product product = findById(id);
        if (product == null) {
            throw new VendingMachineException("Product with id " + id + " does not exist.");
        }
        if (!product.isInStock()) {
            throw new VendingMachineException(product.getName() + " is out of stock.");
        }
        if (balance < product.getPrice()) {
            throw new VendingMachineException("Insufficient balance. " + product.getName()
                    + " costs " + product.getPrice() + " kr (missing " + (product.getPrice() - balance) + " kr).");
        }
        product.decreaseQuantity();
        lastChange = balance - product.getPrice();
        balance = 0; // price deducted, the remainder is returned automatically as change
        return product;
    }

    /** {@inheritDoc} */
    @Override
    public int getLastChange() {
        return lastChange;
    }

    /** {@inheritDoc} */
    @Override
    public int returnChange() {
        int returned = balance;
        balance = 0;
        return returned;
    }

    /** {@inheritDoc} */
    @Override
    public List<Product> getProducts() {
        return Collections.unmodifiableList(products);
    }

    /**
     * Looks a product up by id.
     *
     * @param id the product id
     * @return the product, or null if no product has that id
     */
    private Product findById(int id) {
        for (Product product : products) {
            if (product.getId() == id) {
                return product;
            }
        }
        return null;
    }
}
