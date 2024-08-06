package it.hurts.octostudios.octolib.modules.config.cfgbuilder;

import it.hurts.octostudios.octolib.modules.config.cfgbuilder.scalar.ScalarEntry;
import org.yaml.snakeyaml.util.EnumUtils;

public class EnumEntry extends ScalarEntry {
    
    public EnumEntry(Enum<?> object) {
        super(object, CfgTag.ENUM);
    }
    
    @Override
    public ConfigEntry refine(ConfigEntry entry) {
        if (entry.getTag() != CfgTag.ENUM && entry.getTag() != CfgTag.STR)
            return this;
        
        try {
            return new EnumEntry(EnumUtils.findEnumInsensitiveCase((Class<? extends Enum>) getData().getClass(), entry.getData().toString()));
        } catch (RuntimeException e) {
            return this;
        }
    }

}
