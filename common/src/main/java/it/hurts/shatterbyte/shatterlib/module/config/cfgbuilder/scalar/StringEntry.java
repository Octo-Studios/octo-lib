package it.hurts.shatterbyte.shatterlib.module.config.cfgbuilder.scalar;

import it.hurts.shatterbyte.shatterlib.module.config.cfgbuilder.CfgTag;

public class StringEntry extends ScalarEntry{
    
    public StringEntry(String value) {
        super(value, CfgTag.STR);
    }
    
}
