package com.massivecraft.factions.data;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.massivecraft.factions.*;
import com.massivecraft.factions.integration.LWC;
import com.massivecraft.factions.perms.Relation;
import com.massivecraft.factions.util.AsciiCompass;
import com.massivecraft.factions.util.Localization;
import mkremins.fanciful.FancyMessage;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import java.util.*;
import java.util.Map.Entry;


public abstract class AbstractFactionClaimManager extends IFactionClaimManager {

	public class MemoryBoardMap extends HashMap<FactionClaim, String> {
		private static final long serialVersionUID = -6689617828610585368L;

		Multimap<String, FactionClaim> factionToLandMap = HashMultimap.create();

		@Override
		public String put(FactionClaim floc, String factionId) {
			String previousValue = super.put(floc, factionId);
			if(previousValue != null) {
				factionToLandMap.remove(previousValue, floc);
			}

			factionToLandMap.put(factionId, floc);
			return previousValue;
		}

		@Override
		public String remove(Object key) {
			String result = super.remove(key);
			if(result != null) {
				FactionClaim floc = (FactionClaim) key;
				factionToLandMap.remove(result, floc);
			}

			return result;
		}

		@Override
		public void clear() {
			super.clear();
			factionToLandMap.clear();
		}

		public int getOwnedLandCount(String factionId) {
			return factionToLandMap.get(factionId).size();
		}

		public void removeFaction(String factionId) {
			Collection<FactionClaim> factionClaims = factionToLandMap.removeAll(factionId);
			for(IFactionPlayer fPlayer : IFactionPlayerManager.getInstance().getOnlinePlayers()) {
				if(factionClaims.contains(fPlayer.getLastStoodAt())) {
					if(fPlayer.isWarmingUp()) {
						fPlayer.clearWarmup();
						fPlayer.msg(Localization.WARMUPS_CANCELLED);
					}
				}
			}
			for(FactionClaim floc : factionClaims) {
				super.remove(floc);
			}
		}
	}

	private final char[] mapKeyChrs = "\\/#$%=&^ABCDEFGHJKLMNOPQRSTUVWXYZ1234567890abcdeghjmnopqrsuvwxyz?".toCharArray();

	public MemoryBoardMap flocationIds = new MemoryBoardMap();

	//----------------------------------------------//
	// Get and Set
	//----------------------------------------------//
	public String getIdAt(FactionClaim flocation) {
		if(!flocationIds.containsKey(flocation)) {
			return "0";
		}

		return flocationIds.get(flocation);
	}

	public IFaction getFactionAt(FactionClaim flocation) {
		return Factions.getInstance().getFactionById(getIdAt(flocation));
	}

	public void setIdAt(String id, FactionClaim flocation) {
		clearOwnershipAt(flocation);

		if(id.equals("0")) {
			removeAt(flocation);
		}

		flocationIds.put(flocation, id);
	}

	public void setFactionAt(IFaction faction, FactionClaim flocation) {
		setIdAt(faction.getId(), flocation);
	}

	public void removeAt(FactionClaim flocation) {
		IFaction faction = getFactionAt(flocation);
		faction.getWarps().values().removeIf(lazyLocation -> flocation.isInChunk(lazyLocation.getLocation()));
		for(Entity entity : flocation.getChunk().getEntities()) {
			if(entity instanceof Player) {
				IFactionPlayer fPlayer = IFactionPlayerManager.getInstance().getByPlayer((Player) entity);
				if(fPlayer.isWarmingUp()) {
					fPlayer.clearWarmup();
					fPlayer.msg(Localization.WARMUPS_CANCELLED);
				}
			}
		}
		clearOwnershipAt(flocation);
		flocationIds.remove(flocation);
	}

	public Set<FactionClaim> getAllClaims(String factionId) {
		Set<FactionClaim> locs = new HashSet<>();
		for(Entry<FactionClaim, String> entry : flocationIds.entrySet()) {
			if(entry.getValue().equals(factionId)) {
				locs.add(entry.getKey());
			}
		}
		return locs;
	}

