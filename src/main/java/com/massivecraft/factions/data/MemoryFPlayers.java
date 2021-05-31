package com.massivecraft.factions.data;

import com.massivecraft.factions.FPlayer;
import com.massivecraft.factions.FPlayers;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.util.*;
import java.util.concurrent.ConcurrentSkipListMap;

public abstract class MemoryFPlayers extends FPlayers {
	public final Map<String, FPlayer> fPlayers = new ConcurrentSkipListMap<>(String.CASE_INSENSITIVE_ORDER);

	public Collection<FPlayer> getOnlinePlayers() {
		Set<FPlayer> entities = new HashSet<>();
		for(Player player : Bukkit.getServer().getOnlinePlayers()) {
			entities.add(this.getByPlayer(player));
		}
		return entities;
	}

	@Override
	public FPlayer getByPlayer(Player player) {
		return getById(player.getUniqueId().toString());
	}

	@Override
	public List<FPlayer> getAllFPlayers() {
		return new ArrayList<>(fPlayers.values());
	}

	@Override
	public FPlayer getByOfflinePlayer(OfflinePlayer player) {
		return getById(player.getUniqueId().toString());
	}

	@Override
	public FPlayer getById(String id) {
		FPlayer player = fPlayers.get(id);
		if(player == null) {
			player = generateFPlayer(id);
		}
		return player;
	}

	protected abstract FPlayer generateFPlayer(String id);
}
