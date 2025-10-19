package it.hurts.shatterbyte.shatterlib.module.config;

import com.google.gson.Gson;
import de.marhali.json5.Json5;
import de.marhali.json5.Json5Element;
import de.marhali.json5.Json5Object;
import de.marhali.json5.config.DigitSeparatorStrategy;
import it.hurts.shatterbyte.shatterlib.module.config.annotation.RangeProp;
import it.hurts.shatterbyte.shatterlib.module.config.annotation.SimpleProp;

import java.io.*;
import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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

    private String addInlineComments(String out) throws IOException {
        Field[] fields = this.getClass().getDeclaredFields();
        for (Field field : fields) {
            String inlineComment = "";
            String name = field.getName();

            if (field.isAnnotationPresent(RangeProp.class)) {
                RangeProp ann = field.getAnnotation(RangeProp.class);
                inlineComment = ann.min() + " - " + ann.max();
            } else if (field.isAnnotationPresent(SimpleProp.class)) {
                SimpleProp ann = field.getAnnotation(SimpleProp.class);
                inlineComment = ann.inlineComment();
            }

            if (inlineComment.isEmpty()) {
                continue;
            }

            Pattern p = Pattern.compile("(?m)(^\\s*"
                    + Pattern.quote(name)
                    + "\\s*:\\s*)([^\\n,]+)(,?)");
            Matcher m = p.matcher(out);
            StringBuffer sb = new StringBuffer();
            boolean found = false;
            while (m.find()) {
                found = true;
                String before = m.group(1);    // "  testRange: "
                String value = m.group(2);     // "75.0"
                String trailingComma = m.group(3); // maybe ","
                String replacement = before + value + trailingComma + " // " + inlineComment;
                m.appendReplacement(sb, Matcher.quoteReplacement(replacement));
            }
            m.appendTail(sb);

            if (found) {
                out = sb.toString();
            }
        }

        return out;
    }

    public void save(Path path) throws IOException {
        Json5Object obj = new Json5Object();
        writeToJson(obj);

        StringWriter sw = new StringWriter();
        JSON5.serialize(obj, sw);
        String json = sw.toString();

        json = this.addNewLinesBeforeComments(json);
        json = this.addInlineComments(json);

        Files.createDirectories(path.getParent() == null ? Path.of(".") : path.getParent());
        try (Writer fileWriter = Files.newBufferedWriter(path, StandardCharsets.UTF_8)) {
            fileWriter.write(json);
        }
    }

    private String addNewLinesBeforeComments(String json) {
        if (json == null || json.isEmpty()) return json;

        // preserve original CRLF style if present
        boolean hadCRLF = json.contains("\r\n");
        // normalize to \n for easier processing
        String normalized = json.replace("\r\n", "\n");

        Pattern p = Pattern.compile("(?m)^\\s*//"); // match comment lines (start of line, optional indent, then //)
        Matcher m = p.matcher(normalized);
        StringBuffer sb = new StringBuffer();

        while (m.find()) {
            int start = m.start();

            // find last non-whitespace character before this comment line
            int prev = start - 1;
            while (prev >= 0 && Character.isWhitespace(normalized.charAt(prev))) prev--;

            boolean shouldInsert = true;

            if (prev < 0) {
                // comment is at very start of file -> don't insert blank line
                shouldInsert = false;
            } else {
                char last = normalized.charAt(prev);
                if (last == '{') {
                    // comment directly after opening brace (like "{\n  // ...") -> don't insert
                    shouldInsert = false;
                } else {
                    // check whether there's already an empty line between last non-whitespace and this comment
                    String between = normalized.substring(prev + 1, start);
                    if (between.contains("\n\n")) {
                        shouldInsert = false; // already has a blank line
                    }
                }
            }

            String replacement = (shouldInsert ? "\n" : "") + m.group();
            m.appendReplacement(sb, Matcher.quoteReplacement(replacement));
        }
        m.appendTail(sb);

        String result = sb.toString();
        // restore CRLF if original used it
        if (hadCRLF) result = result.replace("\n", "\r\n");
        return result;
    }


    private Json5Element toJson5Element(Object value) {
        String json = GSON.toJson(value);
        try {
            return JSON5.parse(json);
        } catch (Exception e) {
            String wrapped = "{\"_\": " + json + "}";
            Json5Element parsed = JSON5.parse(wrapped);
            if (parsed.isJson5Object()) {
                return parsed.getAsJson5Object().get("_");
            }
            throw new RuntimeException("unable to convert value to Json5Element", e);
        }
    }

    private Object fromJson5Element(Json5Element elem, Type targetType) throws IOException {
        StringWriter sw = new StringWriter();
        JSON5.serialize(elem, sw);
        return GSON.fromJson(sw.toString(), targetType);
    }

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

                if (field.isAnnotationPresent(SimpleProp.class)) {
                    SimpleProp ann = field.getAnnotation(SimpleProp.class);
                    if (!ann.comment().isEmpty()) {
                        try {
                            elem.setComment(ann.comment());
                        } catch (Throwable t) {
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
