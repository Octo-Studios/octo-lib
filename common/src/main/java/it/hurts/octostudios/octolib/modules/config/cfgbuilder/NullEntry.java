package it.hurts.octostudios.octolib.modules.config.cfgbuilder;

import it.hurts.octostudios.octolib.modules.config.cfgbuilder.scalar.ScalarEntry;

public class NullEntry extends ScalarEntry {
    
    public NullEntry() {
        super(null, CfgTag.NULL);
    }
    
}
