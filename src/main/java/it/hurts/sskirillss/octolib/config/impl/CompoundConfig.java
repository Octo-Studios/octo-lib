package it.hurts.sskirillss.octolib.config.impl;

import it.hurts.sskirillss.octolib.config.cfgbuilder.CompoundEntry;
import it.hurts.sskirillss.octolib.config.cfgbuilder.ConfigEntry;
import it.hurts.sskirillss.octolib.config.loader.DirectorySpreadLoader;
import it.hurts.sskirillss.octolib.config.loader.IConfigFileLoader;
import it.hurts.sskirillss.octolib.config.loader.SolidConfigLoader;

import java.util.Map;

public abstract class CompoundConfig implements OctoConfig {
    
    public CompoundConfig() {}
    
    @Override
    public Object prepareData() {
        CompoundEntry compoundConfig = new CompoundEntry();
        write(compoundConfig);
        return compoundConfig.getData();
    }
    
    @Override
    public void onLoadObject(Object object) {
        CompoundEntry entry = new CompoundEntry((Map<String, ConfigEntry>) object);
        read(entry);
    }
    
    protected boolean spreadFiles() {
        return false;
    }
    
    @Override
    public IConfigFileLoader<?, ?> getLoader() {
        if (spreadFiles())
            return new DirectorySpreadLoader<CompoundEntry>(new SolidConfigLoader<>());
        else
            return IConfigFileLoader.SOLID;
    }
    
    public abstract void write(CompoundEntry compound);
    
    public abstract void read(CompoundEntry compound);
    
    
}
