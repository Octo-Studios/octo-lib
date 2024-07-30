package it.hurts.sskirillss.octolib.config.cfgbuilder.scalar;

import it.hurts.sskirillss.octolib.config.cfgbuilder.CfgTag;

public class DoubleEntry extends NumberEntry{
    
    public DoubleEntry(double value) {
        super(value, CfgTag.FLOAT);
    }
    
}
