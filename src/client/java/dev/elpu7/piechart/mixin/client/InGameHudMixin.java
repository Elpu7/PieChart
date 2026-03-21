package dev.elpu7.piechart.mixin.client;

import dev.elpu7.piechart.client.PiechartEditScreen;
import dev.elpu7.piechart.client.PiechartState;
import dev.elpu7.piechart.client.PiechartRenderer;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.render.RenderTickCounter;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(InGameHud.class)
public abstract class InGameHudMixin {
    @Shadow
    @Final
    private MinecraftClient client;

    @Inject(method = "render", at = @At("TAIL"))
    private void piechart$renderPieChartOnly(DrawContext context, RenderTickCounter tickCounter, CallbackInfo ci) {
        boolean f3Enabled = client.debugHudEntryList.isF3Enabled();
        boolean vanillaPieChartVisible = f3Enabled
                && ((DebugHudAccessor) client.getDebugHud()).piechart$isRenderingChartVisible();

        if (PiechartState.isModKeyPieChartVisible()
                && !(client.currentScreen instanceof PiechartEditScreen)
                && !f3Enabled
                && !vanillaPieChartVisible) {
            PiechartRenderer.renderConfiguredPieChart(client, context);
        }
    }
}
