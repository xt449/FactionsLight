package com.massivecraft.factions;

import com.massivecraft.factions.data.json.JSONFPlayers;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.util.Collection;

public abstract class FPlayers {
	protected static final FPlayers instance = getFPlayersImpl();

	public static FPlayers getInstance() {
		return instance;
	}

	private static FPlayers getFPlayersImpl() {
		// TODO switch on configuration backend
		return new JSONFPlayers();
	}

	public abstract Collection<FPlayer> getOnlinePlayers();

	public abstract FPlayer getByPlayer(Player player);

	public abstract Collection<FPlayer> getAllFPlayers();

	public abstract void forceSave();

	public abstract void forceSave(boolean sync);

	public abstract FPlayer getByOfflinePlayer(OfflinePlayer player);

	public abstract FPlayer getById(String string);

	public abstract int load();
}
