package com.massivecraft.factions.listeners;

import com.massivecraft.factions.*;
import com.massivecraft.factions.configuration.MainConfiguration;
import com.massivecraft.factions.perms.PermissibleAction;
import com.massivecraft.factions.struct.Permission;
import com.massivecraft.factions.util.TL;
import com.massivecraft.factions.util.TextUtil;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.*;
import org.bukkit.entity.minecart.ExplosiveMinecart;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Listener;

import java.util.List;
import java.util.stream.Collectors;


public abstract class AbstractListener implements Listener {
	public boolean playerCanInteractHere(Player player, Location location) {
		return canInteractHere(player, location);
	}

	public static boolean canInteractHere(Player player, Location location) {
		String name = player.getName();
		if(FactionsPlugin.getInstance().configMain.factions().protection().getPlayersWhoBypassAllProtection().contains(name)) {
			return true;
		}

		FPlayer me = FPlayers.getInstance().getByPlayer(player);
		if(me.isAdminBypassing()) {
			return true;
		}

		FLocation loc = new FLocation(location);
		Faction otherFaction = Board.getInstance().getFactionAt(loc);

		if(FactionsPlugin.getInstance().getLandRaidControl().isRaidable(otherFaction)) {
			return true;
		}

		MainConfiguration.Factions.Protection protection = FactionsPlugin.getInstance().configMain.factions().protection();
		if(otherFaction.isWilderness()) {
			if(!protection.isWildernessDenyUsage() || protection.getWorldsNoWildernessProtection().contains(location.getWorld().getName())) {
				return true; // This is not faction territory. Use whatever you like here.
			}
			me.msg(TL.PLAYER_USE_WILDERNESS, "this");
			return false;
		} else if(otherFaction.isSafeZone()) {
			if(!protection.isSafeZoneDenyUsage() || Permission.MANAGE_SAFE_ZONE.has(player)) {
				return true;
			}
			me.msg(TL.PLAYER_USE_SAFEZONE, "this");
			return false;
		} else if(otherFaction.isWarZone()) {
			if(!protection.isWarZoneDenyUsage() || Permission.MANAGE_WAR_ZONE.has(player)) {
				return true;
			}
			me.msg(TL.PLAYER_USE_WARZONE, "this");

			return false;
		}

		boolean access = otherFaction.hasAccess(me, PermissibleAction.ITEM);

		// Cancel if we are not in our own territory
		if(!access) {
			me.msg(TL.PLAYER_USE_TERRITORY, "this", otherFaction.getTag(me.getFaction()));
			return false;
		}

		// Also cancel if player doesn't have ownership rights for this claim
		if(FactionsPlugin.getInstance().configMain.factions().ownedArea().isEnabled() && FactionsPlugin.getInstance().configMain.factions().ownedArea().isDenyUsage() && !otherFaction.playerHasOwnershipRights(me, loc)) {
			me.msg(TL.PLAYER_USE_OWNED, "this", otherFaction.getOwnerListString(loc));
			return false;
		}

		return true;
	}

	protected void handleExplosion(Location loc, Entity boomer, Cancellable event, List<Block> blockList) {
		if(!FactionsPlugin.getInstance().worldUtil().isEnabled(loc.getWorld())) {
			return;
		}

		if(explosionDisallowed(boomer, new FLocation(loc))) {
			event.setCancelled(true);
			return;
		}

		List<Chunk> chunks = blockList.stream().map(Block::getChunk).distinct().collect(Collectors.toList());
		if(chunks.removeIf(chunk -> explosionDisallowed(boomer, new FLocation(chunk)))) {
			blockList.removeIf(block -> !chunks.contains(block.getChunk()));
		}
	}

