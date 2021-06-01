package com.massivecraft.factions.scoreboards;

import com.massivecraft.factions.FPlayer;
import com.massivecraft.factions.Faction;
import com.massivecraft.factions.tag.Tag;
import com.massivecraft.factions.util.TextUtil;

import java.util.List;

public abstract class FSidebarProvider {

	public abstract String getTitle(FPlayer fplayer);

	public abstract List<String> getLines(FPlayer fplayer);

	public String replaceTags(FPlayer fPlayer, String s) {
		s = Tag.parsePlaceholders(fPlayer.getPlayer(), s);

		return qualityAssure(Tag.parsePlain(fPlayer, s));
	}

	public String replaceTags(Faction faction, FPlayer fPlayer, String s) {
		// Run through Placeholder API first
		s = Tag.parsePlaceholders(fPlayer.getPlayer(), s);

		return qualityAssure(Tag.parsePlain(faction, fPlayer, s));
	}

	private String qualityAssure(String line) {
		if(line.contains("{notFrozen}") || line.contains("{notPermanent}")) {
			return "n/a"; // we dont support support these error variables in scoreboards
		}
		return TextUtil.parse(line); // finally add color :)
	}
}
