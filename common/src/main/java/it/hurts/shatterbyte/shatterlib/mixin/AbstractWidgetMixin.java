package it.hurts.shatterbyte.shatterlib.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.ref.LocalDoubleRef;
import it.hurts.shatterbyte.shatterlib.platform.ShatterLibServices;
import it.hurts.shatterbyte.shatterlib.client.screen.widget.Child;
import it.hurts.shatterbyte.shatterlib.client.screen.widget.HasRenderMatrix;
import it.hurts.shatterbyte.shatterlib.util.RenderUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.world.phys.Vec2;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(AbstractWidget.class)
public abstract class AbstractWidgetMixin {
    @Shadow public abstract boolean isMouseOver(double mouseX, double mouseY);
    @Shadow protected boolean isHovered;

    @Shadow
    public abstract int getX();

    @Shadow
    public abstract int getY();

    @Shadow
    public abstract int getWidth();

    @Shadow
    public abstract int getHeight();

    @Shadow
    public abstract boolean isHovered();

    @Shadow
    public abstract boolean isFocused();

    @Accessor("x")
    public abstract int getLocalX();
    @Accessor("y")
    public abstract int getLocalY();

    @Inject(method = "mouseClicked", at = @At("RETURN"), cancellable = true)
    private void inject(MouseButtonEvent event, boolean isDoubleClick, CallbackInfoReturnable<Boolean> cir) {
        if (this instanceof Child<?>) {
            cir.setReturnValue(this.isMouseOver(event.x(), event.y()));
        }
    }

    @Inject(method = "extractRenderState", at = @At("TAIL"))
    private void debugHoverRender(GuiGraphicsExtractor guiGraphics, int mouseX, int mouseY, float partialTick, CallbackInfo ci) {
        if (!ShatterLibServices.platform().isDevelopmentEnvironment() || !Minecraft.getInstance().hasShiftDown()) {
            return;
        }

        if (this.isHovered()) {
            guiGraphics.fill(this.getX(), this.getY(), this.getX() + this.getWidth(), this.getY() + this.getHeight(), 0x55ff0000);
        }

        //if (this.isFocused()) {
            RenderUtils.renderOutline(guiGraphics, this.getX(), this.getY(), this.getWidth(), this.getHeight(), 0xffff0000);
        //}
    }

    @Redirect(method = "extractRenderState", at = @At(value = "FIELD", target = "Lnet/minecraft/client/gui/components/AbstractWidget;isHovered:Z", opcode = Opcodes.PUTFIELD))
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
            cir.setReturnValue(child.getGlobalX());
        }
    }

    @Inject(method = "getY", at = @At("HEAD"), cancellable = true)
    private void getGlobalY(CallbackInfoReturnable<Integer> cir) {
        if (this instanceof Child<?> child) {
            cir.setReturnValue(child.getGlobalY());
        }
    }
}
