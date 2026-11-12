package it.hurts.octostudios.octolib.module.config.annotation.registration;

public interface ConfigRegistration {

    default String getCommonDir() {
        return "";
    }

}
