package dev.elpu7.piechart.client;

import dev.elpu7.elib.client.ElibConfigRegistry;
import java.nio.file.Path;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;

public final class PiechartClient {
    private PiechartClient() {
    }

    public static void initialize(Path configDirectory, String version) {
        PiechartConfigManager.initialize(configDirectory);
        ElibConfigRegistry.register("piechart", Component.literal("PieChart"), version,
            Identifier.fromNamespaceAndPath("piechart", "icon.png"), PiechartEditScreen::new);
    }
}
