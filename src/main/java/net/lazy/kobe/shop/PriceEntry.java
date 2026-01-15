package net.lazy.kobe.shop;

public record PriceEntry(double buy, double sell) {

    public static final PriceEntry NONE = new PriceEntry(-1, -1);

    public boolean canBuy() {
        return buy >= 0;
    }

    public boolean canSell() {
        return sell >= 0;
    }
}