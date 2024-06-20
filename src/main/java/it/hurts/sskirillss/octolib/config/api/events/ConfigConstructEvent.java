package it.hurts.sskirillss.octolib.config.api.events;

import it.hurts.sskirillss.octolib.config.api.IOctoConfig;
import it.hurts.sskirillss.octolib.config.data.ConfigContext;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import net.neoforged.bus.api.Event;
import net.neoforged.bus.api.ICancellableEvent;
import net.neoforged.fml.event.IModBusEvent;

@Data
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class ConfigConstructEvent extends Event implements IModBusEvent, ICancellableEvent {
    private IOctoConfig constructor;

    private ConfigContext schema;
}