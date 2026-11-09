package se.lexicon.view;

import se.lexicon.controller.VendingController;
import se.lexicon.model.Product;

import java.util.List;

/**
 * Console <b>View</b>. Reads input with {@code IO.readln()} and prints with {@code IO.println()}.
 * It contains no business rules and never touches the model: every user action is forwarded to the
 * {@link VendingController}, which calls back the {@code show...} methods below.
 */
public class ConsoleView implements VendingView {

    private static final String LINE = "------------------------------------";

    private VendingController controller;

    /** {@inheritDoc} */
    @Override
    public void setController(VendingController controller) {
        this.controller = controller;
    }

    /**
     * {@inheritDoc}
     * <p>
     * Prints a welcome text, asks the controller for the product list, then loops: reads a menu
     * choice and forwards it to the controller, until the user exits (or input ends).
     */
    @Override
    public void start() {
        IO.println("Welcome to Lexicon Vending Machine");
        controller.onShowProducts();

        boolean running = true;
        while (running) {
            IO.println("\n[1] Insert coin  [2] Select product  [3] Return change  [4] Show products  [0] Exit");
            IO.print("> ");
            String line = IO.readln();
            if (line == null) { // end of input
                controller.onExit();
                break;
            }
            switch (line.trim()) {
                case "1" -> {
                    IO.print("Insert coin: ");
                    Integer coin = readInt();
                    if (coin != null) controller.onInsertCoin(coin);
                }
                case "2" -> {
                    IO.print("Select product: ");
                    Integer id = readInt();
                    if (id != null) controller.onSelectProduct(id);
                }
                case "3" -> controller.onReturnChange();
                case "4" -> controller.onShowProducts();
                case "0" -> {
                    controller.onExit();
                    running = false;
                }
                default -> IO.println("Unknown option.");
            }
        }
    }

    /** {@inheritDoc} */
    @Override
    public void showProducts(List<Product> products) {
        IO.println(LINE);
        for (Product p : products) {
            String typeAndDetail = "(" + p.getType() + ", " + p.getDetail() + ")";
            IO.println(String.format("[%d] %-12s - %d kr  %-28s Stock: %d",
                    p.getId(), p.getName(), p.getPrice(), typeAndDetail, p.getQuantity()));
        }
        IO.println(LINE);
    }

    /** {@inheritDoc} */
    @Override
    public void showBalance(int balance) {
        IO.println("Balance: " + balance + " kr");
    }

    /** {@inheritDoc} */
    @Override
    public void showMessage(String message, MessageType type) {
        IO.println(message);
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
