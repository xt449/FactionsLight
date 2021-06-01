package com.massivecraft.factions.perms;

import com.massivecraft.factions.configuration.DefaultPermissionsConfiguration;
import com.massivecraft.factions.util.TL;
import org.bukkit.Material;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

public enum PermissibleAction {
	BUILD(DefaultPermissionsConfiguration::getBuild, TL.PERM_BUILD, TL.PERM_SHORT_BUILD, Material.STONE),
	DESTROY(DefaultPermissionsConfiguration::getDestroy, TL.PERM_DESTROY, TL.PERM_SHORT_DESTROY, Material.WOODEN_PICKAXE),
	PAINBUILD(DefaultPermissionsConfiguration::getPainBuild, TL.PERM_PAINBUILD, TL.PERM_SHORT_PAINBUILD, Material.WOODEN_SWORD),
	ITEM(DefaultPermissionsConfiguration::getItem, TL.PERM_ITEM, TL.PERM_SHORT_ITEM, Material.ITEM_FRAME),
	CONTAINER(DefaultPermissionsConfiguration::getContainer, TL.PERM_CONTAINER, TL.PERM_SHORT_CONTAINER, Material.CHEST_MINECART),
	BUTTON(DefaultPermissionsConfiguration::getButton, TL.PERM_BUTTON, TL.PERM_SHORT_BUTTON, Material.STONE_BUTTON),
	DOOR(DefaultPermissionsConfiguration::getDoor, TL.PERM_DOOR, TL.PERM_SHORT_DOOR, Material.IRON_DOOR),
	LEVER(DefaultPermissionsConfiguration::getLever, TL.PERM_LEVER, TL.PERM_SHORT_LEVER, Material.LEVER),
	PLATE(DefaultPermissionsConfiguration::getPlate, TL.PERM_PLATE, TL.PERM_SHORT_PLATE, Material.STONE_PRESSURE_PLATE),
	FROSTWALK(DefaultPermissionsConfiguration::getFrostWalk, TL.PERM_FROSTWALK, TL.PERM_SHORT_FROSTWALK, Material.ICE),
	INVITE(DefaultPermissionsConfiguration::getInvite, TL.PERM_INVITE, TL.PERM_SHORT_INVITE, Material.FISHING_ROD),
	KICK(DefaultPermissionsConfiguration::getKick, TL.PERM_KICK, TL.PERM_SHORT_KICK, Material.LEATHER_BOOTS),
	BAN(DefaultPermissionsConfiguration::getBan, TL.PERM_BAN, TL.PERM_SHORT_BAN, Material.BARRIER),
	PROMOTE(DefaultPermissionsConfiguration::getPromote, TL.PERM_PROMOTE, TL.PERM_SHORT_PROMOTE, Material.ANVIL),
	DISBAND(DefaultPermissionsConfiguration::getDisband, TL.PERM_DISBAND, TL.PERM_SHORT_DISBAND, Material.BONE),
	TERRITORY(DefaultPermissionsConfiguration::getTerritory, TL.PERM_TERRITORY, TL.PERM_SHORT_TERRITORY, Material.GRASS_BLOCK),
	LISTCLAIMS(DefaultPermissionsConfiguration::getListClaims, TL.PERM_LISTCLAIMS, TL.PERM_SHORT_LISTCLAIMS, Material.MAP),
	;

	private final Function<DefaultPermissionsConfiguration, DefaultPermissionsConfiguration.FactionOnlyPermInfo> function;
	public final String description;
	public final String descriptionShort;
	public final Material material;

	PermissibleAction(Function<DefaultPermissionsConfiguration, DefaultPermissionsConfiguration.FactionOnlyPermInfo> function, TL description, TL descriptionShort, Material material) {
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

	public DefaultPermissionsConfiguration.FactionOnlyPermInfo getPermInfo(DefaultPermissionsConfiguration permissions) {
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
