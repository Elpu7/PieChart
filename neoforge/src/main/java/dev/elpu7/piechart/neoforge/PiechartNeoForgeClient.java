package dev.elpu7.piechart.neoforge;

import dev.elpu7.piechart.client.PiechartClient;
import dev.elpu7.piechart.client.PiechartController;
import dev.elpu7.piechart.client.PiechartEditScreen;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.loading.FMLPaths;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;
import net.neoforged.neoforge.common.NeoForge;

@Mod(value = PiechartNeoForgeClient.MOD_ID, dist = Dist.CLIENT)
public final class PiechartNeoForgeClient {
    public static final String MOD_ID = "piechart";

    public PiechartNeoForgeClient(IEventBus modBus, ModContainer container) {
        PiechartClient.initialize(FMLPaths.CONFIGDIR.get());

        modBus.addListener(this::registerKeyMappings);
        NeoForge.EVENT_BUS.addListener(this::onEndTick);
        container.registerExtensionPoint(
            IConfigScreenFactory.class,
            (ignored, parent) -> new PiechartEditScreen(parent)
        );
    }

    private void registerKeyMappings(RegisterKeyMappingsEvent event) {
        KeyMapping.Category category = new KeyMapping.Category(PiechartController.getKeyCategoryId());
        event.registerCategory(category);
        event.register(PiechartController.createToggleKey(category));
        event.register(PiechartController.createEditorKey(category));
    }

    private void onEndTick(ClientTickEvent.Post event) {
        PiechartController.onEndTick(Minecraft.getInstance());
    }
}
