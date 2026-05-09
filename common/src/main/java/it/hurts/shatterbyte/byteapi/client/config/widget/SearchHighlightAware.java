package it.hurts.shatterbyte.byteapi.client.config.widget;

import org.jetbrains.annotations.Nullable;

public interface SearchHighlightAware {
    void setSearchHighlightQuery(@Nullable String query);
}
