package it.hurts.sskirillss.octolib.config.cfgbuilder.scalar;

import it.hurts.sskirillss.octolib.config.cfgbuilder.CfgTag;

public class StringEntry extends ScalarEntry{
    
    public StringEntry(String value) {
        super(value, CfgTag.STR);
    }
    
}
