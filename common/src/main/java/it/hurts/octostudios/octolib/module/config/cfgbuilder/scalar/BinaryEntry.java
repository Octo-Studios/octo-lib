package it.hurts.octostudios.octolib.module.config.cfgbuilder.scalar;

import it.hurts.octostudios.octolib.module.config.cfgbuilder.CfgTag;

public class BinaryEntry extends NumberEntry{
    
    public BinaryEntry(byte value) {
        super(value, CfgTag.BINARY);
    }
    
}
