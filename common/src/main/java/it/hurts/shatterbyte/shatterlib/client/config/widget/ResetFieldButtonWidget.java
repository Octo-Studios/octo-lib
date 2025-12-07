package it.hurts.shatterbyte.shatterlib.client.config.widget;

import it.hurts.shatterbyte.shatterlib.client.config.AbstractEntryWidget;
import it.hurts.shatterbyte.shatterlib.client.config.UIElements;

public class ResetFieldButtonWidget extends IconButtonWidget {
    public ResetFieldButtonWidget(AbstractEntryWidget<?> entry) {
        super(0, 0, 14, 14, entry::resetValue, UIElements.ICON_RESET);
    }
}
