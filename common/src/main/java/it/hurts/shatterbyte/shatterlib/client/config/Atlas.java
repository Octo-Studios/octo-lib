package it.hurts.shatterbyte.shatterlib.client.config;

import lombok.AllArgsConstructor;
import lombok.Getter;
import net.minecraft.resources.ResourceLocation;

@AllArgsConstructor
@Getter
public class Atlas {
    ResourceLocation location;
    int textureWidth;
    int textureHeight;
}
