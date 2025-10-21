package it.hurts.shatterbyte.shatterlib.module.config;

import com.google.gson.Gson;
import com.mojang.blaze3d.vertex.PoseStack;
import de.marhali.json5.*;
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
        if (!ConfigManager.getRegisteredPaths().contains(this.getPath())) {
            throw new RuntimeException("Tried to load an unregistered config: "+this.getPath());
        }

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

    private String addInlineComments(String out) {
        Field[] fields = this.getClass().getDeclaredFields();
        for (Field field : fields) {
            String inlineComment = "";
            String name = field.getName();

            if (field.isAnnotationPresent(SimpleProp.class)) {
                SimpleProp ann = field.getAnnotation(SimpleProp.class);
                inlineComment = ann.inlineComment();
            } else if (field.isAnnotationPresent(RangeProp.class)) {
                RangeProp ann = field.getAnnotation(RangeProp.class);
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
        if (!ConfigManager.getRegisteredPaths().contains(this.getPath())) {
            throw new RuntimeException("Tried to save an unregistered config: "+this.getPath());
        }

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
        String normalized = json.replace("\r\n", "\n");

        // capture leading indentation (group 1) and the comment itself (group 2)
        Pattern p = Pattern.compile("(?ms)(^\\s*)(/\\*.*?\\*/|//.*$)");
        Matcher m = p.matcher(normalized);
        StringBuffer sb = new StringBuffer();

        while (m.find()) {
            String indent = m.group(1);    // the whitespace at the start of the comment line
            String comment = m.group(2);   // the actual comment (block or line)

            int start = m.start(2); // start index of the comment itself in normalized
            // find last non-whitespace character before this comment start
            int prev = start - 1;
            while (prev >= 0 && Character.isWhitespace(normalized.charAt(prev))) prev--;

            boolean shouldInsert = true;

            if (prev < 0) {
                // comment is at very start of file -> don't insert blank line
                shouldInsert = false;
            } else {
                char last = normalized.charAt(prev);
                if (last == '{') {
                    // comment directly after opening brace -> don't insert
                    shouldInsert = false;
                } else {
                    // check whether there's already an empty line between last non-whitespace and this comment
                    String between = normalized.substring(prev + 1, start);
                    if (Pattern.compile("\\n\\s*\\n").matcher(between).find()) {
                        shouldInsert = false; // already has a blank line
                    }
                }
            }

            // build replacement: optionally add a single blank line, then the original indentation + comment
            String replacement = (shouldInsert ? "\n" : "") + indent + Matcher.quoteReplacement(comment);
            m.appendReplacement(sb, replacement);
        }
        m.appendTail(sb);

        String result = sb.toString();
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
                    double min = ann.min();
                    double max = ann.max();
                    double val;
                    if (obj.has(name)) {
                        Json5Element e = obj.get(name);
                        val = e.getAsJson5Primitive().getAsNumber().doubleValue();
                    } else {
                        val = field.getDouble(this); // default
                    }

                    if (ann.clamp() && val < min || val > max) {
                        val = Math.max(min, Math.min(max, val)); // clamp
                    }

                    field.setDouble(this, val);
                }
            } catch (IllegalAccessException | IOException ex) {
                throw new RuntimeException(ex);
            }
        }
    }

    private void writeToJson(Json5Object obj) {
        Field[] fields = this.getClass().getDeclaredFields();
        ShatterConfig defaultInstance = ConfigManager.getDefaultInstance(this);
        for (Field field : fields) {
            field.setAccessible(true);
            String name = field.getName();

            try {
                Object value = field.get(this);
                Object defaultValue = field.get(defaultInstance);
                Json5Element elem = toJson5Element(value);
                Json5Element defaultElem = toJson5Element(defaultValue);

                if (field.isAnnotationPresent(SimpleProp.class)) {
                    SimpleProp ann = field.getAnnotation(SimpleProp.class);
                    String comment = "";
                    if (!ann.comment().isEmpty()) {
                        comment = ann.comment();
                    }

                    if (defaultElem instanceof Json5Primitive || defaultElem instanceof Json5Array) {
                        comment += "\n\nDefault: " + defaultElem.getAsString();
                    }
                    elem.setComment(comment);

                } else if (field.isAnnotationPresent(RangeProp.class)) {
                    RangeProp ann = field.getAnnotation(RangeProp.class);
                    String comment = "";
                    if (!ann.comment().isEmpty()) {
                        comment = ann.comment();
                    }

                    if (defaultElem instanceof Json5Primitive || defaultElem instanceof Json5Array) {
                        comment += "\n\nDefault: " + defaultElem.getAsString();
                    }

                    comment += "\n";

                    String lowerRange = ann.min() == Float.NEGATIVE_INFINITY ? "-∞" : String.format(ann.stringFormat(), ann.min());
                    String upperRange = ann.max() == Float.POSITIVE_INFINITY ? "+∞" : String.format(ann.stringFormat(), ann.max());
                    comment += "Range: "+lowerRange+" -> "+upperRange;

                    elem.setComment(comment);
                }

                obj.add(name, elem);
            } catch (IllegalAccessException ex) {
                throw new RuntimeException(ex);
            }
        }
    }

    public abstract String getPath();
}
