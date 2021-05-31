package com.massivecraft.factions.util;

import org.bukkit.Material;

public abstract class MaterialHelper {

	public static Material getMaterial(String name) {
		return Material.valueOf(name.toUpperCase());
	}

}
