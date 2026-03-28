package dev.elpu7.piechart.mixin.client;

import dev.elpu7.piechart.client.PiechartEditScreen;
import dev.elpu7.piechart.client.PiechartState;
import dev.elpu7.piechart.client.PiechartRenderer;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Gui.class)
public abstract class InGameHudMixin {
    @Shadow
    @Final
    private Minecraft minecraft;

    @Inject(method = "extractRenderState", at = @At("TAIL"))
    private void piechart$renderPieChartOnly(GuiGraphicsExtractor graphics, DeltaTracker deltaTracker, CallbackInfo ci) {
        if (PiechartState.isModKeyPieChartVisible()
                && !(minecraft.screen instanceof PiechartEditScreen)
                && !minecraft.debugEntries.isOverlayVisible()) {
            PiechartRenderer.renderConfiguredPieChart(minecraft, graphics);
        }
    }
}
