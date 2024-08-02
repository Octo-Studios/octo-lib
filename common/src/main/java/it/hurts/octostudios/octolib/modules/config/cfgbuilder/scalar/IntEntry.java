package it.hurts.octostudios.octolib.modules.config.cfgbuilder.scalar;

import it.hurts.octostudios.octolib.modules.config.cfgbuilder.CfgTag;

public class IntEntry extends NumberEntry{
    
    public IntEntry(int value) {
        super(value, CfgTag.INT);
    }
    
}
