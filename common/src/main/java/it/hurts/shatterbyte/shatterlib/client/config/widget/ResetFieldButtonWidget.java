package it.hurts.shatterbyte.shatterlib.client.config.widget;

import it.hurts.shatterbyte.shatterlib.client.config.AbstractEntryWidget;
import it.hurts.shatterbyte.shatterlib.client.config.UIElements;

public class ResetFieldButtonWidget extends IconButtonWidget<FieldWidget> {
    public ResetFieldButtonWidget(AbstractEntryWidget<?> entry) {
        super(0, 0, 13, 14, entry::resetValue, UIElements.ICON_RESET);
    }
}
