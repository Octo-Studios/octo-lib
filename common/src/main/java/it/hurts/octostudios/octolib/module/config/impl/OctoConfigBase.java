package it.hurts.octostudios.octolib.module.config.impl;

import it.hurts.octostudios.octolib.module.config.loader.IConfigFileLoader;

public class OctoConfigBase implements OctoConfig {
    
    Object object;
    ConfigSide side;
    
    public OctoConfigBase(Object object) {
        this.object = object;
    }
    
    public OctoConfigBase(Object object, ConfigSide side) {
        this.object = object;
        this.side = side;
    }
    
    @Override
    public Object prepareData() {
        return object;
    }
    
    @Override
    public void onLoadObject(Object object) {}
    
    @Override
    public IConfigFileLoader<?, ?> getLoader() {
        return IConfigFileLoader.SOLID;
    }
    
    @Override
    public ConfigSide getSide() {
        return side;
    }
    
}
