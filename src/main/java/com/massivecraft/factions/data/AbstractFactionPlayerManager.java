package com.massivecraft.factions.data;

import com.massivecraft.factions.Factions;
import com.massivecraft.factions.FactionsPlugin;
import com.massivecraft.factions.IFactionPlayer;
import com.massivecraft.factions.IFactionPlayerManager;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.util.*;
import java.util.concurrent.ConcurrentSkipListMap;

public abstract class AbstractFactionPlayerManager extends IFactionPlayerManager {
	public Map<String, IFactionPlayer> fPlayers = new ConcurrentSkipListMap<>(String.CASE_INSENSITIVE_ORDER);

	public void clean() {
		for(IFactionPlayer fplayer : this.fPlayers.values()) {
			if(!Factions.getInstance().isValidFactionId(fplayer.getFactionId())) {
				FactionsPlugin.getInstance().log("Reset faction data (invalid faction:" + fplayer.getFactionId() + ") for player " + fplayer.getName());
				fplayer.resetFactionData(false);
			}
		}
	}

	public Collection<IFactionPlayer> getOnlinePlayers() {
		Set<IFactionPlayer> entities = new HashSet<>();
		for(Player player : Bukkit.getServer().getOnlinePlayers()) {
			entities.add(this.getByPlayer(player));
		}
		return entities;
	}

	@Override
	public IFactionPlayer getByPlayer(Player player) {
		return getById(player.getUniqueId().toString());
	}

	@Override
	public List<IFactionPlayer> getAllFPlayers() {
		return new ArrayList<>(fPlayers.values());
	}

	@Override
	public IFactionPlayer getByOfflinePlayer(OfflinePlayer player) {
		return getById(player.getUniqueId().toString());
	}

	@Override
	public IFactionPlayer getById(String id) {
		IFactionPlayer player = fPlayers.get(id);
		if(player == null) {
			player = generateFPlayer(id);
		}
		return player;
	}

	protected abstract IFactionPlayer generateFPlayer(String id);

	public abstract void convertFrom(AbstractFactionPlayerManager old);
}
