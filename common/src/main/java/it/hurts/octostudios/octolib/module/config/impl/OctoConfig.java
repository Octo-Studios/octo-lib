package it.hurts.octostudios.octolib.module.config.impl;

import it.hurts.octostudios.octolib.module.config.loader.IConfigFileLoader;

public interface OctoConfig {
    
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
