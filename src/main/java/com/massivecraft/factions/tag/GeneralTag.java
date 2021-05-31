package com.massivecraft.factions.tag;

import com.massivecraft.factions.FPlayers;
import org.bukkit.Bukkit;

import java.util.function.Supplier;

public enum GeneralTag implements Tag {
	FACTIONLESS("factionless", () -> String.valueOf(FPlayers.getInstance().getOnlinePlayers().stream().filter(p -> !p.hasFaction()).count())),
	FACTIONLESS_TOTAL("factionless-total", () -> String.valueOf(FPlayers.getInstance().getAllFPlayers().stream().filter(p -> !p.hasFaction()).count())),
	TOTAL_ONLINE("total-online", () -> String.valueOf(Bukkit.getOnlinePlayers().size())),
	;

	private final String tag;
	private final Supplier<String> supplier;

	public static String parse(String text) {
		for(GeneralTag tag : GeneralTag.values()) {
			text = tag.replace(text);
		}
		return text;
	}

	GeneralTag(String tag, Supplier<String> supplier) {
		this.tag = '{' + tag + '}';
		this.supplier = supplier;
	}

	@Override
	public String toString() {
		return this.tag;
	}

	public String replace(String text) {
		if(!text.contains(this.tag)) {
			return text;
		}
		String result = this.supplier.get();
		return result == null ? null : text.replace(this.tag, result);
	}
}
