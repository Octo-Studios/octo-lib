package it.hurts.shatterbyte.shatterlib.module.config.annotation.registration;

public interface ConfigRegistration {

    default String getCommonDir() {
        return "";
    }

}
