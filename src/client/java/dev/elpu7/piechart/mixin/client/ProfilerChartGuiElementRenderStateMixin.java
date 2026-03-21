package dev.elpu7.piechart.mixin.client;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(net.minecraft.client.gui.render.state.special.ProfilerChartGuiElementRenderState.class)
public abstract class ProfilerChartGuiElementRenderStateMixin {
    private static final float PIE_DIAMETER = 210.0F;

    @Shadow
    public abstract int x1();

    @Shadow
    public abstract int x2();

    @Inject(method = "scale", at = @At("HEAD"), cancellable = true)
    private void piechart$useScaledBounds(CallbackInfoReturnable<Float> cir) {
        cir.setReturnValue((x2() - x1()) / PIE_DIAMETER);
    }
}
