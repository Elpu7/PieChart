package dev.elpu7.piechart.client;

import java.nio.file.Path;

public final class PiechartClient {
    private PiechartClient() {
    }

    public static void initialize(Path configDirectory) {
        PiechartConfigManager.initialize(configDirectory);
    }
}
