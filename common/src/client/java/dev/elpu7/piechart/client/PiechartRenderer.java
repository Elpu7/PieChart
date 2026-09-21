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
        float scale = (float)config.getScale();
        float baseX = graphics.guiWidth() - BASE_WIDTH - RIGHT_MARGIN;
        float baseY = graphics.guiHeight() - BASE_HEIGHT;
        float translatedX = baseX + (float)config.getOffsetX();
        float translatedY = baseY + (float)config.getOffsetY();

        graphics.pose().pushMatrix();
        try {
            graphics.pose().translate(translatedX, translatedY);
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
}
