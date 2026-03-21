package dev.elpu7.piechart.client;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;

public final class PiechartRenderer {
    public static final int BASE_WIDTH = 220;
    public static final int BASE_HEIGHT = 170;

    private PiechartRenderer() {
    }

    public static void renderConfiguredPieChart(MinecraftClient client, DrawContext context) {
        PiechartConfig config = PiechartConfig.getInstance();
        float scale = (float) config.getScale();
        float baseX = context.getScaledWindowWidth() - 220.0F;
        float baseY = context.getScaledWindowHeight() - 170.0F;
        float translatedX = baseX + (float) config.getOffsetX();
        float translatedY = baseY + (float) config.getOffsetY();

        PiechartRenderContext.begin(baseX, baseY, translatedX, translatedY, scale);
        context.getMatrices().pushMatrix();
        context.getMatrices().translate(translatedX, translatedY);
        context.getMatrices().scale(scale, scale);
        context.getMatrices().translate(-baseX, -baseY);
        client.getDebugHud().getPieChart().render(context);
        context.getMatrices().popMatrix();
        PiechartRenderContext.end();
    }
}
