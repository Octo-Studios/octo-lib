package it.hurts.sskirillss.octolib.config.impl;

import it.hurts.sskirillss.octolib.config.cfgbuilder.CompoundEntry;
import it.hurts.sskirillss.octolib.config.loader.IConfigFileLoader;

public abstract class CompoundConfig implements OctoConfig {
    
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
