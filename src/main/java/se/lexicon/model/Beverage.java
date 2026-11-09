package se.lexicon.model;

/** A beverage; its specific detail is its volume in millilitres. */
public class Beverage extends Product {

    private final int volumeMl;

    /**
     * @param id       unique id
     * @param name     display name
     * @param price    price in kr
     * @param quantity stock count
     * @param volumeMl volume in ml, must be positive
     * @throws IllegalArgumentException if any value is invalid
     */
    public Beverage(int id, String name, int price, int quantity, int volumeMl) {
        super(id, name, price, quantity);
        if (volumeMl <= 0) throw new IllegalArgumentException("Volume must be positive");
        this.volumeMl = volumeMl;
    }

    /** @return the volume in ml */
    public int getVolumeMl() {
        return volumeMl;
    }

    /** @return "Beverage" */
    @Override
    public String getType() {
        return "Beverage";
    }

    /** @return the volume, e.g. "330ml" */
    @Override
    public String getDetail() {
        return volumeMl + "ml";
    }
}
