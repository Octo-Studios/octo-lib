package it.hurts.octostudios.octolib.module.config.cfgbuilder.scalar;

import it.hurts.octostudios.octolib.module.config.cfgbuilder.CfgTag;

public class StringEntry extends ScalarEntry{
    
    public StringEntry(String value) {
        super(value, CfgTag.STR);
    }
    
}
