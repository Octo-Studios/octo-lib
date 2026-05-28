package it.hurts.shatterbyte.shatterlib.mixin;

import net.minecraft.client.gui.components.AbstractWidget;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(AbstractWidget.class)
public interface AbstractWidgetAccessor {
    @Accessor("x")
    int getLocalX();
    @Accessor("y")
    int getLocalY();
}