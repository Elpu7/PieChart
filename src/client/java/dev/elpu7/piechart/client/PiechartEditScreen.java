package dev.elpu7.piechart.client;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.Click;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.SliderWidget;
import net.minecraft.text.Text;

public class PiechartEditScreen extends Screen {
    private static final int PANEL_WIDTH = 180;
    private static final int PREVIEW_PADDING = 8;

    private final Screen parent;
    private final PiechartConfig config;
    private ScaleSliderWidget scaleSlider;

    private boolean draggingChart;
    private double dragLastX;
    private double dragLastY;

    public PiechartEditScreen(Screen parent) {
        super(Text.translatable("screen.piechart.edit_mode"));
        this.parent = parent;
        this.config = PiechartConfig.getInstance();
    }

    @Override
    protected void init() {
        int panelX = 16;
        int buttonWidth = PANEL_WIDTH - PREVIEW_PADDING * 2;
        int y = 40;

        scaleSlider = addDrawableChild(new ScaleSliderWidget(panelX + PREVIEW_PADDING, y, buttonWidth, 20, config));
        y += 28;

        addDrawableChild(ButtonWidget.builder(Text.translatable("screen.piechart.reset"), button -> {
            config.reset();
            scaleSlider.syncFromConfig();
        }).dimensions(panelX + PREVIEW_PADDING, y, buttonWidth, 20).build());
        y += 28;

        addDrawableChild(ButtonWidget.builder(Text.translatable("screen.piechart.done"), button -> close())
                .dimensions(panelX + PREVIEW_PADDING, y, buttonWidth, 20)
                .build());
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float deltaTicks) {
        context.fill(0, 0, this.width, this.height, 0x88070B12);

        context.fill(12, 12, 12 + PANEL_WIDTH, this.height - 12, 0xAA101218);
        context.drawTextWithShadow(this.textRenderer, this.title, 24, 20, 0xFFFFFF);
        context.drawWrappedTextWithShadow(
                this.textRenderer,
                Text.translatable("screen.piechart.instructions"),
                24,
                100,
                PANEL_WIDTH - 24,
                0xB8C4D4
        );

        super.render(context, mouseX, mouseY, deltaTicks);

        PiechartRenderer.renderConfiguredPieChart(MinecraftClient.getInstance(), context);
        renderPreviewOutline(context);
    }

    private void renderPreviewOutline(DrawContext context) {
        PiechartConfig currentConfig = PiechartConfig.getInstance();
        int baseX = context.getScaledWindowWidth() - 220;
        int baseY = context.getScaledWindowHeight() - 170;
        int previewX = baseX + (int) Math.round(currentConfig.getOffsetX());
        int previewY = baseY + (int) Math.round(currentConfig.getOffsetY());
        int previewWidth = (int) Math.round(PiechartRenderer.BASE_WIDTH * currentConfig.getScale());
        int previewHeight = (int) Math.round(PiechartRenderer.BASE_HEIGHT * currentConfig.getScale());

        context.drawStrokedRectangle(previewX - 2, previewY - 2, previewWidth + 4, previewHeight + 4, 0xFFE8D8A8);
    }

    @Override
    public boolean mouseClicked(Click click, boolean doubleClick) {
        if (super.mouseClicked(click, doubleClick)) {
            return true;
        }

        if (isInsidePreview(click.x(), click.y())) {
            draggingChart = true;
            dragLastX = click.x();
            dragLastY = click.y();
            return true;
        }

        return false;
    }

    @Override
    public boolean mouseDragged(Click click, double deltaX, double deltaY) {
        if (draggingChart) {
            config.setOffsetX(config.getOffsetX() + click.x() - dragLastX);
            config.setOffsetY(config.getOffsetY() + click.y() - dragLastY);
            dragLastX = click.x();
            dragLastY = click.y();
            return true;
        }

        return super.mouseDragged(click, deltaX, deltaY);
    }

    @Override
    public boolean mouseReleased(Click click) {
        draggingChart = false;
        return super.mouseReleased(click);
    }

        @Override
        public boolean mouseScrolled(double mouseX, double mouseY, double horizontalAmount, double verticalAmount) {
        if (isInsidePreview(mouseX, mouseY)) {
            config.setScale(config.getScale() + verticalAmount * 0.1);
            scaleSlider.syncFromConfig();
            return true;
        }

        return super.mouseScrolled(mouseX, mouseY, horizontalAmount, verticalAmount);
    }

    private boolean isInsidePreview(double mouseX, double mouseY) {
        int baseX = this.width - 220;
        int baseY = this.height - 170;
        int previewX = baseX + (int) Math.round(config.getOffsetX());
        int previewY = baseY + (int) Math.round(config.getOffsetY());
        int previewWidth = (int) Math.round(PiechartRenderer.BASE_WIDTH * config.getScale());
        int previewHeight = (int) Math.round(PiechartRenderer.BASE_HEIGHT * config.getScale());

        return mouseX >= previewX && mouseX <= previewX + previewWidth
                && mouseY >= previewY && mouseY <= previewY + previewHeight;
    }

    @Override
    public void close() {
        config.save();
        MinecraftClient.getInstance().setScreen(parent);
    }

    private static final class ScaleSliderWidget extends SliderWidget {
        private final PiechartConfig config;

        private ScaleSliderWidget(int x, int y, int width, int height, PiechartConfig config) {
            super(x, y, width, height, Text.empty(), toSliderValue(config.getScale()));
            this.config = config;
            updateMessage();
        }

        private static double toSliderValue(double scale) {
            return (scale - PiechartConfig.MIN_SCALE) / (PiechartConfig.MAX_SCALE - PiechartConfig.MIN_SCALE);
        }

        private static double fromSliderValue(double value) {
            return PiechartConfig.MIN_SCALE + value * (PiechartConfig.MAX_SCALE - PiechartConfig.MIN_SCALE);
        }

        private void syncFromConfig() {
            this.value = toSliderValue(config.getScale());
            updateMessage();
        }

        @Override
        protected void updateMessage() {
            setMessage(Text.translatable("screen.piechart.scale", String.format("%.2fx", config.getScale())));
        }

        @Override
        protected void applyValue() {
            config.setScale(fromSliderValue(this.value));
            updateMessage();
        }
    }
}
