package dev.elpu7.piechart.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;

public final class PiechartRenderer {
    public static final int BASE_WIDTH = 260;
    public static final int BASE_HEIGHT = 170;
    public static final int RIGHT_MARGIN = 10;

    private PiechartRenderer() {
    }

    public static void renderConfiguredPieChart(Minecraft client, GuiGraphicsExtractor graphics) {
        PiechartConfig config = PiechartConfigManager.getConfig();
        Placement placement = placement(config, graphics.guiWidth(), graphics.guiHeight());
        float scale = (float)placement.scale();
        float baseX = graphics.guiWidth() - BASE_WIDTH - RIGHT_MARGIN;
        float baseY = graphics.guiHeight() - BASE_HEIGHT;

        graphics.pose().pushMatrix();
        try {
            graphics.pose().translate((float)placement.x(), (float)placement.y());
            graphics.pose().scale(scale, scale);
            graphics.pose().translate(-baseX, -baseY);
            client.getDebugOverlay().getProfilerPieChart().extractRenderState(
                graphics,
                graphics.guiWidth(),
                graphics.guiHeight()
            );
        } finally {
            graphics.pose().popMatrix();
        }
    }

    static double maxScaleForViewport(int width, int height) {
        return Math.min(PiechartConfig.MAX_SCALE,
            Math.min(width / (double)BASE_WIDTH, height / (double)BASE_HEIGHT));
    }

    static Placement placement(PiechartConfig config, int width, int height) {
        double scale = Math.min(config.getScale(), Math.max(0.01D, maxScaleForViewport(width, height)));
        double x = Math.clamp(width - BASE_WIDTH - RIGHT_MARGIN + config.getOffsetX(),
            0.0D, Math.max(0.0D, width - BASE_WIDTH * scale));
        double y = Math.clamp(height - BASE_HEIGHT + config.getOffsetY(),
            0.0D, Math.max(0.0D, height - BASE_HEIGHT * scale));
        return new Placement(scale, x, y);
    }

    record Placement(double scale, double x, double y) {
    }
}
