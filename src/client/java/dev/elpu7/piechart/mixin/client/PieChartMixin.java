package dev.elpu7.piechart.mixin.client;

import dev.elpu7.piechart.client.PiechartRenderContext;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

@Mixin(net.minecraft.client.gui.components.debugchart.ProfilerPieChart.class)
public abstract class PieChartMixin {
    @ModifyArgs(
            method = "extractRenderState",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/gui/GuiGraphicsExtractor;profilerChart(Ljava/util/List;IIII)V"
            )
    )
    private void piechart$scaleProfilerChart(Args args) {
        if (!PiechartRenderContext.isActive()) {
            return;
        }

        args.set(1, PiechartRenderContext.transformX(args.get(1)));
        args.set(2, PiechartRenderContext.transformY(args.get(2)));
        args.set(3, PiechartRenderContext.transformX(args.get(3)));
        args.set(4, PiechartRenderContext.transformY(args.get(4)));
    }
}
