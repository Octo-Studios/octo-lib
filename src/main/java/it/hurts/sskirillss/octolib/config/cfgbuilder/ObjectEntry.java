package it.hurts.sskirillss.octolib.config.cfgbuilder;

public class ObjectEntry extends ConfigEntry {
    
    Object object;
    
    public ObjectEntry(Object object) {
        this(new CfgTag(object.getClass()), object);
    }
    
    public ObjectEntry(CfgTag type, Object object) {
        super(type, EntryId.OBJECT);
        this.object = object;
    }
    
    @Override
    public Object getData() {
        return object;
    }
    
    @Override
    public String toString() {
        return "ObjectEntry!!" + getTag() + ">" + getData();
    }
    
}
