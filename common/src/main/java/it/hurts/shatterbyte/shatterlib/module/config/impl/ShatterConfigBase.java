package it.hurts.shatterbyte.shatterlib.module.config.impl;

import it.hurts.shatterbyte.shatterlib.module.config.loader.IConfigFileLoader;

public class ShatterConfigBase implements ShatterConfig {
    
    Object object;
    ConfigSide side;
    
    public ShatterConfigBase(Object object) {
        this.object = object;
    }
    
    public ShatterConfigBase(Object object, ConfigSide side) {
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
