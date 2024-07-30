package it.hurts.sskirillss.octolib.config.cfgbuilder;

public class DeconstructedObjectEntry extends CompoundEntry {
    
    public static final CfgTag DECONSTRUCTED_CFG_TAG = new CfgTag(DeconstructedObjectEntry.class);
    
    public DeconstructedObjectEntry(CfgTag tag, CompoundEntry compoundEntry) {
        super(tag);
        map.putAll(compoundEntry.map);
    }
    
}
