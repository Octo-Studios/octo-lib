package it.hurts.shatterbyte.shatterlib.module.config.impl;

import it.hurts.shatterbyte.shatterlib.module.config.loader.IConfigFileLoader;

import java.util.Collection;

public class FileSpreadConfig extends ShatterConfigBase {
    
    public FileSpreadConfig(Collection<?> object) {
        super(object);
    }
    
    public FileSpreadConfig(Collection<?> object, ConfigSide side) {
        super(object, side);
    }
    
    @Override
    public IConfigFileLoader<?, ?> getLoader() {
        return IConfigFileLoader.SOLID;
    }
    
}
