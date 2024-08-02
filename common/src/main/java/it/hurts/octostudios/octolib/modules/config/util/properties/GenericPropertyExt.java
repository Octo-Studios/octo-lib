package it.hurts.octostudios.octolib.modules.config.util.properties;

import org.yaml.snakeyaml.introspector.GenericProperty;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.List;

public class GenericPropertyExt extends GenericProperty {
    
    private String inlineComment;
    private String blockComment;
    
    public GenericPropertyExt(String name, Class<?> aClass, Type aType) {
        super(name, aClass, aType);
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
