package dev.elpu7.piechart.quilt.mixin.client;

import dev.elpu7.piechart.client.PiechartController;
import net.minecraft.client.Minecraft;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Minecraft.class)
public abstract class MinecraftMixin {
    @Inject(method = "tick", at = @At("TAIL"))
    private void piechart$onEndTick(CallbackInfo ci) {
        PiechartController.onEndTick((Minecraft)(Object)this);
    }
}
