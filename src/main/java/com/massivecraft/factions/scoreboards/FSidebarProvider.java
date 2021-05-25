package com.massivecraft.factions.scoreboards;

import com.massivecraft.factions.FactionsPlugin;
import com.massivecraft.factions.IFaction;
import com.massivecraft.factions.IFactionPlayer;
import com.massivecraft.factions.tag.Tag;
import com.massivecraft.factions.util.TL;

import java.util.List;

public abstract class FSidebarProvider {

	public abstract String getTitle(IFactionPlayer fplayer);

	public abstract List<String> getLines(IFactionPlayer fplayer);

	public String replaceTags(IFactionPlayer fPlayer, String s) {
		s = Tag.parsePlaceholders(fPlayer.getPlayer(), s);

		return qualityAssure(Tag.parsePlain(fPlayer, s));
	}

	public String replaceTags(IFaction faction, IFactionPlayer fPlayer, String s) {
		// Run through Placeholder API first
		s = Tag.parsePlaceholders(fPlayer.getPlayer(), s);

		return qualityAssure(Tag.parsePlain(faction, fPlayer, s));
	}

	private String qualityAssure(String line) {
		if(line.contains("{notFrozen}") || line.contains("{notPermanent}")) {
			return "n/a"; // we dont support support these error variables in scoreboards
		}
		if(line.contains("{ig}")) {
			// since you can't really fit a whole "Faction Home: world, x, y, z" on one line
			// we assume it's broken up into two lines, so returning our tl will suffice.
			return TL.COMMAND_SHOW_NOHOME.toString();
		}
		return FactionsPlugin.getInstance().txt().parse(line); // finally add color :)
	}
}
