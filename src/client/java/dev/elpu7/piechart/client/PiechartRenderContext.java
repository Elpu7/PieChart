package dev.elpu7.piechart.client;

public final class PiechartRenderContext {
    private static boolean active;
    private static float anchorX;
    private static float anchorY;
    private static float translatedX;
    private static float translatedY;
    private static float scale;

    private PiechartRenderContext() {
    }

    public static void begin(float anchorX, float anchorY, float translatedX, float translatedY, float scale) {
        PiechartRenderContext.active = true;
        PiechartRenderContext.anchorX = anchorX;
        PiechartRenderContext.anchorY = anchorY;
        PiechartRenderContext.translatedX = translatedX;
        PiechartRenderContext.translatedY = translatedY;
        PiechartRenderContext.scale = scale;
    }

    public static void end() {
        active = false;
    }

    public static boolean isActive() {
        return active;
    }

    public static float getScale() {
        return scale;
    }

    public static int transformX(int x) {
        return Math.round(translatedX + scale * (x - anchorX));
    }

    public static int transformY(int y) {
        return Math.round(translatedY + scale * (y - anchorY));
    }
}
