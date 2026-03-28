package dev.elpu7.piechart.mixin.client;

import dev.elpu7.piechart.client.PiechartState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.DebugScreenOverlay;
import net.minecraft.client.gui.components.debugchart.ProfilerPieChart;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(DebugScreenOverlay.class)
public abstract class DebugHudMixin {
    @Shadow
    @Final
    private Minecraft minecraft;

    @Inject(method = "showProfilerChart", at = @At("HEAD"), cancellable = true)
    private void piechart$showRenderingChartWithoutF3(CallbackInfoReturnable<Boolean> cir) {
        if (PiechartState.isModKeyPieChartVisible() && !minecraft.debugEntries.isOverlayVisible()) {
            cir.setReturnValue(true);
        }
    }

    @Redirect(
            method = "extractRenderState",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/gui/components/debugchart/ProfilerPieChart;extractRenderState(Lnet/minecraft/client/gui/GuiGraphicsExtractor;)V"
            )
    )
    private void piechart$skipVanillaPieChartRender(ProfilerPieChart pieChart, GuiGraphicsExtractor graphics) {
        if (!PiechartState.isModKeyPieChartVisible()) {
            pieChart.extractRenderState(graphics);
        }
    }
}