	public Set<FactionClaim> getAllClaims(IFaction faction) {
		return getAllClaims(faction.getId());
	}

	// not to be confused with claims, ownership referring to further member-specific ownership of a claim
	public void clearOwnershipAt(FactionClaim flocation) {
		IFaction faction = getFactionAt(flocation);
		if(faction != null && faction.isNormal()) {
			faction.clearClaimOwnership(flocation);
		}
	}

	public void unclaimAll(String factionId) {
		IFaction faction = Factions.getInstance().getFactionById(factionId);
		if(faction != null && faction.isNormal()) {
			faction.clearAllClaimOwnership();
			faction.clearWarps();
		}
		clean(factionId);
	}

	public void unclaimAllInWorld(String factionId, World world) {
		for(FactionClaim loc : getAllClaims(factionId)) {
			if(loc.getWorldName().equals(world.getName())) {
				removeAt(loc);
			}
		}
	}

	public void clean(String factionId) {
		if(LWC.getEnabled() && FactionsPlugin.getInstance().conf().lwc().isResetLocksOnUnclaim()) {
			for(Entry<FactionClaim, String> entry : flocationIds.entrySet()) {
				if(entry.getValue().equals(factionId)) {
					LWC.clearAllLocks(entry.getKey());
				}
			}
		}

		flocationIds.removeFaction(factionId);
	}

	// Is this coord NOT completely surrounded by coords claimed by the same faction?
	// Simpler: Is there any nearby coord with a faction other than the faction here?
	public boolean isBorderLocation(FactionClaim flocation) {
		IFaction faction = getFactionAt(flocation);
		FactionClaim a = flocation.getRelative(1, 0);
		FactionClaim b = flocation.getRelative(-1, 0);
		FactionClaim c = flocation.getRelative(0, 1);
		FactionClaim d = flocation.getRelative(0, -1);
		return faction != getFactionAt(a) || faction != getFactionAt(b) || faction != getFactionAt(c) || faction != getFactionAt(d);
	}

	// Is this coord connected to any coord claimed by the specified faction?
	public boolean isConnectedLocation(FactionClaim flocation, IFaction faction) {
		FactionClaim a = flocation.getRelative(1, 0);
		FactionClaim b = flocation.getRelative(-1, 0);
		FactionClaim c = flocation.getRelative(0, 1);
		FactionClaim d = flocation.getRelative(0, -1);
		return faction == getFactionAt(a) || faction == getFactionAt(b) || faction == getFactionAt(c) || faction == getFactionAt(d);
	}

	/**
	 * Checks if there is another faction within a given radius other than Wilderness. Used for HCF feature that
	 * requires a 'buffer' between factions.
	 *
	 * @param flocation - center location.
	 * @param faction   - faction checking for.
	 * @param radius    - chunk radius to check.
	 * @return true if another Faction is within the radius, otherwise false.
	 */
	public boolean hasFactionWithin(FactionClaim flocation, IFaction faction, int radius) {
		for(int x = -radius; x <= radius; x++) {
			for(int z = -radius; z <= radius; z++) {
				if(x == 0 && z == 0) {
					continue;
				}

				FactionClaim relative = flocation.getRelative(x, z);
				IFaction other = getFactionAt(relative);

				if(other.isNormal() && other != faction) {
					return true;
				}
			}
		}
		return false;
	}


	//----------------------------------------------//
	// Cleaner. Remove orphaned foreign keys
	//----------------------------------------------//

	public void clean() {
		Iterator<Entry<FactionClaim, String>> iter = flocationIds.entrySet().iterator();
		while(iter.hasNext()) {
			Entry<FactionClaim, String> entry = iter.next();
			if(!Factions.getInstance().isValidFactionId(entry.getValue())) {
				if(LWC.getEnabled() && FactionsPlugin.getInstance().conf().lwc().isResetLocksOnUnclaim()) {
					LWC.clearAllLocks(entry.getKey());
				}
				FactionsPlugin.getInstance().log("Board cleaner removed " + entry.getValue() + " from " + entry.getKey());
				iter.remove();
			}
		}
	}

