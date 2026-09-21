package dev.elpu7.piechart.fabric;

import dev.elpu7.piechart.client.PiechartClient;
import dev.elpu7.piechart.client.PiechartController;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keymapping.v1.KeyMappingHelper;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.KeyMapping;

public final class PiechartFabricClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        PiechartClient.initialize(FabricLoader.getInstance().getConfigDir());

        KeyMapping.Category category = KeyMapping.Category.register(PiechartController.getKeyCategoryId());
        KeyMappingHelper.registerKeyMapping(PiechartController.createToggleKey(category));
        KeyMappingHelper.registerKeyMapping(PiechartController.createEditorKey(category));
        ClientTickEvents.END_CLIENT_TICK.register(PiechartController::onEndTick);
    }
}
