package dev.elpu7.piechart.event;

import dev.elpu7.piechart.client.PiechartEditScreen;
import dev.elpu7.piechart.client.PiechartState;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keymapping.v1.KeyMappingHelper;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.Identifier;
import com.mojang.blaze3d.platform.InputConstants;
import org.lwjgl.glfw.GLFW;

public class KeyInputHandler {
    private static final KeyMapping.Category KEY_CATEGORY = KeyMapping.Category.register(Identifier.fromNamespaceAndPath("piechart", "piechart"));
    private static final String TOGGLE_DEBUG_PROFILER_KEY = "key.piechart.toggle_debug_profiler";
    private static final String OPEN_EDIT_MODE_KEY = "key.piechart.open_editor";

    private static KeyMapping toggleDebugProfilerKey;
    private static KeyMapping openEditModeKey;

    public static void register() {
        toggleDebugProfilerKey = KeyMappingHelper.registerKeyMapping(new KeyMapping(
                TOGGLE_DEBUG_PROFILER_KEY,
                InputConstants.Type.KEYSYM,
                GLFW.GLFW_KEY_F8,
                KEY_CATEGORY
        ));
        openEditModeKey = KeyMappingHelper.registerKeyMapping(new KeyMapping(
                OPEN_EDIT_MODE_KEY,
                InputConstants.Type.KEYSYM,
                GLFW.GLFW_KEY_F7,
                KEY_CATEGORY
        ));

        ClientTickEvents.END_CLIENT_TICK.register(KeyInputHandler::onEndTick);
    }

    private static void onEndTick(Minecraft client) {
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
            client.setScreen(new PiechartEditScreen(client.screen));
        }
    }
}
