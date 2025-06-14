package it.hurts.octostudios.octolib.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.ref.LocalBooleanRef;
import com.llamalad7.mixinextras.sugar.ref.LocalDoubleRef;
import it.hurts.octostudios.octolib.client.screen.widget.Child;
import it.hurts.octostudios.octolib.client.screen.widget.HasRenderMatrix;
import it.hurts.octostudios.octolib.client.shake.Shakeable;
import it.hurts.octostudios.octolib.util.RenderUtils;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.world.phys.Vec2;
import org.joml.Vector2f;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(AbstractWidget.class)
public abstract class AbstractWidgetMixin {
    @Shadow public abstract boolean isMouseOver(double mouseX, double mouseY);
    @Shadow protected boolean isHovered;

    @Accessor("x")
    public abstract int getLocalX();
    @Accessor("y")
    public abstract int getLocalY();

    @Inject(method = "clicked", at = @At("RETURN"), cancellable = true)
    private void inject(double mouseX, double mouseY, CallbackInfoReturnable<Boolean> cir) {
        if (this instanceof Child<?>) {
            cir.setReturnValue(this.isMouseOver(mouseX, mouseY));
        }
    }

    @Redirect(method = "render", at = @At(value = "FIELD", target = "Lnet/minecraft/client/gui/components/AbstractWidget;isHovered:Z", opcode = Opcodes.PUTFIELD))
    private void injectAsWell(AbstractWidget instance, boolean value, @Local(ordinal = 0, argsOnly = true) int mouseX, @Local(ordinal = 1, argsOnly = true) int mouseY) {
        if (this instanceof Child<?>) {
            this.isHovered = this.isMouseOver(mouseX, mouseY);
        } else this.isHovered = value;
    }

    @Inject(method = "isMouseOver", at = @At(value = "HEAD"))
    private void injectMouseOver(CallbackInfoReturnable<Boolean> cir,
                                 @Local(ordinal = 0, argsOnly = true) LocalDoubleRef mouseX,
                                 @Local(ordinal = 1, argsOnly = true) LocalDoubleRef mouseY
    ) {
        if (this instanceof Child<?> child && child.getParent() instanceof HasRenderMatrix has) {
            Vec2 bleh = RenderUtils.toScreenCoords(has.getMatrix(), mouseX.get(), mouseY.get());
            mouseX.set(bleh.x);
            mouseY.set(bleh.y);
        }
    }

    @Inject(method = "getX", at = @At("HEAD"), cancellable = true)
    private void getGlobalX(CallbackInfoReturnable<Integer> cir) {
        if (this instanceof Child<?> child) {
            cir.setReturnValue(child.getPosition().x);
        }
    }

    @Inject(method = "getY", at = @At("HEAD"), cancellable = true)
    private void getGlobalY(CallbackInfoReturnable<Integer> cir) {
        if (this instanceof Child<?> child) {
            cir.setReturnValue(child.getPosition().y);
        }
    }
}