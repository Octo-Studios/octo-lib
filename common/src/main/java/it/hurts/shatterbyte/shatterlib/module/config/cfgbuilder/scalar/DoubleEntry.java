package it.hurts.shatterbyte.shatterlib.module.config.cfgbuilder.scalar;

import it.hurts.shatterbyte.shatterlib.module.config.cfgbuilder.CfgTag;

public class DoubleEntry extends NumberEntry{
    
    public DoubleEntry(double value) {
        super(value, CfgTag.FLOAT);
    }
    
}
