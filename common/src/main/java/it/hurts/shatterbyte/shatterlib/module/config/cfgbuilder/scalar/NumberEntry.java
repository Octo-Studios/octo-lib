package it.hurts.shatterbyte.shatterlib.module.config.cfgbuilder.scalar;

import it.hurts.shatterbyte.shatterlib.module.config.cfgbuilder.CfgTag;

public abstract class NumberEntry extends ScalarEntry{
    
    public NumberEntry(Number value, CfgTag tag) {
        super(value, tag);
    }
    
}
