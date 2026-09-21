package dev.elpu7.piechart.client;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonParseException;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.file.AtomicMoveNotSupportedException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Objects;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class PiechartConfigManager {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final Logger LOGGER = LoggerFactory.getLogger("piechart");

    private static PiechartConfig config = new PiechartConfig();
    private static Path configPath;

    private PiechartConfigManager() {
    }

    public static void initialize(Path configDirectory) {
        configPath = Objects.requireNonNull(configDirectory, "configDirectory").resolve("piechart.json");
        load();
    }

    public static PiechartConfig getConfig() {
        return config;
    }

    public static boolean save() {
        Path path = getConfigPath();
        Path temporaryPath = null;

        try {
            config.sanitize();
            Files.createDirectories(path.getParent());
            temporaryPath = Files.createTempFile(path.getParent(), "piechart-", ".tmp");
            try (Writer writer = Files.newBufferedWriter(temporaryPath)) {
                GSON.toJson(config, writer);
            }

            moveTemporaryConfig(temporaryPath, path);
            temporaryPath = null;
            return true;
        } catch (IOException | RuntimeException exception) {
            LOGGER.error("Failed to save Piechart config", exception);
            return false;
        } finally {
            if (temporaryPath != null) {
                try {
                    Files.deleteIfExists(temporaryPath);
                } catch (IOException exception) {
                    LOGGER.warn("Failed to remove temporary Piechart config {}", temporaryPath, exception);
                }
            }
        }
    }

    private static void load() {
        Path path = getConfigPath();
        if (!Files.exists(path)) {
            config = new PiechartConfig();
            save();
            return;
        }

        try (Reader reader = Files.newBufferedReader(path)) {
            PiechartConfig loadedConfig = GSON.fromJson(reader, PiechartConfig.class);
            config = loadedConfig != null ? loadedConfig.sanitize() : new PiechartConfig();
        } catch (JsonParseException exception) {
            LOGGER.error("Failed to parse Piechart config; using defaults", exception);
            boolean backupCreated = backupBrokenConfig();
            config = new PiechartConfig();
            if (backupCreated) {
                save();
            }
        } catch (IOException exception) {
            LOGGER.error("Failed to read Piechart config; using defaults without overwriting it", exception);
            config = new PiechartConfig();
        }
    }

    private static Path getConfigPath() {
        return Objects.requireNonNull(configPath, "Piechart config manager has not been initialized");
    }

    private static void moveTemporaryConfig(Path temporaryPath, Path destination) throws IOException {
        try {
            Files.move(
                temporaryPath,
                destination,
                StandardCopyOption.ATOMIC_MOVE,
                StandardCopyOption.REPLACE_EXISTING
            );
        } catch (AtomicMoveNotSupportedException exception) {
            Files.move(temporaryPath, destination, StandardCopyOption.REPLACE_EXISTING);
        }
    }

    private static boolean backupBrokenConfig() {
        Path path = getConfigPath();
        Path backupPath = path.resolveSibling(path.getFileName() + ".broken-" + System.currentTimeMillis());

        try {
            Files.move(path, backupPath);
            LOGGER.warn("Moved broken Piechart config to {}", backupPath);
            return true;
        } catch (IOException exception) {
            LOGGER.error("Failed to back up broken Piechart config", exception);
            return false;
        }
    }
}
