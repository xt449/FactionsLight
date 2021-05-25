package com.massivecraft.factions.event;

import com.massivecraft.factions.IFactionPlayer;
import com.massivecraft.factions.IFactionPlayerManager;
import com.massivecraft.factions.IFaction;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

/**
 * Event called when a Faction is created.
 */
public class FactionCreateEvent extends Event {

	private static final HandlerList handlers = new HandlerList();

	private final String factionTag;
	private final Player sender;
	private final IFaction faction;

	public FactionCreateEvent(Player sender, String tag, IFaction faction) {
		this.factionTag = tag;
		this.sender = sender;
		this.faction = faction;
	}

	public IFactionPlayer getFPlayer() {
		return IFactionPlayerManager.getInstance().getByPlayer(sender);
	}

	@Deprecated
	public String getFactionTag() {
		return factionTag;
	}

	public IFaction getFaction() {
		return this.faction;
	}

	@Override
	public HandlerList getHandlers() {
		return handlers;
	}

	public static HandlerList getHandlerList() {
		return handlers;
	}

}
