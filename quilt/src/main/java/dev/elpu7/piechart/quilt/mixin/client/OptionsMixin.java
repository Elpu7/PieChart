package dev.elpu7.piechart.quilt.mixin.client;

import dev.elpu7.piechart.client.PiechartClient;
import dev.elpu7.piechart.client.PiechartController;
import java.io.File;
import java.util.Arrays;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.Options;
import org.quiltmc.loader.api.QuiltLoader;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Options.class)
public abstract class OptionsMixin {
    @Shadow
    @Final
    @Mutable
    public KeyMapping[] keyMappings;

    @Inject(
        method = "<init>",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/Options;load()V",
            shift = At.Shift.BEFORE
        )
    )
    private void piechart$registerKeyMappings(Minecraft minecraft, File gameDirectory, CallbackInfo ci) {
        PiechartClient.initialize(QuiltLoader.getConfigDir(),
            QuiltLoader.getModContainer("piechart").orElseThrow().metadata().version().raw());
        KeyMapping.Category category = KeyMapping.Category.register(PiechartController.getKeyCategoryId());
        KeyMapping toggleKey = PiechartController.createToggleKey(category);
        KeyMapping editorKey = PiechartController.createEditorKey(category);

        this.keyMappings = Arrays.copyOf(this.keyMappings, this.keyMappings.length + 2);
        this.keyMappings[this.keyMappings.length - 2] = toggleKey;
        this.keyMappings[this.keyMappings.length - 1] = editorKey;
    }
}
