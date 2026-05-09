package it.hurts.shatterbyte.byteapi.client.config;

import lombok.AllArgsConstructor;
import lombok.Getter;
import net.minecraft.resources.Identifier;

@AllArgsConstructor
@Getter
public class Atlas {
    Identifier location;
    int textureWidth;
    int textureHeight;
}
