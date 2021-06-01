package com.massivecraft.factions;

import org.bukkit.entity.Player;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.plugin.Plugin;

import java.util.Set;

public interface FactionsAPI {
	/**
	 * Gets the current API version.
	 *
	 * @return current API version
	 * @since API 5
	 */
	default int getAPIVersion() {
		return 5;
	}

	Set<String> getPlayersInFaction(String factionTag);

	Set<String> getOnlinePlayersInFaction(String factionTag);
}
