package com.massivecraft.factions.util;

import com.massivecraft.factions.FactionsPlugin;
import org.bukkit.World;

import java.util.HashSet;

public class WorldUtil {
	private HashSet<String> worlds;
	private final boolean check;
	private boolean whitelist;

	public WorldUtil(FactionsPlugin plugin) {
		check = plugin.configMain.restrictWorlds().isRestrictWorlds();
		if(!check) {
			return;
		}
		worlds = new HashSet<>(plugin.configMain.restrictWorlds().getWorldList());
		whitelist = plugin.configMain.restrictWorlds().isWhitelist();
	}

	public boolean isEnabled(World world) {
		if(!check) {
			return true;
		}
		return whitelist == worlds.contains(world.getName());
	}
}
