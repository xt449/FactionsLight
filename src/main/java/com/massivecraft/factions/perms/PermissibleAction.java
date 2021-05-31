package com.massivecraft.factions.perms;

import com.massivecraft.factions.configuration.DefaultPermissionsConfiguration;
import com.massivecraft.factions.util.TL;
import org.bukkit.Material;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

public enum PermissibleAction {
	BUILD(DefaultPermissionsConfiguration.Permissions::getBuild, TL.PERM_BUILD, TL.PERM_SHORT_BUILD, Material.STONE),
	DESTROY(DefaultPermissionsConfiguration.Permissions::getDestroy, TL.PERM_DESTROY, TL.PERM_SHORT_DESTROY, Material.WOODEN_PICKAXE),
	PAINBUILD(DefaultPermissionsConfiguration.Permissions::getPainBuild, TL.PERM_PAINBUILD, TL.PERM_SHORT_PAINBUILD, Material.WOODEN_SWORD),
	ITEM(DefaultPermissionsConfiguration.Permissions::getItem, TL.PERM_ITEM, TL.PERM_SHORT_ITEM, Material.ITEM_FRAME),
	CONTAINER(DefaultPermissionsConfiguration.Permissions::getContainer, TL.PERM_CONTAINER, TL.PERM_SHORT_CONTAINER, Material.CHEST_MINECART),
	BUTTON(DefaultPermissionsConfiguration.Permissions::getButton, TL.PERM_BUTTON, TL.PERM_SHORT_BUTTON, Material.STONE_BUTTON),
	DOOR(DefaultPermissionsConfiguration.Permissions::getDoor, TL.PERM_DOOR, TL.PERM_SHORT_DOOR, Material.IRON_DOOR),
	LEVER(DefaultPermissionsConfiguration.Permissions::getLever, TL.PERM_LEVER, TL.PERM_SHORT_LEVER, Material.LEVER),
	PLATE(DefaultPermissionsConfiguration.Permissions::getPlate, TL.PERM_PLATE, TL.PERM_SHORT_PLATE, Material.STONE_PRESSURE_PLATE),
	FROSTWALK(DefaultPermissionsConfiguration.Permissions::getFrostWalk, TL.PERM_FROSTWALK, TL.PERM_SHORT_FROSTWALK, Material.ICE),
	INVITE(DefaultPermissionsConfiguration.Permissions::getInvite, TL.PERM_INVITE, TL.PERM_SHORT_INVITE, Material.FISHING_ROD),
	KICK(DefaultPermissionsConfiguration.Permissions::getKick, TL.PERM_KICK, TL.PERM_SHORT_KICK, Material.LEATHER_BOOTS),
	BAN(DefaultPermissionsConfiguration.Permissions::getBan, TL.PERM_BAN, TL.PERM_SHORT_BAN, Material.BARRIER),
	PROMOTE(DefaultPermissionsConfiguration.Permissions::getPromote, TL.PERM_PROMOTE, TL.PERM_SHORT_PROMOTE, Material.ANVIL),
	DISBAND(DefaultPermissionsConfiguration.Permissions::getDisband, TL.PERM_DISBAND, TL.PERM_SHORT_DISBAND, Material.BONE),
	TERRITORY(DefaultPermissionsConfiguration.Permissions::getTerritory, TL.PERM_TERRITORY, TL.PERM_SHORT_TERRITORY, Material.GRASS_BLOCK),
	OWNER(DefaultPermissionsConfiguration.Permissions::getOwner, TL.PERM_OWNER, TL.PERM_SHORT_OWNER, Material.OAK_FENCE_GATE),
	HOME(DefaultPermissionsConfiguration.Permissions::getHome, TL.PERM_HOME, TL.PERM_SHORT_HOME, Material.TORCH),
	SETHOME(DefaultPermissionsConfiguration.Permissions::getSetHome, TL.PERM_SETHOME, TL.PERM_SHORT_SETHOME, Material.COMPASS),
	LISTCLAIMS(DefaultPermissionsConfiguration.Permissions::getListClaims, TL.PERM_LISTCLAIMS, TL.PERM_SHORT_LISTCLAIMS, Material.MAP),
	SETWARP(DefaultPermissionsConfiguration.Permissions::getSetWarp, TL.PERM_SETWARP, TL.PERM_SHORT_SETWARP, Material.END_PORTAL_FRAME),
	WARP(DefaultPermissionsConfiguration.Permissions::getWarp, TL.PERM_WARP, TL.PERM_SHORT_WARP, Material.ENDER_PEARL),
	;

	public final String description;
	public final String descriptionShort;
	public final Material material;
	private final Function<DefaultPermissionsConfiguration.Permissions, DefaultPermissionsConfiguration.Permissions.FactionOnlyPermInfo> function;

	PermissibleAction(Function<DefaultPermissionsConfiguration.Permissions, DefaultPermissionsConfiguration.Permissions.FactionOnlyPermInfo> function, TL description, TL descriptionShort, Material material) {
		this.function = function;
		this.description = description.toString();
		this.descriptionShort = descriptionShort.toString();
		this.material = material;
	}

	private static final Map<String, PermissibleAction> map = new HashMap<>();

	static {
		for(PermissibleAction action : values()) {
			map.put(action.name().toLowerCase(), action);
		}
	}

	public DefaultPermissionsConfiguration.Permissions.FactionOnlyPermInfo getPermInfo(DefaultPermissionsConfiguration.Permissions permissions) {
		return this.function.apply(permissions);
	}

	/**
	 * Case insensitive check for action.
	 *
	 * @param check check
	 * @return permissible
	 */
	public static PermissibleAction fromString(String check) {
		return check == null ? null : map.get(check.toLowerCase());
	}

	@Override
	public String toString() {
		return name();
	}

}
