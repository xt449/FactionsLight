package com.massivecraft.factions.util.material;

import org.bukkit.Material;

public abstract class MaterialHelper {

	public static Material get(String name) {
		return Material.valueOf(name.toUpperCase());
	}

	public static Material get(String name, Material defaultMaterial) {
		try {
			return Material.valueOf(name.toUpperCase());
		} catch(IllegalArgumentException ignored) {
			return defaultMaterial;
		}
	}
}
