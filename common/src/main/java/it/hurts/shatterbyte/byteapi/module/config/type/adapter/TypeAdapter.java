package it.hurts.shatterbyte.byteapi.module.config.type.adapter;

import de.marhali.json5.Json5Element;

public interface TypeAdapter<T> {
    Json5Element encode(T value);
    T decode(Json5Element json);
}
