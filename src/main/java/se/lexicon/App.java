package se.lexicon;

import se.lexicon.data.VendingMachineImpl;
import se.lexicon.model.Beverage;
import se.lexicon.model.Fruit;
import se.lexicon.model.Snack;
import se.lexicon.ui.ConsoleUI;

/**
 * Application entry point (the Main-Class of the runnable JAR).
 * It only wires things together: it stocks the machine and hands it to the console UI.
 */
public class App {

    /**
     * Creates a vending machine stocked with one product of each type and starts the console UI.
     *
     * @param args command line arguments (not used)
     */
    public static void main(String[] args) {
        VendingMachineImpl machine = new VendingMachineImpl();
        machine.addProduct(new Snack(1, "Chips", 15, 5, 130));
        machine.addProduct(new Beverage(2, "Cola", 20, 3, 330));
        machine.addProduct(new Fruit(3, "Apple", 10, 8, "Sweden"));

        new ConsoleUI(machine).run();
    }
}
