package it.hurts.octostudios.octolib.module.config.util;

import it.hurts.octostudios.octolib.module.config.cfgbuilder.ConfigEntry;

import java.util.function.BinaryOperator;

public interface EntryInjector<T extends ConfigEntry> extends BinaryOperator<T> {

}
