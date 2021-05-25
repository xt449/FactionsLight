package com.massivecraft.factions;

import com.massivecraft.factions.data.json.JSONFactionManager;

import java.util.ArrayList;
import java.util.Set;

public abstract class Factions {
	protected static Factions instance = getFactionsImpl();

	public abstract IFaction getFactionById(String id);

	public abstract IFaction getByTag(String str);

	public abstract IFaction getBestTagMatch(String start);

	public abstract boolean isTagTaken(String str);

	public abstract boolean isValidFactionId(String id);

	public abstract IFaction createFaction();

	public abstract void removeFaction(String id);

	public abstract Set<String> getFactionTags();

	public abstract ArrayList<IFaction> getAllFactions();

	@Deprecated
	public abstract IFaction getNone();

	public abstract IFaction getWilderness();

	public abstract IFaction getSafeZone();

	public abstract IFaction getWarZone();

	public abstract void forceSave();

	public abstract void forceSave(boolean sync);

	public static Factions getInstance() {
		return instance;
	}

	private static Factions getFactionsImpl() {
		// TODO switch on configuration backend
		return new JSONFactionManager();
	}

	public abstract int load();
}
