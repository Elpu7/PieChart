package dev.elpu7.piechart.mixin.client;

import dev.elpu7.piechart.client.PiechartState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(net.minecraft.client.gui.hud.DebugHud.class)
public abstract class DebugHudMixin {
    @Inject(method = "shouldShowRenderingChart", at = @At("HEAD"), cancellable = true)
    private void piechart$showRenderingChartWithoutF3(CallbackInfoReturnable<Boolean> cir) {
        if (PiechartState.isModKeyPieChartVisible()) {
            cir.setReturnValue(true);
        }
    }
}
