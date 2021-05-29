package com.massivecraft.factions.integration;

import com.massivecraft.factions.FactionsPlugin;
import net.milkbowl.vault.permission.Permission;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.plugin.RegisteredServiceProvider;

public class VaultPermission {
	private Permission permission = null;

	public VaultPermission() {
		try {
			RegisteredServiceProvider<Permission> rsp = Bukkit.getServer().getServicesManager().getRegistration(Permission.class);
			if(rsp != null) {
				permission = rsp.getProvider();
			}
		} catch(NoClassDefFoundError ex) {
			return;
		}
		if(permission != null) {
			FactionsPlugin.getInstance().getLogger().info("Using Vault with permissions plugin " + permission.getName());
		}
	}

	public String getName() {
		return permission == null ? "nope" : permission.getName();
	}

	public Object getPermission() {
		return permission;
	}

	public String getPrimaryGroup(OfflinePlayer player) {
		return permission == null || !permission.hasGroupSupport() ? " " : permission.getPrimaryGroup(Bukkit.getWorlds().get(0).toString(), player);
	}
}
