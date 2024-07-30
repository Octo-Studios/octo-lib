package it.hurts.sskirillss.octolib.config.cfgbuilder.scalar;

import it.hurts.sskirillss.octolib.config.cfgbuilder.CfgTag;

public class BoolEntry extends ScalarEntry{
    
    public BoolEntry(boolean value) {
        super(String.valueOf(value), CfgTag.BOOL);
    }
    
}
