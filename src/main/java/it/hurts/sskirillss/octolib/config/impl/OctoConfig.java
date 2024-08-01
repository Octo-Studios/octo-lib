package it.hurts.sskirillss.octolib.config.impl;

import it.hurts.sskirillss.octolib.config.loader.IConfigFileLoader;

public interface OctoConfig {
    
    Object prepareData();
    
    void onLoadObject(Object object);
    
    IConfigFileLoader<?, ?> getLoader();
    
}
