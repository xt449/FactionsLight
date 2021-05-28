package com.massivecraft.factions.iface;

import com.massivecraft.factions.util.Localization;
import org.bukkit.OfflinePlayer;

public interface EconomyParticipator extends RelationParticipator {

	String getAccountId();

	OfflinePlayer getOfflinePlayer();

	void msg(String str, Object... args);

	void msg(Localization translation, Object... args);
}
