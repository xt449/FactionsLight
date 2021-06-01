package com.massivecraft.factions.listeners;

import com.massivecraft.factions.*;
import com.massivecraft.factions.perms.PermissibleAction;
import com.massivecraft.factions.util.TL;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;


public abstract class AbstractListener implements Listener {
//	public boolean playerCanInteractHere(Player player, Location location) {
//		return canInteractHere(player, location);
//	}

	public static boolean canInteractHere(Player player, Location location) {
		FPlayer me = FPlayers.getInstance().getByPlayer(player);
		if(me.isAdminBypassing()) {
			return true;
		}

		FLocation loc = new FLocation(location);
		Faction otherFaction = Board.getInstance().getFactionAt(loc);

//		if(FactionsPlugin.getInstance().getLandRaidControl().isRaidable(otherFaction)) {
//			return true;
//		}

//		MainConfiguration.Factions.CommandBlacklist commandBlacklist = FactionsPlugin.getInstance().configMain.factions().commandBlacklist();
//		if(otherFaction.isWilderness()) {
//			if(!commandBlacklist.isWildernessDenyUsage() || commandBlacklist.getWorldsNoWildernessProtection().contains(location.getWorld().getName())) {
//				return true; // This is not faction territory. Use whatever you like here.
//			}
//			me.msg(TL.PLAYER_USE_WILDERNESS, "this");
//			return false;
//		}

		boolean access = otherFaction.hasAccess(me, PermissibleAction.ITEM);

		// Cancel if we are not in our own territory
		if(!access) {
			me.msg(TL.PLAYER_USE_TERRITORY, "this", otherFaction.getTag(me.getFaction()));
			return false;
		}

		return true;
	}

//	protected static void handleExplosion(Location loc, Entity boomer, Cancellable event, List<Block> blockList) {
//		if(!FactionsPlugin.getInstance().configMain.restrictWorlds().isEnabled(loc.getWorld())) {
//			return;
//		}
//
////		if(explosionDisallowed(boomer, new FLocation(loc))) {
////			event.setCancelled(true);
////			return;
////		}
//
//		List<Chunk> chunks = blockList.stream().map(Block::getChunk).distinct().collect(Collectors.toList());
//		if(chunks.removeIf(chunk -> explosionDisallowed(boomer, new FLocation(chunk)))) {
//			blockList.removeIf(block -> !chunks.contains(block.getChunk()));
//		}
//	}

//	public static boolean explosionDisallowed(Entity boomer, FLocation location) {
//		Faction faction = Board.getInstance().getFactionAt(location);
//		boolean online = faction.hasPlayersOnline();
////		if(faction.noExplosionsInTerritory() || (faction.isPeaceful() && FactionsPlugin.getInstance().configMain.factions().specialCase().isPeacefulTerritoryDisableBoom())) {
////			// faction is peaceful and has explosions set to disabled
////			return true;
////		}
//		MainConfiguration.Factions.CommandBlacklist commandBlacklist = FactionsPlugin.getInstance().configMain.factions().commandBlacklist();
//		if(boomer instanceof Creeper && ((faction.isWilderness() && commandBlacklist.isWildernessBlockCreepers() && !commandBlacklist.getWorldsNoWildernessProtection().contains(location.getWorldName())) ||
//				(faction.isNormal() && (online ? commandBlacklist.isTerritoryBlockCreepers() : commandBlacklist.isTerritoryBlockCreepersWhenOffline())))) {
//			// creeper which needs prevention
//			return true;
//		} else if(
//				(boomer instanceof Fireball || boomer instanceof Wither) && (faction.isWilderness() && commandBlacklist.isWildernessBlockFireballs() && !commandBlacklist.getWorldsNoWildernessProtection().contains(location.getWorldName()) || faction.isNormal() && (online ? commandBlacklist.isTerritoryBlockFireballs() : commandBlacklist.isTerritoryBlockFireballsWhenOffline()))) {
//			// ghast fireball which needs prevention
//			// it's a bit crude just using fireball protection for Wither boss too, but I'd rather not add in a whole new set of xxxBlockWitherExplosion or whatever
//			return true;
//		} else if((boomer instanceof TNTPrimed || boomer instanceof ExplosiveMinecart) && ((faction.isWilderness() && commandBlacklist.isWildernessBlockTNT() && !commandBlacklist.getWorldsNoWildernessProtection().contains(location.getWorldName())) ||
//				(faction.isNormal() && (online ? commandBlacklist.isTerritoryBlockTNT() : commandBlacklist.isTerritoryBlockTNTWhenOffline())))) {
//			// TNT which needs prevention
//			return true;
//		} else
//			return (faction.isWilderness() && commandBlacklist.isWildernessBlockOtherExplosions() && !commandBlacklist.getWorldsNoWildernessProtection().contains(location.getWorldName())) ||
//					(faction.isNormal() && (online ? commandBlacklist.isTerritoryBlockOtherExplosions() : commandBlacklist.isTerritoryBlockOtherExplosionsWhenOffline()));
//	}

//	public boolean canPlayerUseBlock(Player player, Material material, Location location, boolean justCheck) {
//		return canUseBlock(player, material, location, justCheck);
//	}

