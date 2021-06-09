package com.massivecraft.factions.configuration;

import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.io.InputStreamReader;

/**
 * @author Jonathan Talcott (xt449 / BinaryBanana)
 */
public abstract class AbstractConfiguration {

	protected final String filePath;
	private File file;

	protected final Plugin plugin;
	protected YamlConfiguration config;

	protected AbstractConfiguration(Plugin plugin, String filePath) {
		this.plugin = plugin;
		this.filePath = filePath;
	}

	protected void writeDefaults() {
		try {
			config.setDefaults(YamlConfiguration.loadConfiguration(new InputStreamReader(plugin.getResource(filePath))));
		} catch(Exception exc) {
			plugin.getLogger().warning("Unable to load defaults for configuration file '" + filePath + "' from '" + plugin.getName() + "'!");
		}
	}

	protected void readValues() {

	}

	public final void initialize() {
		file = new File(plugin.getDataFolder(), filePath);
		config = YamlConfiguration.loadConfiguration(file);

		// File Setup:
		if(!file.exists()) {
			try {
				if(!file.createNewFile()) {
					throw new Exception();
				}
			} catch(Exception exc) {
				plugin.getLogger().warning("Unable to create configuration file '" + filePath + "'!");
			}
		}

		// Config Setup:
		config.options().copyDefaults(true);
		config.options().copyHeader(false);

		// write
		writeDefaults();

		// read
		readValues();

		// This configuration save is only important for the first plugin
		// load or any paths removed by the user or added in a new version
		save();
	}

	protected void save() {
		try {
			config.save(file);
		} catch(Exception exc) {
			plugin.getLogger().warning("Unable to save configuration file '" + filePath + "'!");
		}
	}
}
