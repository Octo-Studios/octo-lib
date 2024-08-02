package it.hurts.octostudios.octolib.modules.config.cfgbuilder.scalar;

import it.hurts.octostudios.octolib.modules.config.cfgbuilder.CfgTag;
import it.hurts.octostudios.octolib.modules.config.cfgbuilder.ConfigEntry;
import it.hurts.octostudios.octolib.modules.config.cfgbuilder.EntryId;

public abstract class ScalarEntry extends ConfigEntry {
    
    Object value;
    
    public ScalarEntry(Object value, CfgTag tag) {
        super(tag, EntryId.SCALAR);
        this.value = value;
    }
    
    @Override
    public final Object getData() {
        return value;
    }
    
}
