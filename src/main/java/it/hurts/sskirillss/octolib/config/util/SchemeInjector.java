package it.hurts.sskirillss.octolib.config.util;

import it.hurts.sskirillss.octolib.config.cfgbuilder.*;
import it.hurts.sskirillss.octolib.config.cfgbuilder.scalar.DoubleEntry;
import it.hurts.sskirillss.octolib.config.cfgbuilder.scalar.IntEntry;
import it.hurts.sskirillss.octolib.config.cfgbuilder.scalar.ScalarEntry;
import it.hurts.sskirillss.octolib.config.cfgbuilder.scalar.StringEntry;

public class SchemeInjector implements EntryInjector<ConfigEntry> {
    
    @Override
    public ConfigEntry apply(ConfigEntry pattern, ConfigEntry target) {
        if (pattern == null || pattern.getNodeId() != target.getNodeId())
            return pattern;
        
        try {
            return setupComments(pattern, pattern.refine(switch (pattern.getNodeId()) {
                case SCALAR -> injectScalar((ScalarEntry) pattern, (ScalarEntry) target);
                case SEQUENCE -> injectSeq((ArrayEntry) pattern, (ArrayEntry) target);
                case MAPPING -> injectMapping((CompoundEntry) pattern, (CompoundEntry) target);
                case OBJECT -> injectObject((ObjectEntry) pattern, (CompoundEntry) target);
                case ANCHOR -> throw new UnsupportedOperationException();
            }));
        } catch (ClassCastException exception) {
            throw new RuntimeException(String.format("Illegal entry type: %s or %s cannot be parsed.",
                    pattern.getClass().getSimpleName(), target.getClass().getSimpleName()), exception);
        }
    }
    
    protected ConfigEntry setupComments(ConfigEntry pattern, ConfigEntry target) {
        target.setBlockComment(pattern.getBlockComment());
        target.setInlineComment(pattern.getInlineComment());
        return target;
    }
    
    protected CompoundEntry injectMapping(CompoundEntry pattern, CompoundEntry target) {
        CompoundEntry result = pattern.getTag() == CfgTag.MAP ? target
                : new DeconstructedObjectEntry(pattern.getTag());
    
        for (var entry : pattern.entrySet()) {
            var key = entry.getKey();
            var value = entry.getValue();
        
            var value1 = target.get(key);
            if (value1 == null)
                continue;
            
            if (value1.getNodeId() != value.getNodeId()) {
                result.put(key, value);
                continue;
            }
        
            try {
                result.put(key, apply(value, value1));
            } catch (ClassCastException exception) {
                throw new RuntimeException(String.format("Illegal entry type: %s or %s cannot be parsed.",
                        value.getClass().getSimpleName(), value1.getClass().getSimpleName()), exception);
            }
        }
        
        return result;
    }
    
    protected DeconstructedObjectEntry injectObject(ObjectEntry pattern, CompoundEntry target) {
        return new DeconstructedObjectEntry(pattern.getTag(), target);
    }
    
    protected ArrayEntry injectSeq(ArrayEntry pattern, ArrayEntry target) {
        var patternTag = pattern.getType();
        target.setType(patternTag);
    
        var iterator = target.iterator();
        while (iterator.hasNext()) {
            var next = iterator.next();
            next = tryParse(next, patternTag);
            
            if (next == null)
                iterator.remove();
        }
        
        return target.isEmpty() ? pattern : target;
    }
    
    protected ConfigEntry tryParse(ConfigEntry object, CfgTag pattern) {
        if (pattern == object.getTag())
            return object;
        
        if (pattern == CfgTag.FLOAT && object.getTag() == CfgTag.INT)
            return new DoubleEntry((int) object.getData());
        
        if (object.getNodeId() == EntryId.SCALAR && pattern == CfgTag.STR)
            return new StringEntry(object.getData().toString());
    
        if (pattern == CfgTag.INT && object.getTag() == CfgTag.STR)
            try {
                return new IntEntry(Integer.parseInt((String) object.getData()));
            } catch (NumberFormatException exception) {
                return null;
            }
        
        if (pattern == CfgTag.ENUM && object.getTag() == CfgTag.STR)
            return object;
        
        if (pattern == CfgTag.FLOAT && object.getTag() == CfgTag.STR)
            try {
                return new DoubleEntry(Double.parseDouble((String) object.getData()));
            } catch (NumberFormatException exception) {
                return null;
            }
        
        return null;
    }
    
    protected ScalarEntry injectScalar(ScalarEntry pattern, ScalarEntry target) {
        var result = tryParse(target, pattern.getTag());
        return result == null ? pattern : (ScalarEntry) result;
    }
    
}
