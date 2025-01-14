package com.massivecraft.factions.listeners;

import com.massivecraft.factions.FactionsPlugin;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerArmorStandManipulateEvent;

public class OneEightPlusListener extends AbstractListener {
	private final FactionsPlugin plugin;

	public OneEightPlusListener(FactionsPlugin plugin) {
		this.plugin = plugin;
	}

	@EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
	public void onPlayerInteract(PlayerArmorStandManipulateEvent event) {
		if(!plugin.configMain.restrictWorlds().isEnabled(event.getPlayer().getWorld())) {
			return;
		}

		if(!canUseBlock(event.getPlayer(), Material.ARMOR_STAND, event.getRightClicked().getLocation())) {
			event.setCancelled(true);
		}
	}
}
