package dev.elpu7.piechart.client;

import dev.elpu7.elib.config.JsonConfigStore;
import java.nio.file.Path;
import java.util.Objects;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class PiechartConfigManager {
    private static final Logger LOGGER = LoggerFactory.getLogger("piechart");

    private static JsonConfigStore<PiechartConfig> store;

    private PiechartConfigManager() {
    }

    public static void initialize(Path configDirectory) {
        Path configPath = Objects.requireNonNull(configDirectory, "configDirectory").resolve("piechart.json");
        store = new JsonConfigStore<>(
            configPath,
            PiechartConfig.class,
            PiechartConfig::new,
            PiechartConfig::sanitize,
            LOGGER
        );
        store.load();
    }

    public static PiechartConfig getConfig() {
        return getStore().get();
    }

    public static boolean save() {
        return getStore().save();
    }

    private static JsonConfigStore<PiechartConfig> getStore() {
        return Objects.requireNonNull(store, "Piechart config manager has not been initialized");
    }
}
