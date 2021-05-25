package com.massivecraft.factions;

import com.massivecraft.factions.data.json.JSONFactionClaimManager;
import mkremins.fanciful.FancyMessage;
import org.bukkit.World;

import java.util.List;
import java.util.Set;


public abstract class IFactionClaimManager {
	protected static IFactionClaimManager instance = getBoardImpl();

	//----------------------------------------------//
	// Get and Set
	//----------------------------------------------//
	public abstract String getIdAt(FactionClaim flocation);

	private static IFactionClaimManager getBoardImpl() {
		return new JSONFactionClaimManager(); // TODO switch on configuration backend
	}

	public static IFactionClaimManager getInstance() {
		return instance;
	}

	public abstract IFaction getFactionAt(FactionClaim flocation);

	public abstract void setIdAt(String id, FactionClaim flocation);

	public abstract void setFactionAt(IFaction faction, FactionClaim flocation);

	public abstract void removeAt(FactionClaim flocation);

	public abstract Set<FactionClaim> getAllClaims(String factionId);

	public abstract Set<FactionClaim> getAllClaims(IFaction faction);

	// not to be confused with claims, ownership referring to further member-specific ownership of a claim
	public abstract void clearOwnershipAt(FactionClaim flocation);

	public abstract void unclaimAll(String factionId);

	public abstract void unclaimAllInWorld(String factionId, World world);

	// Is this coord NOT completely surrounded by coords claimed by the same faction?
	// Simpler: Is there any nearby coord with a faction other than the faction here?
	public abstract boolean isBorderLocation(FactionClaim flocation);

	// Is this coord connected to any coord claimed by the specified faction?
	public abstract boolean isConnectedLocation(FactionClaim flocation, IFaction faction);

	public abstract boolean hasFactionWithin(FactionClaim flocation, IFaction faction, int radius);

	//----------------------------------------------//
	// Cleaner. Remove orphaned foreign keys
	//----------------------------------------------//

	public abstract void clean();

	//----------------------------------------------//
	// Coord count
	//----------------------------------------------//

	public abstract int getFactionCoordCount(String factionId);

	public abstract int getFactionCoordCount(IFaction faction);

	public abstract int getFactionCoordCountInWorld(IFaction faction, String worldName);

	//----------------------------------------------//
	// Map generation
	//----------------------------------------------//

	/**
	 * The map is relative to a coord and a faction north is in the direction of decreasing x east is in the direction
	 * of decreasing z
	 */
	public abstract List<FancyMessage> getMap(IFactionPlayer fPlayer, FactionClaim flocation, double inDegrees);

	public abstract void forceSave();

	public abstract void forceSave(boolean sync);

	public abstract int load();
}
