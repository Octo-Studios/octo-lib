package it.hurts.sskirillss.octolib.config.impl;

import it.hurts.sskirillss.octolib.config.loader.IConfigFileLoader;

public interface OctoConfig {
    
    default Object prepareData() {
        return this;
    };
    
    default void onLoadObject(Object object) {};
    
    default IConfigFileLoader<?, ?> getLoader() {
        return IConfigFileLoader.SOLID;
    };
    
}
