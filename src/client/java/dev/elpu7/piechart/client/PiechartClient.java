package dev.elpu7.piechart.client;

import dev.elpu7.piechart.event.KeyInputHandler;
import net.fabricmc.api.ClientModInitializer;

public class PiechartClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        KeyInputHandler.register();
    }
}
