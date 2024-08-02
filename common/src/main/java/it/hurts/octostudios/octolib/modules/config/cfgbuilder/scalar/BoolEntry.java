package it.hurts.octostudios.octolib.modules.config.cfgbuilder.scalar;

import it.hurts.octostudios.octolib.modules.config.cfgbuilder.CfgTag;

public class BoolEntry extends ScalarEntry{
    
    public BoolEntry(boolean value) {
        super(String.valueOf(value), CfgTag.BOOL);
    }
    
}
