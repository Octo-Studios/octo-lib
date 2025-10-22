package it.hurts.shatterbyte.shatterlib.module.config.type;

import de.marhali.json5.Json5Element;
import lombok.Getter;
import lombok.Setter;

@Getter
public abstract class AbstractEntry<T, E extends AbstractEntry<T, E>> {
    private String comment = "";
    private String inlineComment = "";

    @Setter
    private T value;

    public AbstractEntry(T defaultValue) {
        this.value = defaultValue;
    }

    void setComment(String comment) {
        this.comment = comment;
    }

    void setInlineComment(String inlineComment) {
        this.inlineComment = inlineComment;
    }

    public abstract void loadFromJson(Json5Element element);
    public abstract Json5Element saveToJson();

    public static abstract class Builder<T, E extends AbstractEntry<T, E>, B extends Builder<T, E, B>> {
        protected T value;
        protected String comment = "";
        protected String inlineComment = "";

        protected Builder(T value) {
            this.value = value;
        }

        @SuppressWarnings("unchecked")
        public B withComment(String comment) {
            this.comment = comment;
            return (B) this;
        }

        @SuppressWarnings("unchecked")
        public B withInlineComment(String inlineComment) {
            this.inlineComment = inlineComment;
            return (B) this;
        }

        protected abstract E createEntry(T value);

        public E build() {
            E entry = createEntry(value);
            entry.setComment(comment);
            entry.setInlineComment(inlineComment);
            return entry;
        }
    }
}
