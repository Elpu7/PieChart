package dev.elpu7.piechart.client;

import com.mojang.blaze3d.platform.InputConstants;
import dev.elpu7.elib.client.gui.DoubleSliderWidget;
import dev.elpu7.elib.client.ElibConfigNotifications;
import java.util.Locale;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.network.chat.Component;

public final class PiechartEditScreen extends Screen {
    private static final int BUTTON_WIDTH = 150;
    private static final int BUTTON_GAP = 8;
    private static final int HEADER_HEIGHT = 33;
    private static final int FOOTER_HEIGHT = 33;
    private static final int PANEL_X = 18;
    private static final int PANEL_PADDING = 12;
    private static final int PANEL_VERTICAL_MARGIN = 6;
    private static final int PANEL_WIDTH = BUTTON_WIDTH + PANEL_PADDING * 2;
    private static final int PREVIEW_SIDE_PADDING = 30;
    private static final int PREVIEW_HEADER_HEIGHT = 22;
    private static final int SLIDER_Y = 63;
    private static final int BORDER_COLOR = 0x77FFFFFF;

    private final Screen parent;
    private final PiechartConfig config;
    private DoubleSliderWidget scaleSlider;

    private boolean draggingChart;
    private double dragLastX;
    private double dragLastY;

    public PiechartEditScreen(Screen parent) {
        super(Component.translatable("screen.piechart.edit_mode"));
        this.parent = parent;
        this.config = PiechartConfigManager.getConfig();
    }

    @Override
    protected void init() {
        keepChartOnScreen();
        int footerX = footerLeftX();

        scaleSlider = addRenderableWidget(new DoubleSliderWidget(
            PANEL_X + PANEL_PADDING,
            sliderY(),
            BUTTON_WIDTH,
            20,
            PiechartConfig.MIN_SCALE,
            Math.max(PiechartConfig.MIN_SCALE + 0.01D,
                PiechartRenderer.maxScaleForViewport(this.width, this.height)),
            0.01D,
            config.getScale(),
            value -> Component.translatable("screen.piechart.scale_slider", formatScale(value)),
            null,
            this::scaleAroundChartCenter
        ));
        int buttonY = footerButtonY();

        addRenderableWidget(Button.builder(Component.translatable("screen.piechart.reset"), button -> {
            config.reset();
            keepChartOnScreen();
            scaleSlider.syncFromValue(config.getScale());
            ElibConfigNotifications.reset();
        }).bounds(footerX, buttonY, BUTTON_WIDTH, 20).build());

        addRenderableWidget(Button.builder(Component.translatable("screen.piechart.done"), button -> onClose())
            .bounds(footerX + BUTTON_WIDTH + BUTTON_GAP, buttonY, BUTTON_WIDTH, 20)
            .build());
    }

    @Override
    public void extractRenderState(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTick) {
        renderPreviewSurface(graphics);
        PiechartRenderer.renderConfiguredPieChart(Minecraft.getInstance(), graphics);
        renderPreviewHeader(graphics);
        renderControlsText(graphics);
        super.extractRenderState(graphics, mouseX, mouseY, partialTick);
    }

    private void renderControlsText(GuiGraphicsExtractor graphics) {
        int x = PANEL_X + PANEL_PADDING;
        Component instructions = Component.translatable("screen.piechart.instructions");
        int instructionsY = sliderY() + 32;
        int readoutY = instructionsY + this.font.split(instructions, BUTTON_WIDTH).size() * 9 + 12;
        int panelTop = HEADER_HEIGHT + PANEL_VERTICAL_MARGIN;
        int panelBottom = this.height - FOOTER_HEIGHT - PANEL_VERTICAL_MARGIN;

        graphics.fill(PANEL_X, panelTop, PANEL_X + PANEL_WIDTH, panelBottom, 0x88000000);
        graphics.outline(PANEL_X, panelTop, PANEL_WIDTH, panelBottom - panelTop, BORDER_COLOR);
        graphics.centeredText(this.font, this.title, this.width / 2, 10, 0xFFFFFFFF);
        graphics.textWithWordWrap(this.font, instructions, x, instructionsY, BUTTON_WIDTH, 0xFFE0E0E0);

        graphics.text(this.font, Component.translatable("screen.piechart.offset_x",
            (int)Math.round(config.getOffsetX())), x, readoutY, 0xFFFFFFFF);
        graphics.text(this.font, Component.translatable("screen.piechart.offset_y",
            (int)Math.round(config.getOffsetY())), x, readoutY + 16, 0xFFFFFFFF);
    }

