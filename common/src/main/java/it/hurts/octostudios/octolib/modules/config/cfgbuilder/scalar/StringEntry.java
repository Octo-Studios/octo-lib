package it.hurts.octostudios.octolib.modules.config.cfgbuilder.scalar;

import it.hurts.octostudios.octolib.modules.config.cfgbuilder.CfgTag;

public class StringEntry extends ScalarEntry{
    
    public StringEntry(String value) {
        super(value, CfgTag.STR);
    }
    
}
