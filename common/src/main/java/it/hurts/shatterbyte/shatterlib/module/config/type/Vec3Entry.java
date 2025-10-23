package it.hurts.shatterbyte.shatterlib.module.config.type;

import de.marhali.json5.Json5;
import de.marhali.json5.Json5Element;
import de.marhali.json5.Json5Object;
import de.marhali.json5.Json5Primitive;
import net.minecraft.world.phys.Vec3;

public class Vec3Entry extends AbstractEntry<Vec3, Vec3Entry> {
    private Vec3Entry(Vec3 defaultValue) {
        super(defaultValue);
    }

    public static class Builder extends AbstractEntry.Builder<Vec3, Vec3Entry, Builder> {
        public Builder(Vec3 defaultValue) {
            super(defaultValue);
        }

        @Override
        protected Vec3Entry createEntry(Vec3 defaultValue) {
            return new Vec3Entry(defaultValue);
        }
    }

    public static Builder builder(Vec3 defaultValue) {
        return new Builder(defaultValue);
    }
}
