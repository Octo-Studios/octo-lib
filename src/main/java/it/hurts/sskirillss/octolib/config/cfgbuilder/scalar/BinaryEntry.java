package it.hurts.sskirillss.octolib.config.cfgbuilder.scalar;

import it.hurts.sskirillss.octolib.config.cfgbuilder.CfgTag;

public class BinaryEntry extends NumberEntry{
    
    public BinaryEntry(byte value) {
        super(value, CfgTag.BINARY);
    }
    
}
