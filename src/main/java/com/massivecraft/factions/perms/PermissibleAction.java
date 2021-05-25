package com.massivecraft.factions.perms;

import com.massivecraft.factions.config.file.DefaultPermissionsConfig;
import com.massivecraft.factions.util.Localization;
import com.massivecraft.factions.util.material.MaterialHelper;
import org.bukkit.Material;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

public enum PermissibleAction {
	BUILD(DefaultPermissionsConfig.Permissions::getBuild, Localization.PERM_BUILD, Localization.PERM_SHORT_BUILD, "STONE"),
	DESTROY(DefaultPermissionsConfig.Permissions::getDestroy, Localization.PERM_DESTROY, Localization.PERM_SHORT_DESTROY, "WOODEN_PICKAXE"),
	PAINBUILD(DefaultPermissionsConfig.Permissions::getPainBuild, Localization.PERM_PAINBUILD, Localization.PERM_SHORT_PAINBUILD, "WOODEN_SWORD"),
	ITEM(DefaultPermissionsConfig.Permissions::getItem, Localization.PERM_ITEM, Localization.PERM_SHORT_ITEM, "ITEM_FRAME"),
	CONTAINER(DefaultPermissionsConfig.Permissions::getContainer, Localization.PERM_CONTAINER, Localization.PERM_SHORT_CONTAINER, "CHEST_MINECART"),
	BUTTON(DefaultPermissionsConfig.Permissions::getButton, Localization.PERM_BUTTON, Localization.PERM_SHORT_BUTTON, "STONE_BUTTON"),
	DOOR(DefaultPermissionsConfig.Permissions::getDoor, Localization.PERM_DOOR, Localization.PERM_SHORT_DOOR, "IRON_DOOR"),
	LEVER(DefaultPermissionsConfig.Permissions::getLever, Localization.PERM_LEVER, Localization.PERM_SHORT_LEVER, "LEVER"),
	PLATE(DefaultPermissionsConfig.Permissions::getPlate, Localization.PERM_PLATE, Localization.PERM_SHORT_PLATE, "STONE_PRESSURE_PLATE"),
	FROSTWALK(DefaultPermissionsConfig.Permissions::getFrostWalk, Localization.PERM_FROSTWALK, Localization.PERM_SHORT_FROSTWALK, "ICE"),
	INVITE(true, DefaultPermissionsConfig.Permissions::getInvite, Localization.PERM_INVITE, Localization.PERM_SHORT_INVITE, "FISHING_ROD"),
	KICK(true, DefaultPermissionsConfig.Permissions::getKick, Localization.PERM_KICK, Localization.PERM_SHORT_KICK, "LEATHER_BOOTS"),
	BAN(true, DefaultPermissionsConfig.Permissions::getBan, Localization.PERM_BAN, Localization.PERM_SHORT_BAN, "BARRIER"),
	PROMOTE(true, DefaultPermissionsConfig.Permissions::getPromote, Localization.PERM_PROMOTE, Localization.PERM_SHORT_PROMOTE, "ANVIL"),
	DISBAND(true, DefaultPermissionsConfig.Permissions::getDisband, Localization.PERM_DISBAND, Localization.PERM_SHORT_DISBAND, "BONE"),
	ECONOMY(true, DefaultPermissionsConfig.Permissions::getEconomy, Localization.PERM_ECONOMY, Localization.PERM_SHORT_ECONOMY, "GOLD_INGOT"),
	TERRITORY(true, DefaultPermissionsConfig.Permissions::getTerritory, Localization.PERM_TERRITORY, Localization.PERM_SHORT_TERRITORY, "GRASS_BLOCK"),
	OWNER(true, DefaultPermissionsConfig.Permissions::getOwner, Localization.PERM_OWNER, Localization.PERM_SHORT_OWNER, "FENCE_GATE"),
	HOME(DefaultPermissionsConfig.Permissions::getHome, Localization.PERM_HOME, Localization.PERM_SHORT_HOME, "TORCH"),
	SETHOME(true, DefaultPermissionsConfig.Permissions::getSetHome, Localization.PERM_SETHOME, Localization.PERM_SHORT_SETHOME, "COMPASS"),
	LISTCLAIMS(true, DefaultPermissionsConfig.Permissions::getListClaims, Localization.PERM_LISTCLAIMS, Localization.PERM_SHORT_LISTCLAIMS, "MAP"),
	SETWARP(true, DefaultPermissionsConfig.Permissions::getSetWarp, Localization.PERM_SETWARP, Localization.PERM_SHORT_SETWARP, "END_PORTAL_FRAME"),
	TNTDEPOSIT(true, DefaultPermissionsConfig.Permissions::getTNTDeposit, Localization.PERM_TNTDEPOSIT, Localization.PERM_SHORT_TNTDEPOSIT, "TNT"),
	TNTWITHDRAW(true, DefaultPermissionsConfig.Permissions::getTNTWithdraw, Localization.PERM_TNTWITHDRAW, Localization.PERM_SHORT_TNTWITHDRAW, "TNT"),
	WARP(DefaultPermissionsConfig.Permissions::getWarp, Localization.PERM_WARP, Localization.PERM_SHORT_WARP, "ENDER_PEARL"),
	;

	private final boolean factionOnly;
	private final String materialName;
	private final Localization desc;
	private final Localization shortDesc;
	private Material material;
	private Function<DefaultPermissionsConfig.Permissions, DefaultPermissionsConfig.Permissions.FullPermInfo> fullFunction;
	private Function<DefaultPermissionsConfig.Permissions, DefaultPermissionsConfig.Permissions.FactionOnlyPermInfo> factionOnlyFunction;

	PermissibleAction(Function<DefaultPermissionsConfig.Permissions, DefaultPermissionsConfig.Permissions.FullPermInfo> fullFunction, Localization desc, Localization shortDesc, String materialName) {
		this.factionOnly = false;
		this.fullFunction = fullFunction;
		this.desc = desc;
		this.shortDesc = shortDesc;
		this.materialName = materialName;
	}

	PermissibleAction(boolean factionOnly, Function<DefaultPermissionsConfig.Permissions, DefaultPermissionsConfig.Permissions.FactionOnlyPermInfo> factionOnlyFunction, Localization desc, Localization shortDesc, String materialName) {
		this.factionOnly = factionOnly;
		if(this.factionOnly) {
			this.factionOnlyFunction = factionOnlyFunction;
		} else {
			throw new AssertionError("May only set factionOnly actions in this constructor");
		}
		this.desc = desc;
		this.shortDesc = shortDesc;
		this.materialName = materialName;
	}

	private static final Map<String, PermissibleAction> map = new HashMap<>();

	static {
		for(PermissibleAction action : values()) {
			map.put(action.name().toLowerCase(), action);
		}
	}

	public boolean isFactionOnly() {
		return this.factionOnly;
	}

	public DefaultPermissionsConfig.Permissions.FullPermInfo getFullPerm(DefaultPermissionsConfig.Permissions permissions) {
		return this.fullFunction.apply(permissions);
	}

	public DefaultPermissionsConfig.Permissions.FactionOnlyPermInfo getFactionOnly(DefaultPermissionsConfig.Permissions permissions) {
		return this.factionOnlyFunction.apply(permissions);
	}

	public Material getMaterial() {
		if(this.material == null) {
			this.material = MaterialHelper.get(this.materialName, Material.STONE);
		}
		return this.material;
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

	public String getDescription() {
		return this.desc.toString();
	}

	public String getShortDescription() {
		return this.shortDesc.toString();
	}

	@Override
	public String toString() {
		return name();
	}

}
