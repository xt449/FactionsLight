package com.massivecraft.factions;

import com.massivecraft.factions.util.Localization;
import org.bukkit.OfflinePlayer;

public interface IEconomyParticipator extends IRelationParticipator {

	String getAccountId();

	OfflinePlayer getOfflinePlayer();

	void msg(String str, Object... args);

	void msg(Localization translation, Object... args);
}
