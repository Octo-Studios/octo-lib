package it.hurts.sskirillss.octolib.config.util;

import it.hurts.sskirillss.octolib.config.cfgbuilder.*;
import it.hurts.sskirillss.octolib.config.cfgbuilder.scalar.DoubleEntry;
import it.hurts.sskirillss.octolib.config.cfgbuilder.scalar.IntEntry;
import it.hurts.sskirillss.octolib.config.cfgbuilder.scalar.ScalarEntry;
import it.hurts.sskirillss.octolib.config.cfgbuilder.scalar.StringEntry;

public class SchemeInjector implements EntryInjector<CompoundEntry> {
    
    @Override
    public CompoundEntry apply(CompoundEntry pattern, CompoundEntry target) {
        CompoundEntry result = pattern.getTag() == CfgTag.MAP ? target : new CompoundEntry();
        
        for (var entry : pattern) {
            var key = entry.getKey();
            var value = entry.getValue();
            
            var value1 = target.get(key);
            if (value1 == null || value1.getNodeId() != value.getNodeId()) {
                result.put(key, value);
                continue;
            }
            
            try {
                result.put(key, setupComments(value, value.refactor(switch (value.getNodeId()) {
                    case SCALAR -> injectScalar((ScalarEntry) value, (ScalarEntry) value1);
                    case SEQUENCE -> injectSeq((ArrayEntry) value, (ArrayEntry) value1);
                    case MAPPING -> {
                        if (value.getTag() != CfgTag.MAP)
                            yield new DeconstructedObjectEntry(value.getTag(),
                                    apply((CompoundEntry) value, (CompoundEntry) value1));
                        yield apply((CompoundEntry) value, (CompoundEntry) value1);
                    }
                    case OBJECT -> injectObject((ObjectEntry) value, (CompoundEntry) value1);
                    case ANCHOR -> throw new UnsupportedOperationException();
                })));
            } catch (ClassCastException exception) {
                throw new RuntimeException(String.format("Illegal entry type: %s or %s cannot be parsed.",
                        value.getClass().getSimpleName(), value1.getClass().getSimpleName()), exception);
            }
        }
    
        setupComments(pattern, result);
        return result;
    }
    
    protected ConfigEntry setupComments(ConfigEntry pattern, ConfigEntry target) {
        target.setBlockComment(pattern.getBlockComment());
        target.setInlineComment(pattern.getInlineComment());
        return target;
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
