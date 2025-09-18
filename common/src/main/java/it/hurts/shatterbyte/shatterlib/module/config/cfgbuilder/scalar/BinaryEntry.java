package it.hurts.shatterbyte.shatterlib.module.config.cfgbuilder.scalar;

import it.hurts.shatterbyte.shatterlib.module.config.cfgbuilder.CfgTag;

public class BinaryEntry extends NumberEntry{
    
    public BinaryEntry(byte value) {
        super(value, CfgTag.BINARY);
    }
    
}
