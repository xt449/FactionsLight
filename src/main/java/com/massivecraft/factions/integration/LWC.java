package com.massivecraft.factions.integration;

import com.griefcraft.lwc.LWCPlugin;
import com.griefcraft.model.Protection;
import com.massivecraft.factions.FactionClaim;
import com.massivecraft.factions.IFactionPlayerManager;
import com.massivecraft.factions.IFaction;
import com.massivecraft.factions.FactionsPlugin;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.plugin.Plugin;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public class LWC {
	private static com.griefcraft.lwc.LWC lwc;

	public static void setup(Plugin plugin) {
		if(!(plugin instanceof LWCPlugin)) {
			return;
		}

		lwc = ((LWCPlugin) plugin).getLWC();
		FactionsPlugin.getInstance().log("Successfully hooked into LWC!" + (FactionsPlugin.getInstance().conf().lwc().isEnabled() ? "" : " Integration is currently disabled (\"lwc.integration\")."));
	}

	public static boolean getEnabled() {
		return lwc != null && FactionsPlugin.getInstance().conf().lwc().isEnabled();
	}

	public static Plugin getLWC() {
		return lwc == null ? null : lwc.getPlugin();
	}

	public static void clearOtherLocks(FactionClaim flocation, IFaction faction) {
		Protection protection;
		for(Block block : findBlocks(flocation)) {
			if((protection = lwc.findProtection(block)) != null) {
				if(!faction.getFPlayers().contains(IFactionPlayerManager.getInstance().getByOfflinePlayer(Bukkit.getServer().getOfflinePlayer(protection.getOwner())))) {
					protection.remove();
				}
			}
		}
	}

	public static void clearAllLocks(FactionClaim flocation) {
		Protection protection;
		for(Block block : findBlocks(flocation)) {
			if((protection = lwc.findProtection(block)) != null) {
				protection.remove();
			}
		}
	}

	private static List<Block> findBlocks(FactionClaim flocation) {
		World world = Bukkit.getWorld(flocation.getWorldName());
		if(world == null) {
			return Collections.emptyList();  // world not loaded or something? cancel out to prevent error
		}
		Location location = new Location(world, flocation.getX() * 16, 5, flocation.getZ() * 16);

		BlockState[] blocks = location.getChunk().getTileEntities();
		List<Block> lwcBlocks = new LinkedList<>();

		for(BlockState block : blocks) {
			if(lwc.isProtectable(block)) {
				lwcBlocks.add(block.getBlock());
			}
		}
		return lwcBlocks;
	}
}
