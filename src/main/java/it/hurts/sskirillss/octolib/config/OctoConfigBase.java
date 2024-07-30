package it.hurts.sskirillss.octolib.config;

public class OctoConfigBase implements OctoConfig {
    
    Object object;
    FileSpreadType spreadType = FileSpreadType.SOLID_OBJECT;
    
    public OctoConfigBase(Object object) {
        this.object = object;
    }
    
    public OctoConfigBase(Object object, FileSpreadType spreadType) {
        this.object = object;
        this.spreadType = spreadType;
    }
    
    @Override
    public Object prepareData() {
        return object;
    }
    
    @Override
    public void onLoadObject(Object object) {}
    
    @Override
    public FileSpreadType getSpreadType() {
        return spreadType;
    }
    
}
