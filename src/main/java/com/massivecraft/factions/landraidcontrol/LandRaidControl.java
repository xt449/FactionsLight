package com.massivecraft.factions.landraidcontrol;

import com.massivecraft.factions.IFaction;
import com.massivecraft.factions.IFactionPlayer;
import com.massivecraft.factions.cmd.CommandContext;
import org.bukkit.entity.Player;

public interface LandRaidControl {
	static LandRaidControl getByName(String name) {
		switch(name.toLowerCase()) {
			case "dtr":
				return new DTRControl();
			case "power":
			default:
				return new PowerControl();
		}
	}

	boolean isRaidable(IFaction faction);

	boolean hasLandInflation(IFaction faction);

	int getLandLimit(IFaction faction);

	default int getPossibleClaimCount(IFaction faction) {
		return this.getLandLimit(faction) - faction.getLandRounded();
	}

	boolean canJoinFaction(IFaction faction, IFactionPlayer player, CommandContext context);

	boolean canLeaveFaction(IFactionPlayer player);

	boolean canDisbandFaction(IFaction faction, CommandContext context);

	boolean canKick(IFactionPlayer toKick, CommandContext context);

	void onRespawn(IFactionPlayer player);

	void onDeath(Player player);

	void onQuit(IFactionPlayer player);

	void onJoin(IFactionPlayer player);

	void update(IFactionPlayer player);
}
