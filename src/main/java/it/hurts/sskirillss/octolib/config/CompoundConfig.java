package it.hurts.sskirillss.octolib.config;

import it.hurts.sskirillss.octolib.config.cfgbuilder.CompoundEntry;

public abstract class CompoundConfig implements OctoConfig {
    
    private FileSpreadType spreadType = FileSpreadType.SOLID_OBJECT;
    
    public CompoundConfig() {}
    
    public CompoundConfig(FileSpreadType spreadType) {
        this.spreadType = spreadType;
    }
    
    @Override
    public Object prepareData() {
        CompoundEntry compoundConfig = new CompoundEntry();
        write(compoundConfig);
        return compoundConfig;
    }
    
    @Override
    public void onLoadObject(Object object) {
        CompoundEntry entry = (CompoundEntry) object;
        read(entry);
    }
    
    public abstract void write(CompoundEntry compound);
    
    public abstract void read(CompoundEntry compound);
    
    @Override
    public FileSpreadType getSpreadType() {
        return spreadType;
    }
    
}
