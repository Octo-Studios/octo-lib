package it.hurts.sskirillss.octolib.config;

public interface OctoConfig {
    
    Object prepareData();
    
    void onLoadObject(Object object);
    
    default FileSpreadType getSpreadType() {
        return FileSpreadType.SOLID_OBJECT;
    }
    
}
