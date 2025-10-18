package it.hurts.shatterbyte.shatterlib.module.config;

import com.google.gson.Gson;
import de.marhali.json5.Json5;
import de.marhali.json5.Json5Element;
import de.marhali.json5.Json5Object;
import it.hurts.shatterbyte.shatterlib.module.config.annotation.RangeProp;
import it.hurts.shatterbyte.shatterlib.module.config.annotation.SimpleProp;

import java.io.*;
import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

public abstract class ShatterConfig {
    private static final Gson GSON = new Gson();
    private static final Json5 JSON5 = Json5.builder(builder -> builder
            .quoteless()
            .writeComments()
            .prettyPrinting()
            .build());

    public void load(Path path) throws IOException {
        if (Files.exists(path)) {
            try (Reader reader = Files.newBufferedReader(path, StandardCharsets.UTF_8)) {
                Json5Element rootElem = JSON5.parse(reader);
                if (rootElem.isJson5Object()) {
                    Json5Object obj = rootElem.getAsJson5Object();
                    applyFromJson(obj);
                } else {
                    throw new IOException("Config root is not object");
                }
            }
        } else {
            // file doesn't exist: create default and save
            save(path);
        }
    }

    public void save(Path path) throws IOException {
        Json5Object obj = new Json5Object();
        writeToJson(obj);
        try (Writer writer = Files.newBufferedWriter(path, StandardCharsets.UTF_8)) {
            JSON5.serialize(obj, writer);
        }
    }

    // ---------- helpers ----------

    /**
     * convert arbitrary java object -> Json5Element in a robust way.
     * tries direct parse first; if that fails (e.g. top-level primitive issues),
     * parses a wrapped object {"_": <value>} and returns the "_" element.
     */
    private Json5Element toJson5Element(Object value) {
        // null -> use json null
        String json = GSON.toJson(value);
        try {
            return JSON5.parse(json);
        } catch (Exception e) {
            // fallback: wrap so parser always sees an object
            String wrapped = "{\"_\": " + json + "}";
            Json5Element parsed = JSON5.parse(wrapped);
            if (parsed.isJson5Object()) {
                return parsed.getAsJson5Object().get("_");
            }
            throw new RuntimeException("unable to convert value to Json5Element", e);
        }
    }

    /**
     * convert Json5Element -> Java object (using gson). uses JSON5.serialize to
     * obtain a stable json string for gson to parse.
     */
    private Object fromJson5Element(Json5Element elem, Type targetType) throws IOException {
        StringWriter sw = new StringWriter();
        JSON5.serialize(elem, sw);
        return GSON.fromJson(sw.toString(), targetType);
    }

    // ---------- reflection-based read/write ----------

    private void applyFromJson(Json5Object obj) {
        Field[] fields = this.getClass().getDeclaredFields();
        for (Field field : fields) {
            field.setAccessible(true);
            String name = field.getName();

            try {
                if (field.isAnnotationPresent(SimpleProp.class) && obj.has(name)) {
                    Json5Element e = obj.get(name);
                    Object javaVal = fromJson5Element(e, field.getType());
                    if (javaVal != null) field.set(this, javaVal);
                }

                if (field.isAnnotationPresent(RangeProp.class)) {
                    RangeProp ann = field.getAnnotation(RangeProp.class);
                    float min = ann.min();
                    float max = ann.max();
                    float val;
                    if (obj.has(name)) {
                        Json5Element e = obj.get(name);
                        val = e.getAsJson5Primitive().getAsNumber().floatValue();
                    } else {
                        val = field.getFloat(this); // default
                    }
                    if (val < min || val > max) {
                        val = Math.max(min, Math.min(max, val)); // clamp
                    }
                    field.setFloat(this, val);
                }

                // fields without annotations are left alone (or you can decide to include them)
            } catch (IllegalAccessException | IOException ex) {
                throw new RuntimeException(ex);
            }
        }
    }

    private void writeToJson(Json5Object obj) {
        Field[] fields = this.getClass().getDeclaredFields();
        for (Field field : fields) {
            field.setAccessible(true);
            String name = field.getName();

            try {
                Object value = field.get(this);
                Json5Element elem = toJson5Element(value);

                // if you have comments on the annotation, try to attach them.
                // note: json5-java may expose APIs to attach comments to elements or properties.
                // if `obj.setComment` is what worked for you before, keep it; otherwise
                // the element-level comment API might be different. adjust as needed.
                if (field.isAnnotationPresent(SimpleProp.class)) {
                    SimpleProp ann = field.getAnnotation(SimpleProp.class);
                    if (!ann.comment().isEmpty()) {
                        // best-effort: many json5 libs let you attach a comment to the element.
                        // if this call doesn't exist in your version, change to whatever API is provided.
                        try {
                            elem.setComment(ann.comment());
                        } catch (Throwable t) {
                            // fallback: caller used obj.setComment previously; keep silent if not available
                            try { obj.setComment(ann.comment()); } catch (Throwable ignored) {}
                        }
                    }
                } else if (field.isAnnotationPresent(RangeProp.class)) {
                    RangeProp ann = field.getAnnotation(RangeProp.class);
                    if (!ann.comment().isEmpty()) {
                        try {
                            elem.setComment(ann.comment());
                        } catch (Throwable t) {
                            try { obj.setComment(ann.comment()); } catch (Throwable ignored) {}
                        }
                    }
                }

                obj.add(name, elem);
            } catch (IllegalAccessException ex) {
                throw new RuntimeException(ex);
            }
        }
    }
}
