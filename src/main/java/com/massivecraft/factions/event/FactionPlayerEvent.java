package com.massivecraft.factions.event;

import com.massivecraft.factions.IFaction;
import com.massivecraft.factions.IFactionPlayer;

/**
 * Represents an event involving a Faction and a FPlayer.
 */
public class FactionPlayerEvent extends FactionEvent {

	private final IFactionPlayer fPlayer;

	public FactionPlayerEvent(IFaction faction, IFactionPlayer fPlayer) {
		super(faction);
		this.fPlayer = fPlayer;
	}

	public IFactionPlayer getfPlayer() {
		return this.fPlayer;
	}
}
