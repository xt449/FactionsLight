package com.massivecraft.factions.data.json;

import com.massivecraft.factions.FactionsPlugin;
import com.massivecraft.factions.IFactionPlayerManager;
import com.massivecraft.factions.data.AbstractFactionPlayer;
import com.massivecraft.factions.landraidcontrol.PowerControl;

public class JSONFactionPlayer extends AbstractFactionPlayer {

	public JSONFactionPlayer(AbstractFactionPlayer arg0) {
		super(arg0);
	}

	public JSONFactionPlayer(String id) {
		super(id);
	}

	@Override
	public void remove() {
		((JSONFactionPlayerManager) IFactionPlayerManager.getInstance()).fPlayers.remove(getId());
	}

	public boolean shouldBeSaved() {
		return this.hasFaction() ||
				(FactionsPlugin.getInstance().getLandRaidControl() instanceof PowerControl &&
						(this.getPowerRounded() != this.getPowerMaxRounded() &&
								this.getPowerRounded() != (int) Math.round(FactionsPlugin.getInstance().conf().factions().landRaidControl().power().getPlayerStarting())));
	}
}
