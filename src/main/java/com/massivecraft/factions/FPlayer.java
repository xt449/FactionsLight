package com.massivecraft.factions;

import com.massivecraft.factions.perms.Role;
import mkremins.fanciful.FancyMessage;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

/**
 * Logged in players always have exactly one FPlayer instance. Logged out players may or may not have an FPlayer
 * instance. They will always have one if they are part of a faction. This is because only players with a faction are
 * saved to disk (in order to not waste disk space).
 * <p/>
 * The FPlayer is linked to a minecraft player using the player name.
 * <p/>
 * The same instance is always returned for the same player. This means you can use the == operator. No .equals method
 * necessary.
 */

public interface FPlayer extends RelationParticipator {
	void login();

	void logout();

	Faction getFaction();

	int getFactionId();

	boolean hasFaction();

	void setFaction(Faction faction);

	long getLastFrostwalkerMessage();

	void setLastFrostwalkerMessage();

	Role getRole();

	void setRole(Role role);

	boolean shouldTakeFallDamage();

	void setTakeFallDamage(boolean fallDamage);

	Faction getAutoClaimFor();

	void setAutoClaimFor(Faction faction);

	boolean isAdminBypassing();

	void setIsAdminBypassing(boolean val);

	void resetFactionData();

	long getLastLoginTime();

	void resetLastLoginTime();

	void resetLastGraceTime();

	boolean isMapAutoUpdating();

	void setMapAutoUpdating(boolean mapAutoUpdating);

	boolean inGracePeriod();

	FLocation getLastStoodAt();

	void setLastStoodAt(FLocation flocation);

	String getTitle();

	void setTitle(CommandSender sender, String title);

	String getName();

	String getTag();

	// Base concatenations:

	String getNameAndSomething(String something);

	String getNameAndTitle();

	String getNameAndTag();

	int getKills();

	int getDeaths();

	//----------------------------------------------//
	// Territory
	//----------------------------------------------//

	void sendFactionHereMessage();

	// -------------------------------
	// Actions
	// -------------------------------

	void leave();

	boolean canClaimForFaction(Faction forFaction);

	boolean canClaimForFactionAtLocation(Faction forFaction, FLocation location, boolean notifyFailure);

	boolean attemptClaim(Faction forFaction, Location location, boolean notifyFailure);

	boolean attemptClaim(Faction forFaction, FLocation location, boolean notifyFailure);

	String getId();

	Player getPlayer();

	boolean isOnline();

	void sendMessage(String message);

	void sendMessage(List<String> messages);

	void sendFancyMessage(List<FancyMessage> message);

	void setId(String id);
}
