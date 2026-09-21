package dev.elpu7.piechart.quilt.compat;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
import dev.elpu7.piechart.client.PiechartEditScreen;

public final class PiechartModMenu implements ModMenuApi {
    @Override
    public ConfigScreenFactory<?> getModConfigScreenFactory() {
        return PiechartEditScreen::new;
    }
}
