package com.massivecraft.factions.event;

import com.massivecraft.factions.IFaction;

/**
 * Event called when a faction is disbanded automatically.
 */
public class FactionAutoDisbandEvent extends FactionEvent {
	public FactionAutoDisbandEvent(IFaction faction) {
		super(faction);
	}
}
