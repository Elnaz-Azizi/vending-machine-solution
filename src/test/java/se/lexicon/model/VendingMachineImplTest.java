package se.lexicon.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import se.lexicon.exception.VendingMachineException;

import static org.junit.jupiter.api.Assertions.*;

/** Tests the business rules of VendingMachineImpl directly (the console UI is not tested). */
class VendingMachineImplTest {

    private VendingMachineImpl machine;
    private Product cola;

    /** Builds a fresh machine before every test: Chips (id 1), Cola (id 2, 3 in stock), Apple (id 3, out of stock). */
    @BeforeEach
    void setUp() {
        machine = new VendingMachineImpl();
        cola = new Beverage(2, "Cola", 20, 3, 330);
        machine.addProduct(new Snack(1, "Chips", 15, 5, 130));
        machine.addProduct(cola);
        machine.addProduct(new Fruit(3, "Apple", 10, 0, "Sweden"));
    }

    /** Test case 1: a valid coin is added to the balance. */
    @Test
    void insertValidCoin() {
        assertTrue(machine.insertCoin(10));
        assertEquals(10, machine.getBalance());
    }

    /** Test case 2: an invalid coin is rejected and the balance stays 0. */
    @Test
    void rejectInvalidCoin() {
        assertFalse(machine.insertCoin(7));
        assertEquals(0, machine.getBalance());
    }

    /** Test case 3: a purchase returns the product, empties the balance and reduces stock by 1. */
    @Test
    void purchaseSucceeds() {
        machine.insertCoin(20);
        Product bought = machine.purchaseProduct(2);
        assertSame(cola, bought);
        assertEquals(0, machine.getBalance());
        assertEquals(2, cola.getQuantity());
    }

    /** Overpaying returns the difference automatically as change. */
    @Test
    void purchaseReturnsChangeAutomatically() {
        machine.insertCoin(20);
        machine.purchaseProduct(1);
        assertEquals(5, machine.getLastChange());
        assertEquals(0, machine.getBalance());
    }

    /** Test case 4: too little money fails; balance and stock are unchanged. */
    @Test
    void purchaseFailsInsufficientBalance() {
        machine.insertCoin(10);
        assertThrows(VendingMachineException.class, () -> machine.purchaseProduct(2));
        assertEquals(10, machine.getBalance());
        assertEquals(3, cola.getQuantity());
    }

    /** Test case 5: an out-of-stock product fails; balance and stock are unchanged. */
    @Test
    void purchaseFailsOutOfStock() {
        machine.insertCoin(10);
        machine.insertCoin(10);
        assertThrows(VendingMachineException.class, () -> machine.purchaseProduct(3));
        assertEquals(20, machine.getBalance());
        assertEquals(0, machine.getProducts().get(2).getQuantity());
    }

    /** An unknown product id fails and the balance is unchanged. */
    @Test
    void purchaseFailsUnknownProduct() {
        machine.insertCoin(50);
        assertThrows(VendingMachineException.class, () -> machine.purchaseProduct(99));
        assertEquals(50, machine.getBalance());
    }

    /** Test case 6: returnChange gives back the balance and resets it to 0 (a second call returns 0). */
    @Test
    void returnChange() {
        machine.insertCoin(50);
        assertEquals(50, machine.returnChange());
        assertEquals(0, machine.getBalance());
        assertEquals(0, machine.returnChange());
    }

    /** Test case 7: the machine lists all 3 products added. */
    @Test
    void productList() {
        assertEquals(3, machine.getProducts().size());
    }

    /** Each product describes itself as name (type, detail). */
    @Test
    void productsDescribeThemselves() {
        assertEquals("Chips (Snack, 130g)", machine.getProducts().get(0).describe());
        assertEquals("Cola (Beverage, 330ml)", machine.getProducts().get(1).describe());
        assertEquals("Apple (Fruit, Sweden)", machine.getProducts().get(2).describe());
    }

    /** Two products may not share an id, and null products are rejected. */
    @Test
    void duplicateIdAndNullProductRejected() {
        assertThrows(IllegalArgumentException.class, () -> machine.addProduct(new Snack(1, "Other", 5, 1, 50)));
        assertThrows(IllegalArgumentException.class, () -> machine.addProduct(null));
        assertEquals(3, machine.getProducts().size());
    }

    /** Invalid constructor arguments are rejected (encapsulation / validation). */
    @Test
    void invalidProductRejected() {
        assertThrows(IllegalArgumentException.class, () -> new Snack(9, "", 10, 1, 100));
        assertThrows(IllegalArgumentException.class, () -> new Snack(9, "X", -1, 1, 100));
        assertThrows(IllegalArgumentException.class, () -> new Fruit(9, "X", 10, -1, "SE"));
    }
}
