package it.hurts.octostudios.octolib.modules.config.util;

import it.hurts.octostudios.octolib.modules.config.annotations.IgnoreProp;
import it.hurts.octostudios.octolib.modules.config.annotations.ParameterizedProp;
import it.hurts.octostudios.octolib.modules.config.annotations.Prop;
import it.hurts.octostudios.octolib.modules.config.annotations.TypePropInherited;
import it.hurts.octostudios.octolib.modules.config.util.properties.FieldPropertyExt;
import it.hurts.octostudios.octolib.modules.config.util.properties.GenericPropertyExt;
import it.hurts.octostudios.octolib.modules.config.util.properties.MethodPropertyExt;
import org.yaml.snakeyaml.error.YAMLException;
import org.yaml.snakeyaml.introspector.BeanAccess;
import org.yaml.snakeyaml.introspector.Property;
import org.yaml.snakeyaml.introspector.PropertyUtils;

import java.beans.FeatureDescriptor;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

public class PropertyUtilsExt extends PropertyUtils {
    
    Map<Class<?>, Map<String, Property>> propertiesCache = new HashMap<>();
    
    @Override
    protected Map<String, Property> getPropertiesMap(Class<?> type, BeanAccess bAccess) {
        if (this.propertiesCache.containsKey(type)) {
            return this.propertiesCache.get(type);
        } else {
            Map<String, Property> properties = new LinkedHashMap<>();
            
            Class<?> c;
            Field[] fields;
            Field field;
            int modifiers;
            
            boolean onlyProps = type.isAnnotationPresent(TypePropInherited.class)
                    && type.getAnnotation(TypePropInherited.class).onlyProps();
            
            if (bAccess == BeanAccess.FIELD) {
                for (c = type; c != null; c = c.getSuperclass()) {
                    fields = c.getDeclaredFields();
                    
                    for (Field value : fields) {
                        field = value;
                        
                        if (field.isSynthetic() || field.isEnumConstant() || field.isAnnotationPresent(IgnoreProp.class))
                            continue;
                        
                        modifiers = field.getModifiers();
                        
                        String name = field.getName();
                        String inlineComment = null;
                        String blockComment = null;
                        
                        if (field.isAnnotationPresent(Prop.class)) {
                            var settings = field.getAnnotation(Prop.class);
                            
                            name = settings.name() == null || settings.name().isEmpty() ? name : settings.name();
                            inlineComment = settings.inlineComment();
                            blockComment = settings.comment();
                        } else if (onlyProps)
                            continue;
                        
                        if (!Modifier.isStatic(modifiers) && !Modifier.isTransient(modifiers) && !properties.containsKey(name)) {
                            field.setAccessible(true);
                            var property = new FieldPropertyExt(field, name);
                            if (inlineComment != null && !inlineComment.isEmpty())
                                property.setInlineComment(inlineComment);
                            if (blockComment != null && !blockComment.isEmpty())
                                property.setBlockComment(blockComment);
                            
                            if (field.isAnnotationPresent(ParameterizedProp.class))
                                property.setGenTypeOverride(field.getAnnotation(ParameterizedProp.class).value());
                            
                            properties.put(name, property);
                        }
                    }
                }
            } else {
                try {
                    PropertyDescriptor[] descriptors = Introspector.getBeanInfo(type).getPropertyDescriptors();
                    
                    for (PropertyDescriptor property : descriptors) {
                        
                        Method readMethod = property.getReadMethod();
                        
                        if ((readMethod == null || !readMethod.getName().equals("getClass")) && !this.isTransient(property)) {
                            properties.put(property.getName(), new MethodPropertyExt(property));
                        }
                        
                    }
                } catch (IntrospectionException var11) {
                    throw new YAMLException(var11);
                }
                
                for (c = type; c != null; c = c.getSuperclass()) {
                    fields = c.getDeclaredFields();
                    
                    for (Field value : fields) {
                        field = value;
                        
                        if (field.isSynthetic() || field.isEnumConstant() || field.isAnnotationPresent(IgnoreProp.class)) {
                            properties.remove(field.getName());
                            continue;
                        }
                        
                        String name = field.getName();
                        String inlineComment = null;
                        String blockComment = null;
                        
                        if (field.isAnnotationPresent(Prop.class)) {
                            var settings = field.getAnnotation(Prop.class);
                            
                            name = settings.name() == null || settings.name().isEmpty() ? name : settings.name();
                            inlineComment = settings.inlineComment();
                            blockComment = settings.comment();
                            
                            if (properties.containsKey(name) && properties.get(name) instanceof GenericPropertyExt ext) {
                                if (inlineComment != null && !inlineComment.isEmpty())
                                    ext.setInlineComment(inlineComment);
                                if (blockComment != null && !blockComment.isEmpty())
                                    ext.setBlockComment(blockComment);
                                if (field.isAnnotationPresent(ParameterizedProp.class))
                                    ext.setGenTypeOverride(field.getAnnotation(ParameterizedProp.class).value());
                            }
                            
                        } else if (onlyProps) {
                            properties.remove(name);
                            continue;
                        }
                        
                        modifiers = field.getModifiers();
                        if (!Modifier.isStatic(modifiers) && !Modifier.isTransient(modifiers)) {
                            field.setAccessible(true);
                            
                            var property = new FieldPropertyExt(field, name);
                            if (inlineComment != null && !inlineComment.isEmpty())
                                property.setInlineComment(inlineComment);
                            
                            if (blockComment != null && !blockComment.isEmpty())
                                property.setBlockComment(blockComment);
                            
                            if (field.isAnnotationPresent(ParameterizedProp.class))
                                property.setGenTypeOverride(field.getAnnotation(ParameterizedProp.class).value());
                            
                            if (!properties.containsKey(name) || Modifier.isPublic(modifiers))
                                properties.put(name, property);
                        }
                    }
                }
            }
            
            if (properties.isEmpty()) {
                throw new YAMLException("No JavaBean properties found in " + type.getName());
            } else {
                this.propertiesCache.put(type, properties);
                return properties;
            }
        }
    }
    
    private boolean isTransient(FeatureDescriptor fd) {
        return Boolean.TRUE.equals(fd.getValue("transient"));
    }
    
}
