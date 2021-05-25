package com.massivecraft.factions.tag;

import com.massivecraft.factions.FactionsPlugin;
import com.massivecraft.factions.IFaction;
import com.massivecraft.factions.IFactionPlayer;
import com.massivecraft.factions.integration.Econ;
import com.massivecraft.factions.landraidcontrol.DTRControl;
import com.massivecraft.factions.landraidcontrol.PowerControl;
import com.massivecraft.factions.perms.Relation;
import com.massivecraft.factions.util.Localization;
import org.apache.commons.lang.time.DurationFormatUtils;

import java.util.function.BiFunction;
import java.util.function.Function;

public enum FactionTag implements Tag {
	HOME_X("x", (fac) -> fac.hasHome() ? String.valueOf(fac.getHome().getBlockX()) : Tag.isMinimalShow() ? null : "{ig}"),
	HOME_Y("y", (fac) -> fac.hasHome() ? String.valueOf(fac.getHome().getBlockY()) : Tag.isMinimalShow() ? null : "{ig}"),
	HOME_Z("z", (fac) -> fac.hasHome() ? String.valueOf(fac.getHome().getBlockZ()) : Tag.isMinimalShow() ? null : "{ig}"),
	CHUNKS("chunks", (fac) -> String.valueOf(fac.getLandRounded())),
	WARPS("warps", (fac) -> String.valueOf(fac.getWarps().size())),
	HEADER("header", (fac, fp) -> FactionsPlugin.getInstance().txt().titleize(fac.getTag(fp))),
	POWER("power", (fac) -> String.valueOf(fac.getPowerRounded())),
	MAX_POWER("maxPower", (fac) -> String.valueOf(fac.getPowerMaxRounded())),
	POWER_BOOST("power-boost", (fac) -> {
		double powerBoost = fac.getPowerBoost();
		return (powerBoost == 0.0) ? "" : (powerBoost > 0.0 ? Localization.COMMAND_SHOW_BONUS.toString() : Localization.COMMAND_SHOW_PENALTY.toString() + powerBoost + ")");
	}),
	LEADER("leader", (fac) -> {
		IFactionPlayer fAdmin = fac.getFPlayerAdmin();
		return fAdmin == null ? "Server" : fAdmin.getName().substring(0, fAdmin.getName().length() > 14 ? 13 : fAdmin.getName().length());
	}),
	JOINING("joining", (fac) -> (fac.getOpen() ? Localization.COMMAND_SHOW_UNINVITED.toString() : Localization.COMMAND_SHOW_INVITATION.toString())),
	@SuppressWarnings("Convert2MethodRef")
	FACTION("faction", (fac) -> fac.getTag()),
	FACTION_RELATION_COLOR("faction-relation-color", (fac, fp) -> fp == null ? "" : fp.getColorTo(fac).toString()),
	HOME_WORLD("world", (fac) -> fac.hasHome() ? fac.getHome().getWorld().getName() : Tag.isMinimalShow() ? null : "{ig}"),
	RAIDABLE("raidable", (fac) -> {
		boolean raid = FactionsPlugin.getInstance().getLandRaidControl().isRaidable(fac);
		return raid ? Localization.RAIDABLE_TRUE.toString() : Localization.RAIDABLE_FALSE.toString();
	}),
	DTR("dtr", (fac) -> {
		if(FactionsPlugin.getInstance().getLandRaidControl() instanceof PowerControl) {
			int dtr = fac.getLandRounded() >= fac.getPowerRounded() ? 0 : (int) Math.ceil(((double) (fac.getPowerRounded() - fac.getLandRounded())) / FactionsPlugin.getInstance().conf().factions().landRaidControl().power().getLossPerDeath());
			return Localization.COMMAND_SHOW_DEATHS_TIL_RAIDABLE.format(dtr);
		} else {
			return DTRControl.round(fac.getDTR());
		}
	}),
	MAX_DTR("max-dtr", (fac) -> {
		if(FactionsPlugin.getInstance().getLandRaidControl() instanceof DTRControl) {
			return DTRControl.round(((DTRControl) FactionsPlugin.getInstance().getLandRaidControl()).getMaxDTR(fac));
		}
		return Tag.isMinimalShow() ? null : "{ig}";
	}),
	DTR_FROZEN("dtr-frozen-status", (fac -> Localization.DTR_FROZEN_STATUS_MESSAGE.format(fac.isFrozenDTR() ? Localization.DTR_FROZEN_STATUS_TRUE.toString() : Localization.DTR_FROZEN_STATUS_FALSE.toString()))),
	DTR_FROZEN_TIME("dtr-frozen-time", (fac -> Localization.DTR_FROZEN_TIME_MESSAGE.format(fac.isFrozenDTR() ?
			DurationFormatUtils.formatDuration(fac.getFrozenDTRUntilTime() - System.currentTimeMillis(), FactionsPlugin.getInstance().conf().factions().landRaidControl().dtr().getFreezeTimeFormat()) :
			Localization.DTR_FROZEN_TIME_NOTFROZEN.toString()))),
	MAX_CHUNKS("max-chunks", (fac -> String.valueOf(FactionsPlugin.getInstance().getLandRaidControl().getLandLimit(fac)))),
	PEACEFUL("peaceful", (fac) -> fac.isPeaceful() ? FactionsPlugin.getInstance().conf().colors().relations().getPeaceful() + Localization.COMMAND_SHOW_PEACEFUL.toString() : ""),
	PERMANENT("permanent", (fac) -> fac.isPermanent() ? "permanent" : "{notPermanent}"), // no braces needed
	LAND_VALUE("land-value", (fac) -> Econ.shouldBeUsed() ? Econ.moneyString(Econ.calculateTotalLandValue(fac.getLandRounded())) : Tag.isMinimalShow() ? null : Localization.ECON_OFF.format("value")),
	DESCRIPTION("description", IFaction::getDescription),
	CREATE_DATE("create-date", (fac) -> Localization.sdf.format(fac.getFoundedDate())),
	LAND_REFUND("land-refund", (fac) -> Econ.shouldBeUsed() ? Econ.moneyString(Econ.calculateTotalLandRefund(fac.getLandRounded())) : Tag.isMinimalShow() ? null : Localization.ECON_OFF.format("refund")),
	BANK_BALANCE("faction-balance", (fac) -> {
		if(Econ.shouldBeUsed()) {
			return FactionsPlugin.getInstance().conf().economy().isBankEnabled() ? Econ.moneyString(Econ.getBalance(fac)) : Tag.isMinimalShow() ? null : Localization.ECON_OFF.format("balance");
		}
		return Tag.isMinimalShow() ? null : Localization.ECON_OFF.format("balance");
	}),
	TNT_BALANCE("tnt-balance", (fac) -> {
		if(FactionsPlugin.getInstance().conf().commands().tnt().isEnable()) {
			return String.valueOf(fac.getTNTBank());
		}
		return Tag.isMinimalShow() ? null : "";
	}),
	TNT_MAX("tnt-max-balance", (fac) -> {
		if(FactionsPlugin.getInstance().conf().commands().tnt().isEnable()) {
			return String.valueOf(FactionsPlugin.getInstance().conf().commands().tnt().getMaxStorage());
		}
		return Tag.isMinimalShow() ? null : "";
	}),
	ALLIES_COUNT("allies", (fac) -> String.valueOf(fac.getRelationCount(Relation.ALLY))),
	ENEMIES_COUNT("enemies", (fac) -> String.valueOf(fac.getRelationCount(Relation.ENEMY))),
	TRUCES_COUNT("truces", (fac) -> String.valueOf(fac.getRelationCount(Relation.TRUCE))),
	ONLINE_COUNT("online", (fac, fp) -> {
		if(fp != null && fp.isOnline()) {
			return String.valueOf(fac.getFPlayersWhereOnline(true, fp).size());
		} else {
			// Only console should ever get here.
			return String.valueOf(fac.getFPlayersWhereOnline(true).size());
		}
	}),
	OFFLINE_COUNT("offline", (fac, fp) -> {
		if(fp != null && fp.isOnline()) {
			return String.valueOf(fac.getFPlayers().size() - fac.getFPlayersWhereOnline(true, fp).size());
		} else {
			// Only console should ever get here.
			return String.valueOf(fac.getFPlayersWhereOnline(false).size());
		}
	}),
	FACTION_SIZE("members", (fac) -> String.valueOf(fac.getFPlayers().size())),
	FACTION_KILLS("faction-kills", (fac) -> String.valueOf(fac.getKills())),
	FACTION_DEATHS("faction-deaths", (fac) -> String.valueOf(fac.getDeaths())),
	FACTION_BANCOUNT("faction-bancount", (fac) -> String.valueOf(fac.getBannedPlayers().size())),
	;

