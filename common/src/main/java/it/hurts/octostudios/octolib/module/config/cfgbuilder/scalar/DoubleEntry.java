package it.hurts.octostudios.octolib.module.config.cfgbuilder.scalar;

import it.hurts.octostudios.octolib.module.config.cfgbuilder.CfgTag;

public class DoubleEntry extends NumberEntry{
    
    public DoubleEntry(double value) {
        super(value, CfgTag.FLOAT);
    }
    
}
