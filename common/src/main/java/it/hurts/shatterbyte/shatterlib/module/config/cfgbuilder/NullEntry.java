package it.hurts.shatterbyte.shatterlib.module.config.cfgbuilder;

import it.hurts.shatterbyte.shatterlib.module.config.cfgbuilder.scalar.ScalarEntry;

public class NullEntry extends ScalarEntry {
    
    public NullEntry() {
        super(null, CfgTag.NULL);
    }
    
}
