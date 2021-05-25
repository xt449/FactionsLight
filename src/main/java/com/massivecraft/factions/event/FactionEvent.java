package com.massivecraft.factions.event;

import com.massivecraft.factions.IFaction;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

/**
 * Represents an event involving a Faction.
 */
public class FactionEvent extends Event {

	private static final HandlerList handlers = new HandlerList();
	private final IFaction faction;

	public FactionEvent(IFaction faction) {
		this.faction = faction;
	}

	/**
	 * Get the Faction involved in the event.
	 *
	 * @return faction involved in the event.
	 */
	public IFaction getFaction() {
		return this.faction;
	}

	public HandlerList getHandlers() {
		return handlers;
	}

	public static HandlerList getHandlerList() {
		return handlers;
	}

}
