package it.hurts.octostudios.octolib.modules.config.cfgbuilder.scalar;

import it.hurts.octostudios.octolib.modules.config.cfgbuilder.CfgTag;

public class IntEntry extends NumberEntry{
    
    public IntEntry(int value) {
        this(Integer.valueOf(value));
    }

    public IntEntry(Number value) {
        super(value, CfgTag.INT);
    }
    
}
