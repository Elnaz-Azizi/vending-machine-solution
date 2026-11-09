package se.lexicon;

import se.lexicon.controller.VendingController;
import se.lexicon.model.Beverage;
import se.lexicon.model.Fruit;
import se.lexicon.model.Snack;
import se.lexicon.model.VendingMachineImpl;
import se.lexicon.view.ConsoleView;
import se.lexicon.view.SwingView;
import se.lexicon.view.VendingView;

import java.util.Arrays;

/**
 * Application entry point (the Main-Class of the runnable JAR) and the MVC "composition root":
 * it creates the Model, picks a View, creates the Controller that connects them, and starts it.
 * <p>
 * Default is the graphical view; pass {@code --console} to use the console view instead.
 */
public class App {

    /**
     * Builds the model, view and controller and starts the application.
     *
     * @param args {@code --console} selects the console view; anything else starts the graphical view
     */
    public static void main(String[] args) {
        // Model: the machine and its products
        VendingMachineImpl model = new VendingMachineImpl();
        model.addProduct(new Snack(1, "Chips", 15, 5, 130));
        model.addProduct(new Beverage(2, "Cola", 20, 3, 330));
        model.addProduct(new Fruit(3, "Apple", 10, 8, "Sweden"));

        // View: console or Swing (both implement VendingView)
        VendingView view = Arrays.asList(args).contains("--console") ? new ConsoleView() : new SwingView();

        // Controller: connects model and view (dependency injection through the constructor)
        new VendingController(model, view).start();
    }
}
