package it.hurts.shatterbyte.byteapi.module.config;

import de.marhali.json5.Json5Object;

import java.util.function.Consumer;

public record SchemaFixer(int from, int to, Consumer<Json5Object> fixer) {
    /**
     * @param configSchema
     * @return Updated schema version
     */
    public int apply(Json5Object configSchema) {
        fixer.accept(configSchema);
        return to;
    }
}
