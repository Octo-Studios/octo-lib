package it.hurts.sskirillss.octolib.config.impl;

import it.hurts.sskirillss.octolib.config.loader.DirectorySpreadLoader;
import it.hurts.sskirillss.octolib.config.loader.IConfigFileLoader;

import java.util.Map;

public class DirectoryConfig extends OctoConfigBase {
    
    public DirectoryConfig(Map<String, ?> object) {
        super(object);
    }
    
    @Override
    public IConfigFileLoader<?, ?> getLoader() {
        return new DirectorySpreadLoader<>(IConfigFileLoader.SOLID);
    }
    
}
