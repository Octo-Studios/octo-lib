package it.hurts.sskirillss.octolib.config.util;

import it.hurts.sskirillss.octolib.config.cfgbuilder.ArrayEntry;
import it.hurts.sskirillss.octolib.config.cfgbuilder.CompoundEntry;
import it.hurts.sskirillss.octolib.config.cfgbuilder.ConfigEntry;
import it.hurts.sskirillss.octolib.config.cfgbuilder.DeconstructedObjectEntry;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.LoaderOptions;
import org.yaml.snakeyaml.error.YAMLException;
import org.yaml.snakeyaml.nodes.MappingNode;
import org.yaml.snakeyaml.nodes.NodeId;
import org.yaml.snakeyaml.nodes.Tag;
import org.yaml.snakeyaml.representer.Representer;

public class ConfigEntryConverter {
    
    private final Representer representer;
    
    private final ConstructorExt constructor;
    
    public ConfigEntryConverter(ConstructorExt constructor, Representer representer) {
        this(constructor, representer, initDumperOptions(representer));
    }
    
    private static DumperOptions initDumperOptions(Representer representer) {
        DumperOptions dumperOptions = new DumperOptions();
        dumperOptions.setDefaultFlowStyle(representer.getDefaultFlowStyle());
        dumperOptions.setDefaultScalarStyle(representer.getDefaultScalarStyle());
        dumperOptions.setAllowReadOnlyProperties(representer.getPropertyUtils().isAllowReadOnlyProperties());
        dumperOptions.setTimeZone(representer.getTimeZone());
        return dumperOptions;
    }
    
    public ConfigEntryConverter(ConstructorExt constructor, Representer representer, DumperOptions dumperOptions) {
        this(constructor, representer, dumperOptions, constructor.getLoadingConfig());
    }
    
    public ConfigEntryConverter(ConstructorExt constructor, Representer representer, DumperOptions dumperOptions, LoaderOptions loadingConfig) {
        if (constructor == null) {
            throw new NullPointerException("Constructor must be provided");
        } else if (representer == null) {
            throw new NullPointerException("Representer must be provided");
        } else if (dumperOptions == null) {
            throw new NullPointerException("DumperOptions must be provided");
        } else if (loadingConfig == null) {
            throw new NullPointerException("LoaderOptions must be provided");
        } else {
            if (!constructor.isExplicitPropertyUtils()) {
                constructor.setPropertyUtils(representer.getPropertyUtils());
            } else if (!representer.isExplicitPropertyUtils()) {
                representer.setPropertyUtils(constructor.getPropertyUtils());
            }
            
            this.constructor = constructor;
            this.constructor.setAllowDuplicateKeys(loadingConfig.isAllowDuplicateKeys());
            this.constructor.setWrappedToRootException(loadingConfig.isWrappedToRootException());
            if (!dumperOptions.getIndentWithIndicator() && dumperOptions.getIndent() <= dumperOptions.getIndicatorIndent()) {
                throw new YAMLException("Indicator indent must be smaller then indent.");
            } else {
                representer.setDefaultFlowStyle(dumperOptions.getDefaultFlowStyle());
                representer.setDefaultScalarStyle(dumperOptions.getDefaultScalarStyle());
                representer.getPropertyUtils().setAllowReadOnlyProperties(dumperOptions.isAllowReadOnlyProperties());
                representer.setTimeZone(dumperOptions.getTimeZone());
                this.representer = representer;
            }
        }
    }
    
    public ConfigEntry representDeconstructed(Object obj) {
        if (obj == null)
            throw new NullPointerException("Represented object cannot be null.");
        
        var node = representer.represent(obj);
        
        if (node.getNodeId() == NodeId.scalar)
            throw new NullPointerException("Represented object cannot be a scalar.");
        else if (node.getNodeId() == NodeId.sequence)
            node.setType(ArrayEntry.class);
        else
            node.setType(DeconstructedObjectEntry.class);
        return (ConfigEntry) constructor.constructObject(node);
    }
    
    public ConfigEntry represent(Object obj) {
        if (obj == null)
            throw new NullPointerException("Represented object cannot be null.");
        
        var node = representer.represent(obj);
        
        if (node.getNodeId() == NodeId.scalar)
            throw new NullPointerException("Represented object cannot be a scalar.");
        else if (node.getNodeId() == NodeId.sequence)
            node.setType(ArrayEntry.class);
        else
            node.setTag(CompoundEntry.COMPOUND_CFG_TAG.yamlTag());
        return (ConfigEntry) constructor.constructObject(node);
    }
    
    public <T> T constructAs(ConfigEntry compound, Class<T> type) {
        MappingNode node = (MappingNode) representer.represent(compound);
        node.setTag(Tag.MAP);
        node.setType(type);
        return (T) constructor.constructObject(node);
    }
    
    public <T> T construct(ConfigEntry compound) {
        MappingNode node = (MappingNode) representer.represent(compound);
        return (T) constructor.constructObject(node);
    }
    
}
