package com.massivecraft.factions.util;

import com.massivecraft.factions.FactionsPlugin;

import java.io.File;
import java.lang.reflect.Type;
import java.util.logging.Level;

// TODO: Give better name and place to differentiate from the entity-orm-ish system in "com.massivecraft.core.persist".

public class Persist {

	private final FactionsPlugin plugin;

	public Persist(FactionsPlugin plugin) {
		this.plugin = plugin;
	}

	// ------------------------------------------------------------ //
	// GET NAME - What should we call this type of object?
	// ------------------------------------------------------------ //

	public static String getName(Class<?> clazz) {
		return clazz.getSimpleName().toLowerCase();
	}

	public static String getName(Object o) {
		return getName(o.getClass());
	}

	public static String getName(Type type) {
		return getName(type.getClass());
	}

	// ------------------------------------------------------------ //
	// GET FILE - In which file would we like to store this object?
	// ------------------------------------------------------------ //

	public File getFile(String name) {
		return new File(plugin.getDataFolder(), name + ".json");
	}

	public File getFile(Class<?> clazz) {
		return getFile(getName(clazz));
	}

	public File getFile(Object obj) {
		return getFile(getName(obj));
	}

	// SAVE

	public boolean save(Object instance) {
		return save(instance, getFile(instance));
	}

	public boolean save(Object instance, String name) {
		return save(instance, getFile(name));
	}

	public boolean save(Object instance, File file) {
		return DiscUtil.writeCatch(file, plugin.gson.toJson(instance), true);
	}

	// LOAD BY TYPE
	@SuppressWarnings("unchecked")
	public <T> T load(Type typeOfT, String name) {
		return load(typeOfT, getFile(name));
	}

	@SuppressWarnings("unchecked")
	public <T> T load(Type typeOfT, File file) {
		String content = DiscUtil.readCatch(file);
		if(content == null) {
			return null;
		}

		try {
			return plugin.gson.fromJson(content, typeOfT);
		} catch(Exception ex) {    // output the error message rather than full stack trace; error parsing the file, most likely
			plugin.log(Level.WARNING, ex.getMessage());
		}

		return null;
	}
}
