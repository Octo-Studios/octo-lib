package it.hurts.shatterbyte.shatterlib.module.config.dev.data;

import it.hurts.shatterbyte.shatterlib.module.config.type.ListEntry;
import it.hurts.shatterbyte.shatterlib.module.config.type.MapEntry;
import lombok.NoArgsConstructor;
import net.minecraft.world.phys.Vec3;

public class AbilitiesData {
    private MapEntry<AbilityData> abilities = MapEntry.<AbilityData>builder()
            .addPair("ability1", new AbilityData())
            .addPair("ability2", new AbilityData())
            .addPair("ability3", new AbilityData())
            .build();

    private MapEntry<SynergyData> synergies = MapEntry.<SynergyData>builder()
            .addPair("synergy1", new SynergyData())
            .addPair("synergy2", new SynergyData())
            .addPair("synergy3", new SynergyData())
            .build();
}