package dev.elpu7.piechart.quilt.mixin.client;

import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.util.Map;
import net.minecraft.client.resources.language.ClientLanguage;
import net.minecraft.locale.Language;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(ClientLanguage.class)
public abstract class ClientLanguageMixin {
    private static final String PIECHART_LANGUAGE = "/assets/piechart/lang/en_us.json";

    @ModifyArg(
        method = "loadFrom",
        at = @At(
            value = "INVOKE",
            target = "Ljava/util/Map;copyOf(Ljava/util/Map;)Ljava/util/Map;"
        ),
        index = 0
    )
    private static Map<String, String> piechart$addTranslations(Map<String, String> translations) {
        try (InputStream input = ClientLanguageMixin.class.getResourceAsStream(PIECHART_LANGUAGE)) {
            if (input == null) {
                throw new IllegalStateException("Missing Piechart language resource");
            }

            Language.loadFromJson(input, translations::putIfAbsent);
            return translations;
        } catch (IOException exception) {
            throw new UncheckedIOException("Could not load Piechart translations", exception);
        }
    }
}
