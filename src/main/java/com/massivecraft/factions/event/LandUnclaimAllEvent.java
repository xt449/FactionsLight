package com.massivecraft.factions.event;

import com.massivecraft.factions.FPlayer;
import com.massivecraft.factions.Faction;
import org.bukkit.event.Cancellable;

public class LandUnclaimAllEvent extends FactionPlayerEvent implements Cancellable {
	private boolean cancelled;

	public LandUnclaimAllEvent(Faction f, FPlayer p) {
		super(f, p);
	}

	@Override
	public boolean isCancelled() {
		return cancelled;
	}

	@Override
	public void setCancelled(boolean cancelled) {
		this.cancelled = cancelled;
	}
}