    private void renderPreviewSurface(GuiGraphicsExtractor graphics) {
        PreviewBounds bounds = previewBounds();
        int x = bounds.x();
        int y = bounds.y();
        int width = bounds.right() - x;
        int height = bounds.bottom() - y;

        graphics.fill(x, y, x + width, y + height, 0x88000000);

        int centerX = x + width / 2;
        int centerY = y + height / 2;
        graphics.verticalLine(centerX, y + PREVIEW_HEADER_HEIGHT + 14,
            y + height - 14, 0x44FFFFFF);
        graphics.horizontalLine(x + 14, x + width - 14, centerY, 0x44FFFFFF);
    }

    private void renderPreviewHeader(GuiGraphicsExtractor graphics) {
        PreviewBounds bounds = previewBounds();
        graphics.outline(bounds.x(), bounds.y(),
            bounds.right() - bounds.x(), bounds.bottom() - bounds.y(),
            draggingChart ? 0xFFFFFFFF : BORDER_COLOR);
        graphics.text(
            this.font,
            Component.translatable("screen.piechart.preview"),
            bounds.x() + 10,
            bounds.y() + 7,
            0xFFFFFFFF
        );
    }

    private int footerLeftX() {
        return (this.width - BUTTON_WIDTH * 2 - BUTTON_GAP) / 2;
    }

    private int sliderY() {
        return Math.min(SLIDER_Y, Math.max(38, this.height - 160));
    }

    private int footerButtonY() {
        return this.height - (FOOTER_HEIGHT + 20) / 2;
    }

    @Override
    public boolean mouseClicked(MouseButtonEvent event, boolean doubleClick) {
        if (super.mouseClicked(event, doubleClick)) {
            return true;
        }

        if (event.button() == InputConstants.MOUSE_BUTTON_LEFT
            && isInsidePreviewSurface(event.x(), event.y())) {
            draggingChart = true;
            dragLastX = event.x();
            dragLastY = event.y();
            return true;
        }

        return false;
    }

    @Override
    public boolean mouseDragged(MouseButtonEvent event, double deltaX, double deltaY) {
        if (draggingChart) {
            config.setOffsetX(config.getOffsetX() + event.x() - dragLastX);
            config.setOffsetY(config.getOffsetY() + event.y() - dragLastY);
            keepChartOnScreen();
            dragLastX = event.x();
            dragLastY = event.y();
            return true;
        }

        return super.mouseDragged(event, deltaX, deltaY);
    }

    @Override
    public boolean mouseReleased(MouseButtonEvent event) {
        draggingChart = false;
        return super.mouseReleased(event);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double horizontalAmount, double verticalAmount) {
        if (isInsidePreviewSurface(mouseX, mouseY) && verticalAmount != 0.0D) {
            double newScale = Math.round((currentPlacement().scale() + verticalAmount * 0.05D) * 100.0D) / 100.0D;
            scaleAround(newScale, mouseX, mouseY);
            scaleSlider.syncFromValue(config.getScale());
            return true;
        }

        return super.mouseScrolled(mouseX, mouseY, horizontalAmount, verticalAmount);
    }

    private boolean isInsidePreviewSurface(double mouseX, double mouseY) {
        PreviewBounds bounds = previewBounds();
        return mouseX >= bounds.x() && mouseX < bounds.right()
            && mouseY >= bounds.y() && mouseY < bounds.bottom();
    }

