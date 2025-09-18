package it.hurts.shatterbyte.shatterlib.module.config.cfgbuilder;

public class ListEntry extends ConfigEntry {
    
    public ListEntry() {
        super(CfgTag.SEQ, EntryId.SEQUENCE);
    }
    
    @Override
    public Object getData() {
        return null;
    }
    
}
