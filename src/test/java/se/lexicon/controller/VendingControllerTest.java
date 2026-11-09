package se.lexicon.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import se.lexicon.model.Beverage;
import se.lexicon.model.Product;
import se.lexicon.model.Snack;
import se.lexicon.model.VendingMachineImpl;
import se.lexicon.view.MessageType;
import se.lexicon.view.VendingView;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests the controller with a fake view. This shows the benefit of MVC: the controller depends only on
 * the {@link VendingView} interface, so it can be tested without any console or window.
 */
class VendingControllerTest {

    /** A view that just records what the controller asked it to show. */
    private static class FakeView implements VendingView {
        VendingController controller;
        boolean started;
        List<Product> lastProducts;
        int lastBalance = -1;
        final List<String> messages = new ArrayList<>();
        final List<MessageType> types = new ArrayList<>();

        @Override
        public void setController(VendingController controller) {
            this.controller = controller;
        }

        @Override
        public void start() {
            started = true;
        }

        @Override
        public void showProducts(List<Product> products) {
            lastProducts = products;
        }

        @Override
        public void showBalance(int balance) {
            lastBalance = balance;
        }

        @Override
        public void showMessage(String message, MessageType type) {
            messages.add(message);
            types.add(type);
        }

        String lastMessage() {
            return messages.get(messages.size() - 1);
        }

        MessageType lastType() {
            return types.get(types.size() - 1);
        }
    }

    private VendingMachineImpl model;
    private FakeView view;
    private VendingController controller;

    /** Fresh model (Chips id 1 = 15 kr, Cola id 2 = 20 kr), fake view and controller for every test. */
    @BeforeEach
    void setUp() {
        model = new VendingMachineImpl();
        model.addProduct(new Snack(1, "Chips", 15, 5, 130));
        model.addProduct(new Beverage(2, "Cola", 20, 3, 330));
        view = new FakeView();
        controller = new VendingController(model, view);
    }

    /** The controller registers itself with the view and start() starts the view. */
    @Test
    void constructorRegistersControllerAndStartStartsView() {
        assertSame(controller, view.controller);
        controller.start();
        assertTrue(view.started);
    }

    /** onShowProducts sends the product list and the balance to the view. */
    @Test
    void showProductsUpdatesView() {
        controller.onShowProducts();
        assertEquals(2, view.lastProducts.size());
        assertEquals(0, view.lastBalance);
    }

    /** A valid coin updates the model and the view's balance. */
    @Test
    void insertValidCoin() {
        controller.onInsertCoin(10);
        assertEquals(10, model.getBalance());
        assertEquals(10, view.lastBalance);
        assertEquals(MessageType.SUCCESS, view.lastType());
    }

    /** An invalid coin shows an error and the balance stays 0. */
    @Test
    void insertInvalidCoin() {
        controller.onInsertCoin(7);
        assertEquals(0, model.getBalance());
        assertEquals(MessageType.ERROR, view.lastType());
        assertTrue(view.lastMessage().contains("Invalid coin"));
    }

    /** A successful purchase reports the product and the change, and refreshes products and balance. */
    @Test
    void purchaseSuccessShowsProductAndChange() {
        controller.onInsertCoin(20);
        controller.onSelectProduct(1);
        assertEquals(MessageType.SUCCESS, view.lastType());
        assertTrue(view.lastMessage().contains("Dispensing: Chips (Snack, 130g)"));
        assertTrue(view.lastMessage().contains("Change returned: 5 kr"));
        assertEquals(0, view.lastBalance);
        assertEquals(4, view.lastProducts.get(0).getQuantity());
    }

    /** A failed purchase shows the model's message as an error and keeps the balance. */
    @Test
    void purchaseFailureShowsError() {
        controller.onInsertCoin(1);
        controller.onSelectProduct(2);
        assertEquals(MessageType.ERROR, view.lastType());
        assertTrue(view.lastMessage().contains("Insufficient balance"));
        assertEquals(1, view.lastBalance);
    }

    /** Returning change reports the amount, then reports nothing to return. */
    @Test
    void returnChange() {
        controller.onInsertCoin(50);
        controller.onReturnChange();
        assertEquals("Change returned: 50 kr", view.lastMessage());
        assertEquals(0, view.lastBalance);

        controller.onReturnChange();
        assertEquals("No balance to return.", view.lastMessage());
        assertEquals(MessageType.INFO, view.lastType());
    }

    /** Exiting returns any remaining balance and says goodbye. */
    @Test
    void exitReturnsBalance() {
        controller.onInsertCoin(5);
        controller.onExit();
        assertEquals(0, model.getBalance());
        assertEquals("Goodbye!", view.lastMessage());
        assertTrue(view.messages.contains("Change returned: 5 kr"));
    }
}
