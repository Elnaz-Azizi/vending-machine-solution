package se.lexicon.view;

import se.lexicon.controller.VendingController;
import se.lexicon.model.Product;

import java.util.List;

/**
 * The <b>View</b> contract in MVC. A view only displays data and forwards user actions to the
 * {@link VendingController}; it never changes the model itself. Because the controller depends on
 * this interface, the console view and the Swing view are interchangeable.
 */
public interface VendingView {

    /**
     * Connects the view to its controller. Called once by the controller's constructor.
     *
     * @param controller the controller that handles this view's user actions
     */
    void setController(VendingController controller);

    /**
     * Starts the view and asks the controller for the initial data
     * ({@link VendingController#onShowProducts()}). The console view blocks here until the user
     * exits; the Swing view returns immediately.
     */
    void start();

    /**
     * Displays the products.
     *
     * @param products read-only list of the products currently in the machine
     */
    void showProducts(List<Product> products);

    /**
     * Displays the current balance.
     *
     * @param balance money inserted, in kr
     */
    void showBalance(int balance);

    /**
     * Displays a status message.
     *
     * @param message the text
     * @param type    how to style it
     */
    void showMessage(String message, MessageType type);
}
