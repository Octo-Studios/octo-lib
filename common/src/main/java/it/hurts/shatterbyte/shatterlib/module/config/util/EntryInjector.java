package it.hurts.shatterbyte.shatterlib.module.config.util;

import it.hurts.shatterbyte.shatterlib.module.config.cfgbuilder.ConfigEntry;

import java.util.function.BinaryOperator;

public interface EntryInjector<T extends ConfigEntry> extends BinaryOperator<T> {

}
