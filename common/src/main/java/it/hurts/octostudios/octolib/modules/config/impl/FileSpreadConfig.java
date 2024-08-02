package it.hurts.octostudios.octolib.modules.config.impl;

import it.hurts.octostudios.octolib.modules.config.loader.IConfigFileLoader;

import java.util.Collection;

public class FileSpreadConfig extends OctoConfigBase {
    
    public FileSpreadConfig(Collection<?> object) {
        super(object);
    }
    
    @Override
    public IConfigFileLoader<?, ?> getLoader() {
        return IConfigFileLoader.SOLID;
    }
    
}
