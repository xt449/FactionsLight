package com.massivecraft.factions.data.json;

import com.massivecraft.factions.FPlayers;
import com.massivecraft.factions.FactionsPlugin;
import com.massivecraft.factions.data.MemoryFPlayer;
import com.massivecraft.factions.landraidcontrol.PowerControl;

public class JSONFPlayer extends MemoryFPlayer {

	public JSONFPlayer(String id) {
		super(id);
	}

	@Override
	public void remove() {
		((JSONFPlayers) FPlayers.getInstance()).fPlayers.remove(getId());
	}

//	public boolean shouldBeSaved() {
//		return this.hasFaction() ||
//				(FactionsPlugin.getInstance().getLandRaidControl() instanceof PowerControl &&
//						(this.getPowerRounded() != this.getPowerMaxRounded() &&
//								this.getPowerRounded() != (int) Math.round(FactionsPlugin.getInstance().configMain.factions().landRaidControl().power().getPlayerStarting())));
//	}
}
