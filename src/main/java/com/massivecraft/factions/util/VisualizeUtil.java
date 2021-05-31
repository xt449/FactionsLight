package com.massivecraft.factions.util;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

import java.util.*;

public abstract class VisualizeUtil {

	protected static final Map<UUID, Set<Location>> playerLocations = new HashMap<>();

	public static Set<Location> getPlayerLocations(Player player) {
		return getPlayerLocations(player.getUniqueId());
	}

	public static Set<Location> getPlayerLocations(UUID uuid) {
		return playerLocations.computeIfAbsent(uuid, k -> new HashSet<>());
	}

	@SuppressWarnings("deprecation")
	public static void clear(Player player) {
		Set<Location> locations = getPlayerLocations(player);
		if(locations == null) {
			return;
		}
		for(Location location : locations) {
			Block block = location.getWorld().getBlockAt(location);
			player.sendBlockChange(location, block.getType(), block.getData());
		}
		locations.clear();
	}

}
