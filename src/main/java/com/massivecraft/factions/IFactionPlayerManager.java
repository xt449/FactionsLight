package com.massivecraft.factions;

import com.massivecraft.factions.data.json.JSONFactionPlayerManager;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.util.Collection;

public abstract class IFactionPlayerManager {
	protected static IFactionPlayerManager instance = getFPlayersImpl();

	public abstract void clean();

	public static IFactionPlayerManager getInstance() {
		return instance;
	}

	private static IFactionPlayerManager getFPlayersImpl() {
		// TODO switch on configuration backend
		return new JSONFactionPlayerManager();
	}

	public abstract Collection<IFactionPlayer> getOnlinePlayers();

	public abstract IFactionPlayer getByPlayer(Player player);

	public abstract Collection<IFactionPlayer> getAllFPlayers();

	public abstract void forceSave();

	public abstract void forceSave(boolean sync);

	public abstract IFactionPlayer getByOfflinePlayer(OfflinePlayer player);

	public abstract IFactionPlayer getById(String string);

	public abstract int load();
}
