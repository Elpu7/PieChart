package dev.elpu7.piechart.client;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonParseException;
import net.fabricmc.loader.api.FabricLoader;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;

public final class PiechartConfig {
    public static final double MIN_SCALE = 0.35;
    public static final double MAX_SCALE = 3.0;

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final Path PATH = FabricLoader.getInstance().getConfigDir().resolve("piechart.json");

    private static PiechartConfig instance;

    private double offsetX;
    private double offsetY;
    private double scale;

    public PiechartConfig() {
        this.offsetX = 0.0;
        this.offsetY = 0.0;
        this.scale = 1.0;
    }

    public static PiechartConfig getInstance() {
        if (instance == null) {
            instance = load();
        }

        return instance;
    }

    private static PiechartConfig load() {
        if (!Files.exists(PATH)) {
            return new PiechartConfig();
        }

        try (Reader reader = Files.newBufferedReader(PATH)) {
            PiechartConfig config = GSON.fromJson(reader, PiechartConfig.class);
            return config != null ? config.clamped() : new PiechartConfig();
        } catch (IOException | JsonParseException exception) {
            return new PiechartConfig();
        }
    }

    public void save() {
        clamped();

        try {
            Files.createDirectories(PATH.getParent());
            try (Writer writer = Files.newBufferedWriter(PATH)) {
                GSON.toJson(this, writer);
            }
        } catch (IOException ignored) {
        }
    }

    public double getOffsetX() {
        return offsetX;
    }

    public void setOffsetX(double offsetX) {
        this.offsetX = offsetX;
    }

    public double getOffsetY() {
        return offsetY;
    }

    public void setOffsetY(double offsetY) {
        this.offsetY = offsetY;
    }

    public double getScale() {
        return scale;
    }

    public void setScale(double scale) {
        this.scale = Math.clamp(scale, MIN_SCALE, MAX_SCALE);
    }

    public void reset() {
        offsetX = 0.0;
        offsetY = 0.0;
        scale = 1.0;
    }

    private PiechartConfig clamped() {
        scale = Math.clamp(scale, MIN_SCALE, MAX_SCALE);
        offsetX = Math.clamp(offsetX, -800.0, 800.0);
        offsetY = Math.clamp(offsetY, -800.0, 800.0);
        return this;
    }
}
