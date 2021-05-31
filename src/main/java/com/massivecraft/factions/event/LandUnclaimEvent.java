package com.massivecraft.factions.event;

import com.massivecraft.factions.FLocation;
import com.massivecraft.factions.FPlayer;
import com.massivecraft.factions.Faction;
import org.bukkit.event.Cancellable;

/**
 * Event called when an FPlayer unclaims land for a Faction.
 */
public class LandUnclaimEvent extends FactionPlayerEvent implements Cancellable {

	private boolean cancelled;
	private final FLocation location;

	public LandUnclaimEvent(FLocation loc, Faction f, FPlayer p) {
		super(f, p);
		cancelled = false;
		location = loc;
	}

	public FLocation getLocation() {
		return this.location;
	}

	@Override
	public boolean isCancelled() {
		return cancelled;
	}

	@Override
	public void setCancelled(boolean c) {
		cancelled = c;
	}
}
