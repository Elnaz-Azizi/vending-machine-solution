package se.lexicon.model;

/** A snack; its specific detail is its weight in grams. */
public class Snack extends Product {

    private final int weightGrams;

    /**
     * @param id          unique id
     * @param name        display name
     * @param price       price in kr
     * @param quantity    stock count
     * @param weightGrams weight in grams, must be positive
     * @throws IllegalArgumentException if any value is invalid
     */
    public Snack(int id, String name, int price, int quantity, int weightGrams) {
        super(id, name, price, quantity);
        if (weightGrams <= 0) throw new IllegalArgumentException("Weight must be positive");
        this.weightGrams = weightGrams;
    }

    /** @return the weight in grams */
    public int getWeightGrams() {
        return weightGrams;
    }

    /** @return "Snack" */
    @Override
    public String getType() {
        return "Snack";
    }

    /** @return the weight, e.g. "130g" */
    @Override
    public String getDetail() {
        return weightGrams + "g";
    }
}
