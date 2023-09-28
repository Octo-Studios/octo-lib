package it.hurts.sskirillss.octolib.config.utils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class ConfigUtils {
    public static final Gson SERIALIZER = new GsonBuilder()
            .setPrettyPrinting()
            .disableHtmlEscaping()
            .create();
}