package com.massivecraft.factions.scoreboards.sidebar;

import com.massivecraft.factions.FactionsPlugin;
import com.massivecraft.factions.IFaction;
import com.massivecraft.factions.IFactionPlayer;
import com.massivecraft.factions.scoreboards.FSidebarProvider;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

public class FInfoSidebar extends FSidebarProvider {
	private final IFaction faction;

	public FInfoSidebar(IFaction faction) {
		this.faction = faction;
	}

	@Override
	public String getTitle(IFactionPlayer fplayer) {
		return replaceTags(this.faction, fplayer, FactionsPlugin.getInstance().conf().scoreboard().info().getTitle());
	}

	@Override
	public List<String> getLines(IFactionPlayer fplayer) {
		List<String> lines = new ArrayList<>(FactionsPlugin.getInstance().conf().scoreboard().info().getContent());

		ListIterator<String> it = lines.listIterator();
		while(it.hasNext()) {
			String next = it.next();
			if(next == null) {
				it.remove();
				continue;
			}
			String replaced = replaceTags(faction, fplayer, next);
			if(replaced == null) {
				it.remove();
			} else {
				it.set(replaced);
			}
		}
		return lines;
	}
}
