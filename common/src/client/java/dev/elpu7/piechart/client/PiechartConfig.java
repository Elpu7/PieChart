package dev.elpu7.piechart.client;

public final class PiechartConfig {
    public static final double MIN_SCALE = 0.35D;
    public static final double MAX_SCALE = 3.0D;
    public static final double MIN_OFFSET = -800.0D;
    public static final double MAX_OFFSET = 800.0D;
    public static final double DEFAULT_SCALE = 1.0D;

    private double offsetX;
    private double offsetY;
    private double scale = DEFAULT_SCALE;

    public double getOffsetX() {
        return offsetX;
    }

    public void setOffsetX(double offsetX) {
        this.offsetX = clamp(offsetX, MIN_OFFSET, MAX_OFFSET, 0.0D);
    }

    public double getOffsetY() {
        return offsetY;
    }

    public void setOffsetY(double offsetY) {
        this.offsetY = clamp(offsetY, MIN_OFFSET, MAX_OFFSET, 0.0D);
    }

    public double getScale() {
        return scale;
    }

    public void setScale(double scale) {
        this.scale = clamp(scale, MIN_SCALE, MAX_SCALE, DEFAULT_SCALE);
    }

    public void reset() {
        offsetX = 0.0D;
        offsetY = 0.0D;
        scale = DEFAULT_SCALE;
    }

    PiechartConfig sanitize() {
        setOffsetX(offsetX);
        setOffsetY(offsetY);
        setScale(scale);
        return this;
    }

    private static double clamp(double value, double min, double max, double fallback) {
        if (Double.isNaN(value) || Double.isInfinite(value)) {
            return fallback;
        }

        return Math.clamp(value, min, max);
    }
}