	public static boolean canUseBlock(Player player, Material material, Location location) {
		FPlayer me = FPlayers.getInstance().getByPlayer(player);
		if(me.isAdminBypassing()) {
			return true;
		}

		FLocation loc = new FLocation(location);
		Faction otherFaction = Board.getInstance().getFactionAt(loc);

		// no door/chest/whatever protection in wilderness, war zones, or safe zones
		if(!otherFaction.isNormal()) {
			switch(material) {
				case ITEM_FRAME:
				case ARMOR_STAND:
					return canInteractHere(player, location);
			}
			return true;
		}

//		if(FactionsPlugin.getInstance().getLandRaidControl().isRaidable(otherFaction)) {
//			return true;
//		}

		PermissibleAction action = null;

		switch(material) {
			case LEVER:
				action = PermissibleAction.LEVER;
				break;
			case STONE_BUTTON:
			case BIRCH_BUTTON:
			case ACACIA_BUTTON:
			case DARK_OAK_BUTTON:
			case JUNGLE_BUTTON:
			case OAK_BUTTON:
			case SPRUCE_BUTTON:
				action = PermissibleAction.BUTTON;
				break;
			case DARK_OAK_DOOR:
			case ACACIA_DOOR:
			case BIRCH_DOOR:
			case IRON_DOOR:
			case JUNGLE_DOOR:
			case SPRUCE_DOOR:
			case ACACIA_TRAPDOOR:
			case OAK_DOOR:
			case BIRCH_TRAPDOOR:
			case DARK_OAK_TRAPDOOR:
			case IRON_TRAPDOOR:
			case JUNGLE_TRAPDOOR:
			case OAK_TRAPDOOR:
			case SPRUCE_TRAPDOOR:
				action = PermissibleAction.DOOR;
				break;
			case CHEST:
			case ENDER_CHEST:
			case TRAPPED_CHEST:
			case BARREL:
			case FURNACE:
			case DROPPER:
			case DISPENSER:
			case HOPPER:
			case BLAST_FURNACE:
			case CAULDRON:
			case CAMPFIRE:
			case BREWING_STAND:
			case CARTOGRAPHY_TABLE:
			case GRINDSTONE:
			case SMOKER:
			case STONECUTTER:
			case LECTERN:
			case ITEM_FRAME:
			case JUKEBOX:
			case ARMOR_STAND:
			case REPEATER:
			case ENCHANTING_TABLE:
			case FARMLAND:
			case BEACON:
			case ANVIL:
			case CHIPPED_ANVIL:
			case DAMAGED_ANVIL:
			case FLOWER_POT:
			case BEE_NEST:
				action = PermissibleAction.CONTAINER;
				break;
			default:
				// Check for doors that might have diff material name in old version.
				if(material.name().contains("DOOR") || material.name().contains("GATE")) {
					action = PermissibleAction.DOOR;
				}
				if(material.name().contains("BUTTON")) {
					action = PermissibleAction.BUTTON;
				}
				if(material.name().contains("FURNACE")) {
					action = PermissibleAction.CONTAINER;
				}
				// Lazier than checking all the combinations
				if(material.name().contains("SHULKER") || material.name().contains("ANVIL") || material.name().startsWith("POTTED")) {
					action = PermissibleAction.CONTAINER;
				}
				if(material.name().endsWith("_PLATE")) {
					action = PermissibleAction.PLATE;
				}
				if(material.name().contains("SIGN")) {
					action = PermissibleAction.ITEM;
				}
				break;
		}

		if(action == null) {
			return true;
		}

		// Ignored types
//		if(action == PermissibleAction.CONTAINER && FactionsPlugin.getInstance().configMain.factions().commandBlacklist().getContainerExceptions().contains(material)) {
//			return true;
//		}

		// F PERM check runs through before other checks.
		if(!otherFaction.hasAccess(me, action)) {
			if(action != PermissibleAction.PLATE) {
				me.msg(TL.GENERIC_NOPERMISSION, action);
			}
			return false;
		}

		// Also cancel if player doesn't have ownership rights for this claim
//		if(FactionsPlugin.getInstance().configMain.factions().ownedArea().isEnabled() && FactionsPlugin.getInstance().configMain.factions().ownedArea().isProtectMaterials() && !otherFaction.playerHasOwnershipRights(me, loc)) {
//			if(!justCheck) {
//				me.msg(TL.PLAYER_USE_OWNED, TextUtil.getMaterialName(material), otherFaction.getOwnerListString(loc));
//			}
//
//			return false;
//		}

		return true;
	}
}
