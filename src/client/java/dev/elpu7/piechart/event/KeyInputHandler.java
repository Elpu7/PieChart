package dev.elpu7.piechart.event;

import dev.elpu7.piechart.client.PiechartState;
import dev.elpu7.piechart.client.PiechartEditScreen;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.util.Identifier;
import org.lwjgl.glfw.GLFW;

public class KeyInputHandler {
    private static final KeyBinding.Category KEY_CATEGORY = KeyBinding.Category.create(Identifier.of("piechart", "piechart"));
    private static final String TOGGLE_DEBUG_PROFILER_KEY = "key.piechart.toggle_debug_profiler";
    private static final String OPEN_EDIT_MODE_KEY = "key.piechart.open_editor";

    private static KeyBinding toggleDebugProfilerKey;
    private static KeyBinding openEditModeKey;

    public static void register() {
        toggleDebugProfilerKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                TOGGLE_DEBUG_PROFILER_KEY,
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_UNKNOWN,
                KEY_CATEGORY
        ));
        openEditModeKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                OPEN_EDIT_MODE_KEY,
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_UNKNOWN,
                KEY_CATEGORY
        ));

        ClientTickEvents.END_CLIENT_TICK.register(KeyInputHandler::onEndTick);
    }

    private static void onEndTick(MinecraftClient client) {
        if (client.world == null || client.player == null) {
            PiechartState.hide();
            client.getDebugHud().getPieChart().setProfileResult(null);
            return;
        }

        while (toggleDebugProfilerKey.wasPressed()) {
            PiechartState.toggleModKeyPieChart();

            if (!PiechartState.isModKeyPieChartVisible()) {
                client.getDebugHud().getPieChart().setProfileResult(null);
            }
        }

        while (openEditModeKey.wasPressed()) {
            client.setScreen(new PiechartEditScreen(client.currentScreen));
        }
    }
}
