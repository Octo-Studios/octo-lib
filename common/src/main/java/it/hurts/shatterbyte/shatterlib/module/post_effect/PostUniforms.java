package it.hurts.shatterbyte.shatterlib.module.post_effect;

import com.mojang.blaze3d.buffers.GpuBuffer;
import com.mojang.blaze3d.buffers.Std140Builder;
import com.mojang.blaze3d.buffers.Std140SizeCalculator;
import com.mojang.blaze3d.systems.RenderSystem;
import it.hurts.shatterbyte.shatterlib.mixin.post_effect.PostChainAccessor;
import it.hurts.shatterbyte.shatterlib.mixin.post_effect.PostPassAccessor;
import net.minecraft.client.renderer.PostChain;
import org.lwjgl.system.MemoryStack;

public final class PostUniforms {
    private PostUniforms() {
    }

    public static void setFloats(PostChain postChain, String group, float... values) {
        if (values.length == 0)
            return;

        var passes = ((PostChainAccessor) postChain).shatterlib$getPasses();
        var device = RenderSystem.getDevice();

        for (var pass : passes) {
            var customUniforms = ((PostPassAccessor) pass).shatterlib$getCustomUniforms();
            var previous = customUniforms.get(group);

            if (previous == null)
                continue;

            try (var stack = MemoryStack.stackPush()) {
                var calculator = new Std140SizeCalculator();

                for (var ignored : values)
                    calculator.putFloat();

                var builder = Std140Builder.onStack(stack, calculator.get());

                for (var value : values)
                    builder.putFloat(value);

                customUniforms.put(group, device.createBuffer(() -> group, GpuBuffer.USAGE_UNIFORM, builder.get()));
            }

            previous.close();
        }
    }
}