	private final String tag;
	private final BiFunction<IFaction, IFactionPlayer, String> biFunction;
	private final Function<IFaction, String> function;

	public static String parse(String text, IFaction faction, IFactionPlayer player) {
		for(FactionTag tag : FactionTag.values()) {
			text = tag.replace(text, faction, player);
		}
		return text;
	}

	public static String parse(String text, IFaction faction) {
		for(FactionTag tag : FactionTag.values()) {
			text = tag.replace(text, faction);
		}
		return text;
	}

	FactionTag(String tag, BiFunction<IFaction, IFactionPlayer, String> function) {
		this(tag, null, function);
	}

	FactionTag(String tag, Function<IFaction, String> function) {
		this(tag, function, null);
	}

	FactionTag(String tag, Function<IFaction, String> function, BiFunction<IFaction, IFactionPlayer, String> biFunction) {
		if(tag.equalsIgnoreCase("permanent")) {
			this.tag = tag;
		} else {
			this.tag = '{' + tag + '}';
		}
		this.biFunction = biFunction;
		this.function = function;
	}

	@Override
	public String getTag() {
		return this.tag;
	}

	@Override
	public boolean foundInString(String test) {
		return test != null && test.contains(this.tag);
	}

	public String replace(String text, IFaction faction, IFactionPlayer player) {
		if(!this.foundInString(text)) {
			return text;
		}
		String result = this.function == null ? this.biFunction.apply(faction, player) : this.function.apply(faction);
		return result == null ? null : text.replace(this.tag, result);
	}

	public String replace(String text, IFaction faction) {
		return this.replace(text, faction, null);
	}
}
