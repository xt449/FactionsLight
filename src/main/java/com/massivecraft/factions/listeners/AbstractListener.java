package com.massivecraft.factions.listeners;

import com.massivecraft.factions.*;
import com.massivecraft.factions.perms.PermissibleAction;
import com.massivecraft.factions.util.TL;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;


public abstract class AbstractListener implements Listener {
	public static boolean canInteractHere(Player player, Location location) {
		FPlayer me = FPlayers.getInstance().getByPlayer(player);
		if(me.isAdminBypassing()) {
			return true;
		}

		FLocation loc = new FLocation(location);
		Faction otherFaction = Board.getInstance().getFactionAt(loc);

		boolean access = otherFaction.hasAccess(me, PermissibleAction.ITEM);

		// Cancel if we are not in our own territory
		if(!access) {
			me.msg(TL.PLAYER_USE_TERRITORY, "this", otherFaction.getTag(me.getFaction()));
			return false;
		}

		return true;
	}

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

		return true;
	}
}
