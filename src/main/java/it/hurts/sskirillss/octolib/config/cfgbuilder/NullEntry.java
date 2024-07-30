package it.hurts.sskirillss.octolib.config.cfgbuilder;

import it.hurts.sskirillss.octolib.config.cfgbuilder.scalar.ScalarEntry;

public class NullEntry extends ScalarEntry {
    
    public NullEntry() {
        super(null, CfgTag.NULL);
    }
    
}
