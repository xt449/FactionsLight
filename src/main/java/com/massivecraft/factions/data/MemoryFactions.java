package com.massivecraft.factions.data;

import com.massivecraft.factions.Faction;
import com.massivecraft.factions.Factions;
import com.massivecraft.factions.util.MiscUtil;
import com.massivecraft.factions.util.TL;
import org.bukkit.ChatColor;

import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public abstract class MemoryFactions extends Factions {

	public final Map<Integer, Faction> factions = new ConcurrentHashMap<>();
	public int nextId = 1;

	public int load() {
		// Make sure the default neutral faction exists
		if(!factions.containsKey(0)) {
			Faction faction = generateFactionObject(0);
			factions.put(0, faction);
			faction.setTag(TL.WILDERNESS.toString());
			faction.setDescription(TL.WILDERNESS_DESCRIPTION.toString());
		} else {
			Faction faction = factions.get(0);
			if(!faction.getTag().equalsIgnoreCase(TL.WILDERNESS.toString())) {
				faction.setTag(TL.WILDERNESS.toString());
			}
			if(!faction.getDescription().equalsIgnoreCase(TL.WILDERNESS_DESCRIPTION.toString())) {
				faction.setDescription(TL.WILDERNESS_DESCRIPTION.toString());
			}
		}
		return 0;
	}

	public Faction getFactionById(int id) {
		return factions.get(id);
	}

	public abstract Faction generateFactionObject(int string);

	public Faction getByTag(String str) {
		String compStr = MiscUtil.getComparisonString(str);
		for(Faction faction : factions.values()) {
			if(faction.getComparisonTag().equals(compStr)) {
				return faction;
			}
		}
		return null;
	}

	public Faction getBestTagMatch(String start) {
		int best = 0;
		start = start.toLowerCase();
		int minlength = start.length();
		Faction bestMatch = null;
		for(Faction faction : factions.values()) {
			String candidate = faction.getTag();
			candidate = ChatColor.stripColor(candidate);
			if(candidate.length() < minlength) {
				continue;
			}
			if(!candidate.toLowerCase().startsWith(start)) {
				continue;
			}

			// The closer to zero the better
			int lendiff = candidate.length() - minlength;
			if(lendiff == 0) {
				return faction;
			}
			if(lendiff < best || best == 0) {
				best = lendiff;
				bestMatch = faction;
			}
		}

		return bestMatch;
	}

	public boolean isTagTaken(String str) {
		return this.getByTag(str) != null;
	}

	public boolean isValidFactionId(int id) {
		return factions.containsKey(id);
	}

	public Faction createFaction() {
		Faction faction = generateFactionObject();
		factions.put(faction.getId(), faction);
		return faction;
	}

	public abstract Faction generateFactionObject();

	public void removeFaction(int id) {
		factions.remove(id).remove();
	}

	@Override
	public ArrayList<Faction> getAllFactions() {
		return new ArrayList<>(factions.values());
	}

	@Override
	public Faction getWilderness() {
		return factions.get(0);
	}
}
