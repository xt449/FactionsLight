package com.massivecraft.factions.data;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.massivecraft.factions.*;
import com.massivecraft.factions.integration.LWCIntegration;
import com.massivecraft.factions.perms.Relation;
import com.massivecraft.factions.util.AsciiCompass;
import com.massivecraft.factions.util.TextUtil;
import mkremins.fanciful.FancyMessage;
import org.bukkit.ChatColor;
import org.bukkit.World;

import java.util.*;
import java.util.Map.Entry;


public abstract class MemoryBoard extends Board {

	public static class MemoryBoardMap extends HashMap<FLocation, Integer> {
		private static final long serialVersionUID = -6689617828610585368L;

		final Multimap<Integer, FLocation> factionToLandMap = HashMultimap.create();

		@Override
		public Integer put(FLocation floc, Integer factionId) {
			Integer previousValue = super.put(floc, factionId);
			if(previousValue != null) {
				factionToLandMap.remove(previousValue, floc);
			}

			factionToLandMap.put(factionId, floc);
			return previousValue;
		}

		@Override
		public Integer remove(Object key) {
			Integer result = super.remove(key);
			if(result != null) {
				FLocation floc = (FLocation) key;
				factionToLandMap.remove(result, floc);
			}

			return result;
		}

		@Override
		public void clear() {
			super.clear();
			factionToLandMap.clear();
		}

		public int getOwnedLandCount(int factionId) {
			return factionToLandMap.get(factionId).size();
		}

		public void removeFaction(int factionId) {
			Collection<FLocation> fLocations = factionToLandMap.removeAll(factionId);
			for(FLocation floc : fLocations) {
				super.remove(floc);
			}
		}
	}

	private final char[] mapKeyChrs = "\\/#$%=&^ABCDEFGHJKLMNOPQRSTUVWXYZ1234567890abcdeghjmnopqrsuvwxyz?".toCharArray();

	public final MemoryBoardMap flocationIds = new MemoryBoardMap();

	//----------------------------------------------//
	// Get and Set
	//----------------------------------------------//
	public int getIdAt(FLocation flocation) {
		if(!flocationIds.containsKey(flocation)) {
			return 0;
		}

		return flocationIds.get(flocation);
	}

	public Faction getFactionAt(FLocation flocation) {
		return Factions.getInstance().getFactionById(getIdAt(flocation));
	}

	public void setIdAt(int id, FLocation flocation) {
		if(id == 0) {
			removeAt(flocation);
		}

		flocationIds.put(flocation, id);
	}

	public void setFactionAt(Faction faction, FLocation flocation) {
		setIdAt(faction.getId(), flocation);
	}

	public void removeAt(FLocation flocation) {
		flocationIds.remove(flocation);
	}

	public Set<FLocation> getAllClaims(int factionId) {
		Set<FLocation> locs = new HashSet<>();
		for(Entry<FLocation, Integer> entry : flocationIds.entrySet()) {
			if(entry.getValue().equals(factionId)) {
				locs.add(entry.getKey());
			}
		}
		return locs;
	}

	public Set<FLocation> getAllClaims(Faction faction) {
		return getAllClaims(faction.getId());
	}

	public void unclaimAll(int factionId) {
		clean(factionId);
	}

	public void unclaimAllInWorld(int factionId, World world) {
		for(FLocation loc : getAllClaims(factionId)) {
			if(loc.getWorldName().equals(world.getName())) {
				removeAt(loc);
			}
		}
	}

	public void clean(int factionId) {
		if(LWCIntegration.getEnabled() && FactionsPlugin.getInstance().configMain.lwc().isResetLocksOnUnclaim()) {
			for(Entry<FLocation, Integer> entry : flocationIds.entrySet()) {
				if(entry.getValue().equals(factionId)) {
					LWCIntegration.clearAllLocks(entry.getKey());
				}
			}
		}

		flocationIds.removeFaction(factionId);
	}

	// Is this coord NOT completely surrounded by coords claimed by the same faction?
	// Simpler: Is there any nearby coord with a faction other than the faction here?
//	public boolean isBorderLocation(FLocation flocation) {
//		Faction faction = getFactionAt(flocation);
//		FLocation a = flocation.getRelative(1, 0);
//		FLocation b = flocation.getRelative(-1, 0);
//		FLocation c = flocation.getRelative(0, 1);
//		FLocation d = flocation.getRelative(0, -1);
//		return faction != getFactionAt(a) || faction != getFactionAt(b) || faction != getFactionAt(c) || faction != getFactionAt(d);
//	}

	// Is this coord connected to any coord claimed by the specified faction?
//	public boolean isConnectedLocation(FLocation flocation, Faction faction) {
//		FLocation a = flocation.getRelative(1, 0);
//		FLocation b = flocation.getRelative(-1, 0);
//		FLocation c = flocation.getRelative(0, 1);
//		FLocation d = flocation.getRelative(0, -1);
//		return faction == getFactionAt(a) || faction == getFactionAt(b) || faction == getFactionAt(c) || faction == getFactionAt(d);
//	}

//	public boolean hasFactionWithin(FLocation flocation, Faction faction, int radius) {
//		for(int x = -radius; x <= radius; x++) {
//			for(int z = -radius; z <= radius; z++) {
//				if(x == 0 && z == 0) {
//					continue;
//				}
//
//				FLocation relative = flocation.getRelative(x, z);
//				Faction other = getFactionAt(relative);
//
//				if(other.isNormal() && other != faction) {
//					return true;
//				}
//			}
//		}
//		return false;
//	}