	public static boolean explosionDisallowed(Entity boomer, FLocation location) {
		Faction faction = Board.getInstance().getFactionAt(location);
		boolean online = faction.hasPlayersOnline();
		if(faction.noExplosionsInTerritory() || (faction.isPeaceful() && FactionsPlugin.getInstance().configMain.factions().specialCase().isPeacefulTerritoryDisableBoom())) {
			// faction is peaceful and has explosions set to disabled
			return true;
		}
		MainConfiguration.Factions.Protection protection = FactionsPlugin.getInstance().configMain.factions().protection();
		if(boomer instanceof Creeper && ((faction.isWilderness() && protection.isWildernessBlockCreepers() && !protection.getWorldsNoWildernessProtection().contains(location.getWorldName())) ||
				(faction.isNormal() && (online ? protection.isTerritoryBlockCreepers() : protection.isTerritoryBlockCreepersWhenOffline())) ||
				(faction.isWarZone() && protection.isWarZoneBlockCreepers()) ||
				faction.isSafeZone())) {
			// creeper which needs prevention
			return true;
		} else if(
				(boomer instanceof Fireball || boomer instanceof Wither) && (faction.isWilderness() && protection.isWildernessBlockFireballs() && !protection.getWorldsNoWildernessProtection().contains(location.getWorldName()) || faction.isNormal() && (online ? protection.isTerritoryBlockFireballs() : protection.isTerritoryBlockFireballsWhenOffline()) || faction.isWarZone() && protection.isWarZoneBlockFireballs() || faction.isSafeZone())) {
			// ghast fireball which needs prevention
			// it's a bit crude just using fireball protection for Wither boss too, but I'd rather not add in a whole new set of xxxBlockWitherExplosion or whatever
			return true;
		} else if((boomer instanceof TNTPrimed || boomer instanceof ExplosiveMinecart) && ((faction.isWilderness() && protection.isWildernessBlockTNT() && !protection.getWorldsNoWildernessProtection().contains(location.getWorldName())) ||
				(faction.isNormal() && (online ? protection.isTerritoryBlockTNT() : protection.isTerritoryBlockTNTWhenOffline())) ||
				(faction.isWarZone() && protection.isWarZoneBlockTNT()) ||
				(faction.isSafeZone() && protection.isSafeZoneBlockTNT()))) {
			// TNT which needs prevention
			return true;
		} else
			return (faction.isWilderness() && protection.isWildernessBlockOtherExplosions() && !protection.getWorldsNoWildernessProtection().contains(location.getWorldName())) ||
					(faction.isNormal() && (online ? protection.isTerritoryBlockOtherExplosions() : protection.isTerritoryBlockOtherExplosionsWhenOffline())) ||
					(faction.isWarZone() && protection.isWarZoneBlockOtherExplosions()) ||
					(faction.isSafeZone() && protection.isSafeZoneBlockOtherExplosions());
	}

	public boolean canPlayerUseBlock(Player player, Material material, Location location, boolean justCheck) {
		return canUseBlock(player, material, location, justCheck);
	}

	public static boolean canUseBlock(Player player, Material material, Location location, boolean justCheck) {
		if(FactionsPlugin.getInstance().configMain.factions().protection().getPlayersWhoBypassAllProtection().contains(player.getName())) {
			return true;
		}

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

		if(FactionsPlugin.getInstance().getLandRaidControl().isRaidable(otherFaction)) {
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
		if(action == PermissibleAction.CONTAINER && FactionsPlugin.getInstance().configMain.factions().protection().getContainerExceptions().contains(material)) {
			return true;
		}

		// F PERM check runs through before other checks.
		if(!otherFaction.hasAccess(me, action)) {
			if(action != PermissibleAction.PLATE) {
				me.msg(TL.GENERIC_NOPERMISSION, action);
			}
			return false;
		}

		// Also cancel if player doesn't have ownership rights for this claim
		if(FactionsPlugin.getInstance().configMain.factions().ownedArea().isEnabled() && FactionsPlugin.getInstance().configMain.factions().ownedArea().isProtectMaterials() && !otherFaction.playerHasOwnershipRights(me, loc)) {
			if(!justCheck) {
				me.msg(TL.PLAYER_USE_OWNED, TextUtil.getMaterialName(material), otherFaction.getOwnerListString(loc));
			}

			return false;
		}

		return true;
	}

}
