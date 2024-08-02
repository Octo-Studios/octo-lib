package it.hurts.octostudios.octolib.modules.config.util.properties;

import org.yaml.snakeyaml.error.YAMLException;
import org.yaml.snakeyaml.util.ArrayUtils;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.List;

public class FieldPropertyExt extends GenericPropertyExt {
    
    private final Field field;
    
    public FieldPropertyExt(Field field) {
        super(field.getName(), field.getType(), field.getGenericType());
        this.field = field;
        field.setAccessible(true);
    }
    
    public FieldPropertyExt(Field field, String name) {
        super(name, field.getType(), field.getGenericType());
        this.field = field;
        field.setAccessible(true);
    }
    
    public void set(Object object, Object value) throws Exception {
        this.field.set(object, value);
    }
    
    public Object get(Object object) {
        try {
            return this.field.get(object);
        } catch (Exception var3) {
            throw new YAMLException("Unable to access field " + this.field.getName() + " on object " + object + " : " + var3);
        }
    }
    
    public List<Annotation> getAnnotations() {
        return ArrayUtils.toUnmodifiableList(this.field.getAnnotations());
    }
    
    public <A extends Annotation> A getAnnotation(Class<A> annotationType) {
        return this.field.getAnnotation(annotationType);
    }
    
}
