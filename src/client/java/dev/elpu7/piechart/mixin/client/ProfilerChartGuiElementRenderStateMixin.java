package dev.elpu7.piechart.mixin.client;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(net.minecraft.client.renderer.state.gui.pip.GuiProfilerChartRenderState.class)
public abstract class ProfilerChartGuiElementRenderStateMixin {
    private static final float CHART_WIDTH = 260.0F;

    @Shadow
    public abstract int x0();

    @Shadow
    public abstract int x1();

    @Inject(method = "scale", at = @At("HEAD"), cancellable = true)
    private void piechart$useScaledBounds(CallbackInfoReturnable<Float> cir) {
        cir.setReturnValue((x1() - x0()) / CHART_WIDTH);
    }
}
