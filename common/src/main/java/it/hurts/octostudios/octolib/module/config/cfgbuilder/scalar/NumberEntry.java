package it.hurts.octostudios.octolib.module.config.cfgbuilder.scalar;

import it.hurts.octostudios.octolib.module.config.cfgbuilder.CfgTag;

public abstract class NumberEntry extends ScalarEntry{
    
    public NumberEntry(Number value, CfgTag tag) {
        super(value, tag);
    }
    
}
