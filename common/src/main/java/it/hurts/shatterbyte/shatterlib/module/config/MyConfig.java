package it.hurts.shatterbyte.shatterlib.module.config;

import it.hurts.shatterbyte.shatterlib.ShatterLib;
import it.hurts.shatterbyte.shatterlib.client.shake.ShakeData;
import it.hurts.shatterbyte.shatterlib.module.config.type.BooleanEntry;
import it.hurts.shatterbyte.shatterlib.module.config.type.ColorEntry;
import it.hurts.shatterbyte.shatterlib.module.config.type.MapEntry;
import it.hurts.shatterbyte.shatterlib.module.config.type.Vec3Entry;
import it.hurts.shatterbyte.shatterlib.util.ShatterColor;
import lombok.Getter;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.Vec3;

import java.util.Map;

@Getter
public class MyConfig extends ShatterConfig {
    private ColorEntry testColor = ColorEntry.builder(new ShatterColor(1f, 0.2f, 1f, 1f))
            .includeAlpha(true)
            .withComment("test comment!")
            .withInlineComment("yay inline comment :D")
            .build();

    private BooleanEntry testBoolean = BooleanEntry.builder(false)
            .withComment("Test boolean comment!")
            .build();

    private BooleanEntry anotherTestBoolean = BooleanEntry.builder(true)
            .withComment("Another test boolean comment!")
            .build();

    private Vec3Entry someVec3 = Vec3Entry.builder(new Vec3(1.25d, 3d, 0.25d))
            .withComment("test comment vec3 bleeh")
            .build();

    private MapEntry<ShakeData> funkyMap = MapEntry.builder(Map.of(
            "shatterlib:test", new ShakeData(1f,1f,1f),
            "shatterlib:test2", new ShakeData(0.2f,3f,10.5d)
    )).withComment("map").build();

    @Override
    public String getPath() {
        return ShatterLib.MODID;
    }
}