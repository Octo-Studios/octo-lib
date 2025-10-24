package it.hurts.shatterbyte.shatterlib.module.config.dev;

import it.hurts.shatterbyte.shatterlib.module.config.ShatterConfig;
import it.hurts.shatterbyte.shatterlib.module.config.dev.data.AbilitiesData;
import it.hurts.shatterbyte.shatterlib.module.config.dev.data.RelicData;
import it.hurts.shatterbyte.shatterlib.module.config.type.SimpleEntry;
import net.minecraft.world.phys.Vec3;

public class ExampleConfig extends ShatterConfig {
    private SimpleEntry<RelicData> data = SimpleEntry.builder(new RelicData())
            .build();

    @Override
    public String getPath() {
        return "example";
    }
}
