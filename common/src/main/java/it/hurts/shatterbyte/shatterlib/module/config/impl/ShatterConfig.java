package it.hurts.shatterbyte.shatterlib.module.config.impl;

import it.hurts.shatterbyte.shatterlib.module.config.loader.IConfigFileLoader;

public interface ShatterConfig {
    
    default Object prepareData() {
        return this;
    };
    
    default void onLoadObject(Object object) {};
    
    default IConfigFileLoader<?, ?> getLoader() {
        return IConfigFileLoader.SOLID;
    };
    
    default ConfigSide getSide() {
        return ConfigSide.SERVER;
    }
    
}
