package dev.elpu7.piechart.client;

import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.Identifier;

public final class PiechartController {
    private static final Identifier KEY_CATEGORY_ID = Identifier.fromNamespaceAndPath("piechart", "piechart");

    private static KeyMapping toggleDebugProfilerKey;
    private static KeyMapping openEditModeKey;

    private PiechartController() {
    }

    public static Identifier getKeyCategoryId() {
        return KEY_CATEGORY_ID;
    }

    public static KeyMapping createToggleKey(KeyMapping.Category category) {
        if (toggleDebugProfilerKey != null) {
            throw new IllegalStateException("Piechart toggle key mapping has already been created");
        }

        toggleDebugProfilerKey = new KeyMapping(
            "key.piechart.toggle_debug_profiler",
            InputConstants.Type.KEYBOARD,
            InputConstants.KEY_F8,
            category
        );
        return toggleDebugProfilerKey;
    }

    public static KeyMapping createEditorKey(KeyMapping.Category category) {
        if (openEditModeKey != null) {
            throw new IllegalStateException("Piechart editor key mapping has already been created");
        }

        openEditModeKey = new KeyMapping(
            "key.piechart.open_editor",
            InputConstants.Type.KEYBOARD,
            InputConstants.KEY_F7,
            category
        );
        return openEditModeKey;
    }

    public static void onEndTick(Minecraft client) {
        if (toggleDebugProfilerKey == null || openEditModeKey == null) {
            return;
        }

        if (client.level == null || client.player == null) {
            PiechartState.hide();
            client.getDebugOverlay().getProfilerPieChart().setPieChartResults(null);
            return;
        }

        while (toggleDebugProfilerKey.consumeClick()) {
            PiechartState.toggleModKeyPieChart();

            if (!PiechartState.isModKeyPieChartVisible()) {
                client.getDebugOverlay().getProfilerPieChart().setPieChartResults(null);
            }
        }

        while (openEditModeKey.consumeClick()) {
            client.setScreenAndShow(new PiechartEditScreen(client.gui.screen()));
        }
    }
}
