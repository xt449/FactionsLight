package com.massivecraft.factions.data;

import com.massivecraft.factions.Factions;
import com.massivecraft.factions.FactionsPlugin;
import com.massivecraft.factions.IFactionClaimManager;
import com.massivecraft.factions.IFactionPlayerManager;

public class SaveTask implements Runnable {

	private static boolean running = false;

	private final FactionsPlugin plugin;

	public SaveTask(FactionsPlugin plugin) {
		this.plugin = plugin;
	}

	public void run() {
		if(!plugin.getAutoSave() || running) {
			return;
		}
		running = true;
		Factions.getInstance().forceSave(false);
		IFactionPlayerManager.getInstance().forceSave(false);
		IFactionClaimManager.getInstance().forceSave(false);
		running = false;
	}
}
