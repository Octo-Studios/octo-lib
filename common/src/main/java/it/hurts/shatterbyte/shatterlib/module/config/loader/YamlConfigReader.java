package it.hurts.shatterbyte.shatterlib.module.config.loader;

import java.io.IOException;
import java.io.StringReader;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

final class YamlConfigReader {

    private YamlConfigReader() {

    }

    static StringReader open(Path file) throws IOException {
        try {
            return new StringReader(Files.readString(file, StandardCharsets.UTF_8));
        } catch (CharacterCodingException exception) {
            var legacyCharset = Charset.defaultCharset();

            if (legacyCharset.equals(StandardCharsets.UTF_8))
                throw exception;

            return new StringReader(Files.readString(file, legacyCharset));
        }
    }
}
