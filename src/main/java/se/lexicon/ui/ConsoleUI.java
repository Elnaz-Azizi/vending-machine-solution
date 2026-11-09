package se.lexicon.ui;

import se.lexicon.data.VendingMachine;
import se.lexicon.exception.VendingMachineException;
import se.lexicon.model.Product;

/**
 * Console front end. Contains no business rules; it only reads user input, calls the
 * {@link VendingMachine} and prints the result. Uses {@code IO.print/println/readln} (Java 25).
 */
public class ConsoleUI {

    private static final String LINE = "------------------------------------";

    /**
     * The machine this UI controls. It is received from outside (Dependency Injection) instead of
     * being created here with {@code new VendingMachineImpl()}.
     * <p>
     * Dependency Injection (DI) means a class does not build the objects it depends on; whoever
     * creates the class ({@code App}) passes them in, here through the constructor ("constructor
     * injection"). The field is typed as the {@link VendingMachine} interface, not the concrete
     * {@code VendingMachineImpl}, so ConsoleUI only knows the contract.
     * <p>
     * Benefits: loose coupling (the implementation can be swapped without editing this class),
     * easy testing (a fake machine can be injected), and a clear separation of UI and machine logic.
     * The field is {@code final}, so the dependency is always present and cannot change later.
     */
    private final VendingMachine machine;

    /**
     * Creates a UI for the given machine (constructor injection, see {@link #machine}).
     *
     * @param machine the machine this UI controls (depends on the interface, not the implementation)
     */
    public ConsoleUI(VendingMachine machine) {
        this.machine = machine;
    }

    /**
     * Shows the product list and then loops: reads a menu choice, performs it and prints the
     * balance, until the user chooses Exit (or input ends). Any remaining balance is returned on exit.
     */
    public void run() {
        IO.println("Welcome to Lexicon Vending Machine");
        showProducts();

        boolean running = true;
        while (running) {
            IO.println("\n[1] Insert coin  [2] Select product  [3] Return change  [4] Show products  [0] Exit");
            IO.print("> ");
            String line = IO.readln();
            if (line == null) { // end of input
                machine.returnChange();
                break;
            }
            String choice = line.trim();
            switch (choice) {
                case "1" -> insertCoin();
                case "2" -> selectProduct();
                case "3" -> returnChange();
                case "4" -> showProducts();
                case "0" -> {
                    if (machine.getBalance() > 0) {
                        IO.println("Change returned: " + machine.returnChange() + " kr");
                    }
                    IO.println("Goodbye!");
                    running = false;
                }
                default -> IO.println("Unknown option.");
            }
            if (running) {
                IO.println("Balance: " + machine.getBalance() + " kr");
            }
        }
    }

    /** Prints every product (id, name, price, type detail, stock) followed by the current balance. */
    private void showProducts() {
        IO.println(LINE);
        for (Product p : machine.getProducts()) {
            IO.println(String.format("[%d] %-12s - %d kr  %-28s Stock: %d",
                    p.getId(), p.getName(), p.getPrice(), shortDescription(p), p.getQuantity()));
        }
        IO.println(LINE);
        IO.println("Balance: " + machine.getBalance() + " kr");
    }

    /**
     * Builds the "(Snack, 130g)" part of the product line from its type and detail, so the table
     * can show the name and the detail in separate columns.
     *
     * @param p the product
     * @return the parenthesised type and detail
     */
    private String shortDescription(Product p) {
        return "(" + p.getType() + ", " + p.getDetail() + ")";
    }

    /** Asks for a coin value and inserts it; prints a rejection message if the value is not accepted. */
    private void insertCoin() {
        IO.print("Insert coin: ");
        Integer coin = readInt();
        if (coin == null) return;
        if (!machine.insertCoin(coin)) {
            IO.println("Invalid coin. Only 1, 2, 5, 10, 20, 50 kr accepted.");
        }
    }

    /**
     * Asks for a product id and tries to buy it. On success prints the dispensed product and any
     * change; on failure prints the machine's explanation (unknown id, out of stock, too little money).
     */
    private void selectProduct() {
        IO.print("Select product: ");
        Integer id = readInt();
        if (id == null) return;
        try {
            Product product = machine.purchaseProduct(id);
            IO.println("Dispensing: " + product.describe());
            if (machine.getLastChange() > 0) {
                IO.println("Change returned: " + machine.getLastChange() + " kr");
            }
        } catch (VendingMachineException e) {
            IO.println(e.getMessage());
        }
    }

    /** Returns the whole balance to the user, or says there is nothing to return. */
    private void returnChange() {
        int change = machine.returnChange();
        IO.println(change > 0 ? "Change returned: " + change + " kr" : "No balance to return.");
    }

    /**
     * Reads one line and parses it as an integer.
     *
     * @return the number, or {@code null} (after printing a message) if the input is not a number
     */
    private Integer readInt() {
        try {
            return Integer.parseInt(IO.readln().trim());
        } catch (NumberFormatException | NullPointerException e) {
            IO.println("Please enter a number.");
            return null;
        }
    }
}
