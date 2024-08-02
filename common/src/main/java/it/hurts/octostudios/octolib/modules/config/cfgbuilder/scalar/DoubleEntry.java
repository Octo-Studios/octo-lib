package it.hurts.octostudios.octolib.modules.config.cfgbuilder.scalar;

import it.hurts.octostudios.octolib.modules.config.cfgbuilder.CfgTag;

public class DoubleEntry extends NumberEntry{
    
    public DoubleEntry(double value) {
        super(value, CfgTag.FLOAT);
    }
    
}