	//----------------------------------------------//
	// Coord count
	//----------------------------------------------//

	public int getFactionCoordCount(String factionId) {
		return flocationIds.getOwnedLandCount(factionId);
	}

	public int getFactionCoordCount(IFaction faction) {
		return getFactionCoordCount(faction.getId());
	}

	public int getFactionCoordCountInWorld(IFaction faction, String worldName) {
		String factionId = faction.getId();
		int ret = 0;
		for(Entry<FactionClaim, String> entry : flocationIds.entrySet()) {
			if(entry.getValue().equals(factionId) && entry.getKey().getWorldName().equals(worldName)) {
				ret += 1;
			}
		}
		return ret;
	}

	//----------------------------------------------//
	// Map generation
	//----------------------------------------------//

	/**
	 * The map is relative to a coord and a faction north is in the direction of decreasing x east is in the direction
	 * of decreasing z
	 */
	public ArrayList<FancyMessage> getMap(IFactionPlayer fplayer, FactionClaim flocation, double inDegrees) {
		IFaction faction = fplayer.getFaction();
		ArrayList<FancyMessage> ret = new ArrayList<>();
		IFaction factionLoc = getFactionAt(flocation);
		ret.add(new FancyMessage(FactionsPlugin.getInstance().txt().titleize("(" + flocation.getCoordString() + ") " + factionLoc.getTag(fplayer))));

		// Get the compass
		ArrayList<String> asciiCompass = AsciiCompass.getAsciiCompass(inDegrees, ChatColor.RED, FactionsPlugin.getInstance().txt().parse("<a>"));

		int halfWidth = FactionsPlugin.getInstance().conf().map().getWidth() / 2;
		// Use player's value for height
		int halfHeight = fplayer.getMapHeight() / 2;
		FactionClaim topLeft = flocation.getRelative(-halfWidth, -halfHeight);
		int width = halfWidth * 2 + 1;
		int height = halfHeight * 2 + 1;

		if(FactionsPlugin.getInstance().conf().map().isShowFactionKey()) {
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
					if(false) {
						row.tooltip(Localization.CLAIM_YOUAREHERE.toString());
					}
				} else {
					FactionClaim flocationHere = topLeft.getRelative(dx, dz);
					IFaction factionHere = getFactionAt(flocationHere);
					Relation relation = fplayer.getRelationTo(factionHere);
					if(factionHere.isWilderness()) {
						row.then("-").color(FactionsPlugin.getInstance().conf().colors().factions().getWilderness());
					} else if(factionHere.isSafeZone()) {
						row.then("+").color(FactionsPlugin.getInstance().conf().colors().factions().getSafezone());
					} else if(factionHere.isWarZone()) {
						row.then("+").color(FactionsPlugin.getInstance().conf().colors().factions().getWarzone());
					} else if(factionHere == faction || factionHere == factionLoc || relation.isAtLeast(Relation.ALLY) ||
							(FactionsPlugin.getInstance().conf().map().isShowNeutralFactionsOnMap() && relation.equals(Relation.NEUTRAL)) ||
							(FactionsPlugin.getInstance().conf().map().isShowEnemyFactions() && relation.equals(Relation.ENEMY)) ||
							FactionsPlugin.getInstance().conf().map().isShowTruceFactions() && relation.equals(Relation.TRUCE)) {
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
		if(FactionsPlugin.getInstance().conf().map().isShowFactionKey()) {
			FancyMessage fRow = new FancyMessage("");
			for(String key : fList.keySet()) {
				final Relation relation = fplayer.getRelationTo(Factions.getInstance().getByTag(key));
				fRow.then(String.format("%s: %s ", fList.get(key), key)).color(relation.getColor());
			}
			ret.add(fRow);
		}

		return ret;
	}

	public abstract void convertFrom(AbstractFactionClaimManager old);
}
