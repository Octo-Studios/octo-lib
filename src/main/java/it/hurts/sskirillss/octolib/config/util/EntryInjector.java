package it.hurts.sskirillss.octolib.config.util;

import it.hurts.sskirillss.octolib.config.cfgbuilder.ConfigEntry;

import java.util.function.BinaryOperator;

public interface EntryInjector<T extends ConfigEntry> extends BinaryOperator<T> {

}
