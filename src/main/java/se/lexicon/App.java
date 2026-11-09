package se.lexicon;

import se.lexicon.controller.VendingController;
import se.lexicon.model.Beverage;
import se.lexicon.model.Fruit;
import se.lexicon.model.Snack;
import se.lexicon.model.VendingMachineImpl;
import se.lexicon.view.ConsoleView;
import se.lexicon.view.VendingView;

/**
 * Application entry point (the Main-Class of the runnable JAR) and the MVC "composition root":
 * it creates the Model, creates a View, creates the Controller that connects them, and starts it.
 */
public class App {

    /**
     * Builds the model, view and controller and starts the application.
     *
     * @param args command line arguments (not used)
     */
    public static void main(String[] args) {
        // Model: the machine and its products
        VendingMachineImpl model = new VendingMachineImpl();
        model.addProduct(new Snack(1, "Chips", 15, 5, 130));
        model.addProduct(new Beverage(2, "Cola", 20, 3, 330));
        model.addProduct(new Fruit(3, "Apple", 10, 8, "Sweden"));

        // View: the console view (implements VendingView)
        VendingView view = new ConsoleView();

        // Controller: connects model and view (dependency injection through the constructor)
        new VendingController(model, view).start();
    }
}
