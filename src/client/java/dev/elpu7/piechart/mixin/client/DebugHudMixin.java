package dev.elpu7.piechart.mixin.client;

import dev.elpu7.piechart.client.PiechartState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.hud.debug.chart.PieChart;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(net.minecraft.client.gui.hud.DebugHud.class)
public abstract class DebugHudMixin {
    @Shadow
    @Final
    private MinecraftClient client;

    @Inject(method = "shouldShowRenderingChart", at = @At("HEAD"), cancellable = true)
    private void piechart$showRenderingChartWithoutF3(CallbackInfoReturnable<Boolean> cir) {
        if (PiechartState.isModKeyPieChartVisible() && !client.debugHudEntryList.isF3Enabled()) {
            cir.setReturnValue(true);
        }
    }

    @Redirect(
            method = "render",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/gui/hud/debug/chart/PieChart;render(Lnet/minecraft/client/gui/DrawContext;)V"
            )
    )
    private void piechart$skipVanillaPieChartRender(PieChart pieChart, DrawContext context) {
        if (!PiechartState.isModKeyPieChartVisible()) {
            pieChart.render(context);
        }
    }
}
