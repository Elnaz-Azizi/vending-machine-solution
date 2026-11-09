package se.lexicon.controller;

import se.lexicon.exception.VendingMachineException;
import se.lexicon.model.Product;
import se.lexicon.model.VendingMachine;
import se.lexicon.view.MessageType;
import se.lexicon.view.VendingView;

/**
 * The <b>Controller</b> in MVC. It receives user actions from the view, applies them to the model
 * ({@link VendingMachine}) and tells the view what to show. It owns all "what to say to the user"
 * decisions, so every view shows the same messages and the model stays free of any UI text logic.
 * <p>
 * Both collaborators are injected through the constructor (Dependency Injection) and typed as
 * interfaces, so the controller works with any model implementation and any view.
 */
public class VendingController {

    private final VendingMachine model;
    private final VendingView view;

    /**
     * Wires the controller to a model and a view, and registers itself with the view.
     *
     * @param model the vending machine logic
     * @param view  the view to update and to receive user actions from
     */
    public VendingController(VendingMachine model, VendingView view) {
        this.model = model;
        this.view = view;
        view.setController(this);
    }

    /** Starts the application by starting the view (the view then requests the initial data). */
    public void start() {
        view.start();
    }

    /** User asked to see the products: shows the product list and the balance. */
    public void onShowProducts() {
        view.showProducts(model.getProducts());
        view.showBalance(model.getBalance());
    }

    /**
     * User inserted a coin: adds it to the balance, or reports that the coin is not accepted.
     *
     * @param coin the coin value in kr
     */
    public void onInsertCoin(int coin) {
        if (model.insertCoin(coin)) {
            view.showMessage("Inserted " + coin + " kr", MessageType.SUCCESS);
        } else {
            view.showMessage("Invalid coin. Only 1, 2, 5, 10, 20, 50 kr accepted.", MessageType.ERROR);
        }
        view.showBalance(model.getBalance());
    }

    /**
     * User selected a product: tries to buy it and reports the dispensed product plus change,
     * or the model's reason for failing. On success the stock changed, so the product list is refreshed too.
     *
     * @param id the product id
     */
    public void onSelectProduct(int id) {
        try {
            Product product = model.purchaseProduct(id);
            String text = "Dispensing: " + product.describe();
            if (model.getLastChange() > 0) {
                text += "\nChange returned: " + model.getLastChange() + " kr";
            }
            view.showMessage(text, MessageType.SUCCESS);
            view.showProducts(model.getProducts()); // stock changed
        } catch (VendingMachineException e) {
            view.showMessage(e.getMessage(), MessageType.ERROR);
        }
        view.showBalance(model.getBalance());
    }

    /** User asked for the balance back: returns it, or says there is nothing to return. */
    public void onReturnChange() {
        int change = model.returnChange();
        if (change > 0) {
            view.showMessage("Change returned: " + change + " kr", MessageType.SUCCESS);
        } else {
            view.showMessage("No balance to return.", MessageType.INFO);
        }
        view.showBalance(model.getBalance());
    }

    /** User is leaving: returns any remaining balance and says goodbye. */
    public void onExit() {
        if (model.getBalance() > 0) {
            view.showMessage("Change returned: " + model.returnChange() + " kr", MessageType.INFO);
        }
        view.showMessage("Goodbye!", MessageType.INFO);
    }
}
