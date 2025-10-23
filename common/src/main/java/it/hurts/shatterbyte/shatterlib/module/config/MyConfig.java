package it.hurts.shatterbyte.shatterlib.module.config;

import it.hurts.shatterbyte.shatterlib.ShatterLib;
import it.hurts.shatterbyte.shatterlib.client.shake.ShakeData;
import it.hurts.shatterbyte.shatterlib.module.config.type.ColorEntry;
import it.hurts.shatterbyte.shatterlib.module.config.type.MapEntry;
import it.hurts.shatterbyte.shatterlib.module.config.type.SimpleEntry;
import it.hurts.shatterbyte.shatterlib.util.ShatterColor;
import lombok.Getter;
import net.minecraft.world.phys.Vec3;

import java.util.Map;

@Getter
public class MyConfig extends ShatterConfig {
    private ColorEntry testColor = ColorEntry.builder(new ShatterColor(1f, 0.2f, 1f, 1f))
            .includeAlpha(true)
            .withComment("test comment!")
            .build();

    private SimpleEntry<Boolean> testBoolean = SimpleEntry.builder(false)
            .withComment("Test boolean comment!")
            .build();

    private SimpleEntry<Double> testDouble = SimpleEntry.builder(35.4d)
            .withComment("double comment")
            .build();

    private SimpleEntry<String> testString = SimpleEntry.builder("aaa\nNEWLINE JUMPSCARE")
            .withComment("string comment")
            .build();

    private SimpleEntry<Float> testNumber = SimpleEntry.builder(2f)
            .withComment("float comment")
            .build();

    private SimpleEntry<Boolean> anotherTestBoolean = SimpleEntry.builder(true)
            .withComment("Another test boolean comment!")
            .build();

    private SimpleEntry<Vec3> someVec3 = SimpleEntry.builder(new Vec3(1.25d, 3d, 0.25d))
            .withComment("test comment vec3 bleeh")
            .build();

    private MapEntry<ShakeData> funkyMap = MapEntry.<ShakeData>builder()
            .addPair("shatterlib:test", new ShakeData(1f,1f,1f))
            .withComment("map")
            .build();

    @Override
    public String getPath() {
        return ShatterLib.MODID;
    }
}