package it.hurts.shatterbyte.shatterlib.module.config.type;

import de.marhali.json5.Json5;
import de.marhali.json5.Json5Element;
import lombok.Getter;

@Getter
public abstract class AbstractEntry<T> {
    private String comment = "";
    private String inlineComment = "";
    private T value;

    public AbstractEntry(T defaultValue) {
        this.value = defaultValue;
    }

    public abstract void loadFromJson(Json5 json, Json5Element element);
    public abstract Json5Element saveToJson(Json5 json);
}
