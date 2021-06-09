package com.massivecraft.factions;

import com.massivecraft.factions.data.json.JSONFactions;

import java.util.ArrayList;

public abstract class Factions {
	protected static final Factions instance = getFactionsImpl();

	public abstract Faction getFactionById(int id);

	public abstract Faction getByTag(String str);

	public abstract Faction getBestTagMatch(String start);

	public abstract boolean isTagTaken(String str);

	public abstract boolean isValidFactionId(int id);

	public abstract Faction createFaction();

	public abstract void removeFaction(int id);

	public abstract ArrayList<Faction> getAllFactions();

	public abstract Faction getWilderness();

	public abstract void forceSave();

	public abstract void forceSave(boolean sync);

	public static Factions getInstance() {
		return instance;
	}

	private static Factions getFactionsImpl() {
		// TODO switch on configuration backend
		return new JSONFactions();
	}

	public abstract int load();
}
