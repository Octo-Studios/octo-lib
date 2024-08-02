package it.hurts.octostudios.octolib.modules.config.cfgbuilder;

import org.yaml.snakeyaml.nodes.Tag;

import java.util.HashMap;
import java.util.Objects;

public class CfgTag {
    
    public static final String PREFIX = "tag:yaml.org,2002:";
    public static final String ENUM_POSTFIX = ".enum";
    public static final CfgTag YAML;
    public static final CfgTag MERGE;
    public static final CfgTag SET;
    public static final CfgTag PAIRS;
    public static final CfgTag OMAP;
    public static final CfgTag BINARY;
    public static final CfgTag INT;
    public static final CfgTag FLOAT;
    public static final CfgTag TIMESTAMP;
    public static final CfgTag BOOL;
    public static final CfgTag NULL;
    public static final CfgTag STR;
    public static final CfgTag SEQ;
    public static final CfgTag MAP;
    public static final CfgTag ENUM;
    
    public static final HashMap<Tag, CfgTag> STANDART_TAGS = new HashMap<>();
    
    static {
        Tag.standardTags.forEach(tag -> STANDART_TAGS.put(tag, new CfgTag(tag)));
        YAML = STANDART_TAGS.get(Tag.YAML);
        MERGE = STANDART_TAGS.get(Tag.MERGE);
        SET = STANDART_TAGS.get(Tag.SET);
        PAIRS = STANDART_TAGS.get(Tag.PAIRS);
        OMAP = STANDART_TAGS.get(Tag.OMAP);
        BINARY = STANDART_TAGS.get(Tag.BINARY);
        INT = STANDART_TAGS.get(Tag.INT);
        FLOAT = STANDART_TAGS.get(Tag.FLOAT);
        TIMESTAMP = STANDART_TAGS.get(Tag.TIMESTAMP);
        BOOL = STANDART_TAGS.get(Tag.BOOL);
        NULL = STANDART_TAGS.get(Tag.NULL);
        STR = STANDART_TAGS.get(Tag.STR);
        SEQ = STANDART_TAGS.get(Tag.SEQ);
        MAP = STANDART_TAGS.get(Tag.MAP);
        ENUM = new CfgTag(Enum.class);
    }
    
    private final Tag tag;
    
    private CfgTag(Tag tag) {
        this.tag = tag;
    }
    
    public CfgTag(String name) {
        this.tag = new Tag(name);
    }
    
    public CfgTag(Class<?> clazz) {
        this.tag = new Tag(clazz);
    }
    
    public static CfgTag by(Tag tag) {
        return STANDART_TAGS.containsKey(tag) ? STANDART_TAGS.get(tag) : new CfgTag(tag);
    }
    
    public Tag yamlTag() {
        return tag;
    }
    
    public boolean isSecondary() {
        return tag.isSecondary();
    }
    
    public String getValue() {
        return tag.getValue();
    }
    
    public boolean startsWith(String prefix) {
        return tag.startsWith(prefix);
    }
    
    public String getClassName() {
        return tag.getClassName();
    }
    
    public boolean isCompatible(Class<?> clazz) {
        return tag.isCompatible(clazz);
    }
    
    public boolean matches(Class<?> clazz) {
        return tag.matches(clazz);
    }
    
    public boolean isCustomGlobal() {
        return tag.isCustomGlobal();
    }
    
    @Override
    public String toString() {
        return tag.toString();
    }
    
    @Override
    public int hashCode() {
        return tag.hashCode();
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof CfgTag cfgTag)) return false;
        
        return Objects.equals(tag, cfgTag.tag);
    }
    
}
