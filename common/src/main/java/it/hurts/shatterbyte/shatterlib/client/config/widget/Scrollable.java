package it.hurts.shatterbyte.shatterlib.client.config.widget;

public interface Scrollable {
    double getScrollOffset();
    void setScrollOffset(double offset);
    default void clamp(double min, double max) {
        this.setScrollOffset(Math.clamp(this.getScrollOffset(), min, max));
    }

    default void scroll(double amount) {
        this.setScrollOffset(this.getScrollOffset() + amount);
    }
}
