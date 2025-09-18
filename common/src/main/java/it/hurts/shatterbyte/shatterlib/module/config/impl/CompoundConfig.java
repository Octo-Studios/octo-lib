package it.hurts.shatterbyte.shatterlib.module.config.impl;

import it.hurts.shatterbyte.shatterlib.module.config.cfgbuilder.CompoundEntry;
import it.hurts.shatterbyte.shatterlib.module.config.loader.IConfigFileLoader;

public abstract class CompoundConfig implements ShatterConfig {
    
    public CompoundConfig() {}
    
    @Override
    public Object prepareData() {
        CompoundEntry compoundConfig = new CompoundEntry();
        write(compoundConfig);
        return compoundConfig;
    }
    
    @Override
    public void onLoadObject(Object object) {
        read((CompoundEntry) object);
    }
    
    @Override
    public IConfigFileLoader<?, ?> getLoader() {
        return IConfigFileLoader.SOLID;
    }
    
    public abstract void write(CompoundEntry compound);
    
    public abstract void read(CompoundEntry compound);
    
    
}
