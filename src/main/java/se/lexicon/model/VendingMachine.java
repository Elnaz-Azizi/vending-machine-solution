package se.lexicon.model;

import java.util.List;

/**
 * Contract for the vending machine logic (no console or window code). The controller depends on this interface,
 * so the implementation can change without touching the controller or the views.
 */
public interface VendingMachine {

    /**
     * Adds a coin to the balance if it is an accepted value (1, 2, 5, 10, 20 or 50 kr).
     *
     * @param coin the coin value in kr
     * @return true if accepted; false if rejected (the balance is unchanged)
     */
    boolean insertCoin(int coin);

    /**
     * @return the money currently inserted, in kr
     */
    int getBalance();

    /**
     * Buys a product by id. Deducts the price, reduces stock by 1 and returns any remaining
     * balance automatically (see {@link #getLastChange()}).
     *
     * @param id the product id
     * @return the purchased product
     * @throws se.lexicon.exception.VendingMachineException if the product does not exist,
     *                                                      is out of stock, or the balance is too low
     *                                                      (state is left unchanged in that case)
     */
    Product purchaseProduct(int id);

    /**
     * @return the change returned automatically by the most recent successful purchase, in kr
     */
    int getLastChange();

    /**
     * Gives back the whole balance and resets it to 0.
     *
     * @return the amount returned (0 if there was nothing to return)
     */
    int returnChange();

    /**
     * @return a read-only view of all products in the machine
     */
    List<Product> getProducts();
}
