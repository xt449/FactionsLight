package com.massivecraft.factions.event;

import com.massivecraft.factions.IFaction;
import com.massivecraft.factions.perms.Relation;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

/**
 * Event called when a Faction relation is called.
 */
public class FactionRelationEvent extends Event {

	private static final HandlerList handlers = new HandlerList();

	private final IFaction fsender;
	private final IFaction ftarget;
	private final Relation foldrel;
	private final Relation frel;

	public FactionRelationEvent(IFaction sender, IFaction target, Relation oldrel, Relation rel) {
		fsender = sender;
		ftarget = target;
		foldrel = oldrel;
		frel = rel;
	}

	public HandlerList getHandlers() {
		return handlers;
	}

	public static HandlerList getHandlerList() {
		return handlers;
	}

	public Relation getOldRelation() {
		return foldrel;
	}

	public Relation getRelation() {
		return frel;
	}

	public IFaction getFaction() {
		return fsender;
	}

	public IFaction getTargetFaction() {
		return ftarget;
	}
}
