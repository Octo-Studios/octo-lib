package it.hurts.octostudios.octolib.module.config.cfgbuilder.scalar;

import it.hurts.octostudios.octolib.module.config.cfgbuilder.CfgTag;

public class BoolEntry extends ScalarEntry{
    
    public BoolEntry(boolean value) {
        super(String.valueOf(value), CfgTag.BOOL);
    }
    
}
