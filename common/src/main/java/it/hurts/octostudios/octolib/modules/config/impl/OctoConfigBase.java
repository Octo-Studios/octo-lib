package it.hurts.octostudios.octolib.modules.config.impl;

import it.hurts.octostudios.octolib.modules.config.loader.IConfigFileLoader;

public class OctoConfigBase implements OctoConfig {
    
    Object object;
    
    public OctoConfigBase(Object object) {
        this.object = object;
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
    
}
