package it.hurts.sskirillss.octolib.config.api.events;

import it.hurts.sskirillss.octolib.config.data.OctoConfig;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import net.minecraftforge.eventbus.api.Cancelable;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.fml.event.IModBusEvent;

@Data
@Cancelable
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class ConfigSaveEvent extends Event implements IModBusEvent {
    private OctoConfig config;
}