package se.lexicon.model;

/** A fruit; its specific detail is its country of origin. */
public class Fruit extends Product {

    private final String origin;

    /**
     * @param id       unique id
     * @param name     display name
     * @param price    price in kr
     * @param quantity stock count
     * @param origin   country of origin, must not be blank
     * @throws IllegalArgumentException if any value is invalid
     */
    public Fruit(int id, String name, int price, int quantity, String origin) {
        super(id, name, price, quantity);
        if (origin == null || origin.isBlank()) throw new IllegalArgumentException("Origin must not be empty");
        this.origin = origin;
    }

    /** @return the country of origin */
    public String getOrigin() {
        return origin;
    }

    /** @return "Fruit" */
    @Override
    public String getType() {
        return "Fruit";
    }

    /** @return the origin, e.g. "Sweden" */
    @Override
    public String getDetail() {
        return origin;
    }
}
