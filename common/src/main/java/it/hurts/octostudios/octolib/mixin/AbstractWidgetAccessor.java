package it.hurts.octostudios.octolib.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.ref.LocalDoubleRef;
import it.hurts.octostudios.octolib.client.screen.widget.Child;
import it.hurts.octostudios.octolib.client.screen.widget.HasRenderMatrix;
import it.hurts.octostudios.octolib.util.RenderUtils;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.world.phys.Vec2;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(AbstractWidget.class)
public interface AbstractWidgetAccessor {
    @Accessor("x")
    int getLocalX();
    @Accessor("y")
    int getLocalY();
}