	//----------------------------------------------//
	// Cleaner. Remove orphaned foreign keys
	//----------------------------------------------//
	public void clean() {
		Iterator<Entry<FLocation, Integer>> iter = flocationIds.entrySet().iterator();
		while(iter.hasNext()) {
			Entry<FLocation, Integer> entry = iter.next();
			if(!Factions.getInstance().isValidFactionId(entry.getValue())) {
				if(LWCIntegration.getEnabled() && FactionsPlugin.getInstance().configMain.lwc().isResetLocksOnUnclaim()) {
					LWCIntegration.clearAllLocks(entry.getKey());
				}
				FactionsPlugin.getInstance().log("Board cleaner removed " + entry.getValue() + " from " + entry.getKey());
				iter.remove();
			}
		}
	}

	//----------------------------------------------//
	// Coord count
	//----------------------------------------------//

	public int getFactionCoordCount(int factionId) {
		return flocationIds.getOwnedLandCount(factionId);
	}

	public int getFactionCoordCount(Faction faction) {
		return getFactionCoordCount(faction.getId());
	}

//	public int getFactionCoordCountInWorld(Faction faction, String worldName) {
//		String factionId = faction.getId();
//		int ret = 0;
//		for(Entry<FLocation, String> entry : flocationIds.entrySet()) {
//			if(entry.getValue().equals(factionId) && entry.getKey().getWorldName().equals(worldName)) {
//				ret += 1;
//			}
//		}
//		return ret;
//	}

	//----------------------------------------------//
	// Map generation
	//----------------------------------------------//

	/**
	 * The map is relative to a coord and a faction north is in the direction of decreasing x east is in the direction
	 * of decreasing z
	 */
	public ArrayList<FancyMessage> getMap(FPlayer fplayer, FLocation flocation, double inDegrees) {
		Faction faction = fplayer.getFaction();
		ArrayList<FancyMessage> ret = new ArrayList<>();
		Faction factionLoc = getFactionAt(flocation);
		ret.add(new FancyMessage(TextUtil.titleize("(" + flocation.getCoordString() + ") " + factionLoc.getTag(fplayer))));

		// Get the compass
		ArrayList<String> asciiCompass = AsciiCompass.getAsciiCompass(inDegrees, ChatColor.RED, TextUtil.parse("<a>"));

		int mapWidth = 35;
		int halfWidth = mapWidth / 2;
		// Use player's value for height
		int mapHeight = 18;
		int halfHeight = mapHeight / 2;
		FLocation topLeft = flocation.getRelative(-halfWidth, -halfHeight);
		int width = halfWidth * 2 + 1;
		int height = halfHeight * 2 + 1;

		if(FactionsPlugin.getInstance().configMain.map().isShowFactionKey()) {
			height--;
		}

		Map<String, Character> fList = new HashMap<>();
		int chrIdx = 0;

		// For each row
		for(int dz = 0; dz < height; dz++) {
			// Draw and add that row
			FancyMessage row = new FancyMessage("");

			if(dz < 3) {
				row.then(asciiCompass.get(dz));
			}
			for(int dx = (dz < 3 ? 6 : 3); dx < width; dx++) {
				if(dx == halfWidth && dz == halfHeight) {
					row.then("+").color(ChatColor.AQUA);
				} else {
					FLocation flocationHere = topLeft.getRelative(dx, dz);
					Faction factionHere = getFactionAt(flocationHere);
					Relation relation = fplayer.getRelationTo(factionHere);
					if(factionHere.isWilderness()) {
						row.then("-").color(FactionsPlugin.getInstance().configMain.colors().factions().wilderness());
					} else if(factionHere == faction || factionHere == factionLoc || relation.isAtLeast(Relation.ALLY) ||
							(FactionsPlugin.getInstance().configMain.map().isShowNeutralFactionsOnMap() && relation.equals(Relation.NEUTRAL)) ||
							(FactionsPlugin.getInstance().configMain.map().isShowEnemyFactions() && relation.equals(Relation.ENEMY)) ||
							FactionsPlugin.getInstance().configMain.map().isShowTruceFactions() && relation.equals(Relation.TRUCE)) {
						if(!fList.containsKey(factionHere.getTag())) {
							fList.put(factionHere.getTag(), this.mapKeyChrs[Math.min(chrIdx++, this.mapKeyChrs.length - 1)]);
						}
						char tag = fList.get(factionHere.getTag());
						row.then(String.valueOf(tag)).color(factionHere.getColorTo(faction));
					} else {
						row.then("-").color(ChatColor.GRAY);
					}
				}
			}
			ret.add(row);
		}

		// Add the faction key
		if(FactionsPlugin.getInstance().configMain.map().isShowFactionKey()) {
			FancyMessage fRow = new FancyMessage("");
			for(String key : fList.keySet()) {
				final Relation relation = fplayer.getRelationTo(Factions.getInstance().getByTag(key));
				fRow.then(String.format("%s: %s ", fList.get(key), key)).color(relation.getColor());
			}
			ret.add(fRow);
		}

		return ret;
	}
}
