package it.hurts.octostudios.octolib.modules.config.impl;

import it.hurts.octostudios.octolib.modules.config.cfgbuilder.CompoundEntry;
import it.hurts.octostudios.octolib.modules.config.loader.IConfigFileLoader;

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