    private PreviewBounds previewBounds() {
        PiechartRenderer.Placement placement = currentPlacement();
        int previewX = previewX(placement);
        int previewY = previewY(placement);
        int previewWidth = previewWidth(placement);
        int previewHeight = previewHeight(placement);

        return new PreviewBounds(
            Math.max(0, previewX - PREVIEW_SIDE_PADDING),
            Math.max(0, previewY - previewTopPadding(placement)),
            Math.min(this.width, previewX + previewWidth + PREVIEW_SIDE_PADDING),
            Math.min(this.height, previewY + previewHeight + PREVIEW_SIDE_PADDING)
        );
    }

    private static int previewTopPadding(PiechartRenderer.Placement placement) {
        return PREVIEW_HEADER_HEIGHT + 24 + (int)Math.ceil(40.0D * placement.scale());
    }

    private void scaleAroundChartCenter(double scale) {
        PiechartRenderer.Placement placement = currentPlacement();
        scaleAround(scale,
            placement.x() + PiechartRenderer.BASE_WIDTH * placement.scale() / 2.0D,
            placement.y() + PiechartRenderer.BASE_HEIGHT * placement.scale() / 2.0D);
    }

    private void scaleAround(double requestedScale, double anchorX, double anchorY) {
        PiechartRenderer.Placement before = currentPlacement();
        double maxScale = Math.max(PiechartConfig.MIN_SCALE,
            PiechartRenderer.maxScaleForViewport(this.width, this.height));
        double scale = Math.clamp(requestedScale, PiechartConfig.MIN_SCALE, maxScale);
        if (scale == before.scale()) {
            return;
        }

        double chartX = (anchorX - before.x()) / before.scale();
        double chartY = (anchorY - before.y()) / before.scale();
        config.setScale(scale);
        config.setOffsetX(anchorX - chartX * scale
            - (this.width - PiechartRenderer.BASE_WIDTH - PiechartRenderer.RIGHT_MARGIN));
        config.setOffsetY(anchorY - chartY * scale - (this.height - PiechartRenderer.BASE_HEIGHT));
        keepChartOnScreen();
    }

    private void keepChartOnScreen() {
        PiechartRenderer.Placement placement = currentPlacement();
        config.setScale(placement.scale());
        config.setOffsetX(placement.x() - (this.width - PiechartRenderer.BASE_WIDTH - PiechartRenderer.RIGHT_MARGIN));
        config.setOffsetY(placement.y() - (this.height - PiechartRenderer.BASE_HEIGHT));
    }

    private PiechartRenderer.Placement currentPlacement() {
        return PiechartRenderer.placement(config, this.width, this.height);
    }

    private int previewX(PiechartRenderer.Placement placement) {
        return Math.clamp((int)Math.round(placement.x()),
            0, Math.max(0, this.width - previewWidth(placement)));
    }

    private int previewY(PiechartRenderer.Placement placement) {
        return Math.clamp((int)Math.round(placement.y()),
            0, Math.max(0, this.height - previewHeight(placement)));
    }

    private static int previewWidth(PiechartRenderer.Placement placement) {
        return (int)Math.ceil(PiechartRenderer.BASE_WIDTH * placement.scale());
    }

    private static int previewHeight(PiechartRenderer.Placement placement) {
        return (int)Math.ceil(PiechartRenderer.BASE_HEIGHT * placement.scale());
    }

    private record PreviewBounds(int x, int y, int right, int bottom) {
    }

    @Override
    public void onClose() {
        Minecraft.getInstance().setScreenAndShow(parent);
    }

    @Override
    public void removed() {
        if (!PiechartConfigManager.save()) {
            Minecraft client = Minecraft.getInstance();
            if (client.player != null) {
                client.player.sendOverlayMessage(Component.translatable("message.piechart.config_save_failed"));
            }
        }
        super.removed();
    }

    private static String formatScale(double scale) {
        return String.format(Locale.ROOT, "%.2fx", scale);
    }
}
