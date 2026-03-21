package dev.elpu7.piechart.mixin.client;

import net.minecraft.client.gui.hud.DebugHud;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(DebugHud.class)
public interface DebugHudAccessor {
    @Accessor("renderingChartVisible")
    boolean piechart$isRenderingChartVisible();
}
