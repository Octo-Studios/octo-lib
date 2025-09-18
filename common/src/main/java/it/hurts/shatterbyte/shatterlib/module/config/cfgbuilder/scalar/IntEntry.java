package it.hurts.shatterbyte.shatterlib.module.config.cfgbuilder.scalar;

import it.hurts.shatterbyte.shatterlib.module.config.cfgbuilder.CfgTag;

public class IntEntry extends NumberEntry{
    
    public IntEntry(int value) {
        super(value, CfgTag.INT);
    }
    
}
