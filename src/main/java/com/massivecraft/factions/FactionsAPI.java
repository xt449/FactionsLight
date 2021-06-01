package com.massivecraft.factions;

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
