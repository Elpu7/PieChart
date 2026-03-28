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
        PiechartConfig config = PiechartConfig.getInstance();
        float scale = (float) config.getScale();
        float baseX = graphics.guiWidth() - BASE_WIDTH - RIGHT_MARGIN;
        float baseY = graphics.guiHeight() - 170.0F;
        float translatedX = baseX + (float) config.getOffsetX();
        float translatedY = baseY + (float) config.getOffsetY();

        PiechartRenderContext.begin(baseX, baseY, translatedX, translatedY, scale);
        graphics.pose().pushMatrix();
        graphics.pose().translate(translatedX, translatedY);
        graphics.pose().scale(scale, scale);
        graphics.pose().translate(-baseX, -baseY);
        client.getDebugOverlay().getProfilerPieChart().extractRenderState(graphics);
        graphics.pose().popMatrix();
        PiechartRenderContext.end();
    }
}
