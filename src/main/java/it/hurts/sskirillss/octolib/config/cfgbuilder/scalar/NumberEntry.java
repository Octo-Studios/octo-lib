package it.hurts.sskirillss.octolib.config.cfgbuilder.scalar;

import it.hurts.sskirillss.octolib.config.cfgbuilder.CfgTag;

public abstract class NumberEntry extends ScalarEntry{
    
    public NumberEntry(Number value, CfgTag tag) {
        super(value, tag);
    }
    
}
