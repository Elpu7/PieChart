package dev.elpu7.piechart.client;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.Click;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.SliderWidget;
import net.minecraft.text.Text;

public class PiechartEditScreen extends Screen {
    private static final int PANEL_X = 18;
    private static final int PANEL_Y = 18;
    private static final int PANEL_WIDTH = 216;
    private static final int PREVIEW_PADDING = 12;
    private static final int PANEL_BOTTOM_MARGIN = 18;

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
        int contentX = PANEL_X + PREVIEW_PADDING;
        int buttonWidth = PANEL_WIDTH - PREVIEW_PADDING * 2;
        int halfButtonWidth = (buttonWidth - 8) / 2;
        int y = PANEL_Y + 44;

        scaleSlider = addDrawableChild(new ScaleSliderWidget(contentX, y, buttonWidth, 20, config));
        y += 112;

        addDrawableChild(ButtonWidget.builder(Text.translatable("screen.piechart.reset"), button -> {
            config.reset();
            scaleSlider.syncFromConfig();
        }).dimensions(contentX, y, halfButtonWidth, 20).build());

        addDrawableChild(ButtonWidget.builder(Text.translatable("screen.piechart.done"), button -> close())
                .dimensions(contentX + halfButtonWidth + 8, y, halfButtonWidth, 20)
                .build());
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float deltaTicks) {
        renderBackdrop(context);
        renderSidePanel(context);
        renderPreviewSurface(context);
        super.render(context, mouseX, mouseY, deltaTicks);
        PiechartRenderer.renderConfiguredPieChart(MinecraftClient.getInstance(), context);
        renderPreviewOutline(context);
        renderValueReadout(context);
    }

    private void renderBackdrop(DrawContext context) {
        context.fillGradient(0, 0, this.width, this.height, 0xC20A1118, 0xC2141E1D);
    }

    private void renderSidePanel(DrawContext context) {
        int panelBottom = this.height - PANEL_BOTTOM_MARGIN;
        int panelRight = PANEL_X + PANEL_WIDTH;

        context.fill(PANEL_X, PANEL_Y, panelRight, panelBottom, 0xCC11161D);
        context.drawStrokedRectangle(PANEL_X, PANEL_Y, PANEL_WIDTH, panelBottom - PANEL_Y, 0xFFAF9D72);

        context.drawTextWithShadow(this.textRenderer, this.title, PANEL_X + PREVIEW_PADDING, PANEL_Y + 12, 0xFFF5E7BD);
        context.drawTextWithShadow(
                this.textRenderer,
                Text.translatable("screen.piechart.subtitle"),
                PANEL_X + PREVIEW_PADDING,
                PANEL_Y + 26,
                0xFF9FB4C8
        );
        context.drawWrappedTextWithShadow(
                this.textRenderer,
                Text.translatable("screen.piechart.instructions"),
                PANEL_X + PREVIEW_PADDING,
                PANEL_Y + 78,
                PANEL_WIDTH - PREVIEW_PADDING * 2,
                0xFFD6E0EA
        );
    }

    private void renderPreviewSurface(DrawContext context) {
        int x = getPreviewX() - 30;
        int y = getPreviewY() - 30;
        int width = (int) Math.round(PiechartRenderer.BASE_WIDTH * config.getScale()) + 60;
        int height = (int) Math.round(PiechartRenderer.BASE_HEIGHT * config.getScale()) + 60;

        context.fill(x, y, x + width, y + height, 0x442A3640);
        context.drawStrokedRectangle(x, y, width, height, 0x557E95A8);

        int centerX = x + width / 2;
        int centerY = y + height / 2;
        context.drawVerticalLine(centerX, y + 14, y + height - 14, 0x336D8192);
        context.drawHorizontalLine(x + 14, x + width - 14, centerY, 0x336D8192);
        context.drawTextWithShadow(
                this.textRenderer,
                Text.translatable("screen.piechart.preview"),
                x + 10,
                y + 10,
                0xFFD8E5F0
        );
    }

    private void renderPreviewOutline(DrawContext context) {
        int previewX = getPreviewX();
        int previewY = getPreviewY();
        int previewWidth = getPreviewWidth();
        int previewHeight = getPreviewHeight();
        int outlineColor = draggingChart ? 0xFFFFE39A : 0xFFE8D8A8;

        context.drawStrokedRectangle(previewX - 2, previewY - 2, previewWidth + 4, previewHeight + 4, outlineColor);
    }

    private void renderValueReadout(DrawContext context) {
        int x = PANEL_X + PREVIEW_PADDING;
        int y = PANEL_Y + 124;

        context.drawTextWithShadow(
                this.textRenderer,
                Text.translatable("screen.piechart.scale_value", String.format("%.2fx", config.getScale())),
                x,
                y,
                0xFFF0F4F8
        );
        context.drawTextWithShadow(
                this.textRenderer,
                Text.translatable("screen.piechart.offset_x", (int) Math.round(config.getOffsetX())),
                x,
                y + 16,
                0xFFBBD0E2
        );
        context.drawTextWithShadow(
                this.textRenderer,
                Text.translatable("screen.piechart.offset_y", (int) Math.round(config.getOffsetY())),
                x,
                y + 32,
                0xFFBBD0E2
        );
        context.drawTextWithShadow(
                this.textRenderer,
                Text.translatable("screen.piechart.hint_drag"),
                x,
                y + 56,
                0xFFE3C98F
        );
        context.drawTextWithShadow(
                this.textRenderer,
                Text.translatable("screen.piechart.hint_scroll"),
                x,
                y + 70,
                0xFFE3C98F
        );
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

    private int getPreviewX() {
        int baseX = this.width - 220;
        return baseX + (int) Math.round(config.getOffsetX());
    }

    private int getPreviewY() {
        int baseY = this.height - 170;
        return baseY + (int) Math.round(config.getOffsetY());
    }

    private int getPreviewWidth() {
        return (int) Math.round(PiechartRenderer.BASE_WIDTH * config.getScale());
    }

    private int getPreviewHeight() {
        return (int) Math.round(PiechartRenderer.BASE_HEIGHT * config.getScale());
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
            setMessage(Text.translatable("screen.piechart.scale_slider", String.format("%.2fx", config.getScale())));
        }

        @Override
        protected void applyValue() {
            config.setScale(fromSliderValue(this.value));
            updateMessage();
        }
    }
}
