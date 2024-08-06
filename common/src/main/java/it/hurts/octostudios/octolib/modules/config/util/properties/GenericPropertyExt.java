package it.hurts.octostudios.octolib.modules.config.util.properties;

import org.yaml.snakeyaml.introspector.GenericProperty;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.List;

public class GenericPropertyExt extends GenericProperty {
    
    private String inlineComment;
    private String blockComment;
    private Type genTypeOverride;
    
    public GenericPropertyExt(String name, Class<?> aClass, Type aType) {
        super(name, aClass, aType);
    }
    
    public Type getGenTypeOverride() {
        return genTypeOverride;
    }
    
    public void setGenTypeOverride(Type genTypeOverride) {
        this.genTypeOverride = genTypeOverride;
    }
    
    @Override
    public Class<?>[] getActualTypeArguments() {
        if (genTypeOverride != null) {
            Class<?> classType = (Class<?>) genTypeOverride;
            if (classType.isArray()) {
                var actualClasses = new Class<?>[1];
                actualClasses[0] = getType().getComponentType();
                return actualClasses;
            }
        }
        
        return super.getActualTypeArguments();
    }
    
    @Override
    public void set(Object o, Object o1) throws Exception {

    }
    
    @Override
    public Object get(Object o) {
        return null;
    }
    
    @Override
    public List<Annotation> getAnnotations() {
        return null;
    }
    
    @Override
    public <A extends Annotation> A getAnnotation(Class<A> aClass) {
        return null;
    }
    
    public String getInlineComment() {
        return inlineComment;
    }
    
    public void setInlineComment(String inlineComment) {
        this.inlineComment = inlineComment;
    }
    
    public String getBlockComment() {
        return blockComment;
    }
    
    public void setBlockComment(String blockComment) {
        this.blockComment = blockComment;
    }
    
}
