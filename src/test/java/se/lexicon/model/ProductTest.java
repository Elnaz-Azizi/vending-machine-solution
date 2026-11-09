package se.lexicon.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/** Tests the product hierarchy: self-description, stock handling and validation. */
class ProductTest {

    /** A snack shows its weight in grams. */
    @Test
    void snackDescribesItself() {
        assertEquals("Chips (Snack, 130g)", new Snack(1, "Chips", 15, 5, 130).describe());
    }

    /** A beverage shows its volume in ml. */
    @Test
    void beverageDescribesItself() {
        assertEquals("Cola (Beverage, 330ml)", new Beverage(2, "Cola", 20, 3, 330).describe());
    }

    /** A fruit shows its country of origin. */
    @Test
    void fruitDescribesItself() {
        assertEquals("Apple (Fruit, Sweden)", new Fruit(3, "Apple", 10, 8, "Sweden").describe());
    }

    /** Decreasing stock lowers the quantity by exactly one. */
    @Test
    void decreaseQuantityReducesStockByOne() {
        Product p = new Snack(1, "Chips", 15, 2, 130);
        p.decreaseQuantity();
        assertEquals(1, p.getQuantity());
        assertTrue(p.isInStock());
    }

    /** Decreasing an empty stock throws and keeps the quantity at 0. */
    @Test
    void decreaseQuantityOnEmptyStockThrows() {
        Product p = new Snack(1, "Chips", 15, 0, 130);
        assertFalse(p.isInStock());
        assertThrows(IllegalStateException.class, p::decreaseQuantity);
        assertEquals(0, p.getQuantity());
    }

    /** Every invalid constructor argument is rejected. */
    @Test
    void constructorsValidateInput() {
        assertThrows(IllegalArgumentException.class, () -> new Snack(0, "Chips", 15, 1, 130));
        assertThrows(IllegalArgumentException.class, () -> new Snack(1, null, 15, 1, 130));
        assertThrows(IllegalArgumentException.class, () -> new Snack(1, "Chips", 0, 1, 130));
        assertThrows(IllegalArgumentException.class, () -> new Snack(1, "Chips", 15, -1, 130));
        assertThrows(IllegalArgumentException.class, () -> new Snack(1, "Chips", 15, 1, 0));
        assertThrows(IllegalArgumentException.class, () -> new Beverage(2, "Cola", 20, 1, 0));
        assertThrows(IllegalArgumentException.class, () -> new Fruit(3, "Apple", 10, 1, " "));
    }
}
