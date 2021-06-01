package com.massivecraft.factions;

import com.massivecraft.factions.combat.Setting;
import com.massivecraft.factions.perms.Permissible;
import com.massivecraft.factions.perms.PermissibleAction;
import com.massivecraft.factions.perms.Relation;
import com.massivecraft.factions.perms.Role;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.Map;
import java.util.Set;

public interface Faction extends RelationParticipator {

	void addAnnouncement(FPlayer fPlayer, String msg);

	void sendUnreadAnnouncements(FPlayer fPlayer);

	void removeAnnouncements(FPlayer fPlayer);

	Set<String> getInvites();

	String getId();

	void invite(FPlayer fplayer);

	void deinvite(FPlayer fplayer);

	boolean isInvited(FPlayer fplayer);

	void ban(FPlayer target, FPlayer banner);

	void unban(FPlayer player);

	boolean isBanned(FPlayer player);

	Set<BanInfo> getBannedPlayers();

	boolean getOpen();

	void setOpen(boolean isOpen);

	Setting getCombatSetting();

	// TODO
//	boolean isPeaceful();

//	void setPeaceful(boolean isPeaceful);

//	void setPeacefulExplosionsEnabled(boolean val);

//	boolean getPeacefulExplosionsEnabled();

//	boolean noExplosionsInTerritory();

	boolean isPermanent();

	void setPermanent(boolean isPermanent);

	String getTag();

	String getTag(Faction otherFaction);

	String getTag(FPlayer otherFplayer);

	void setTag(String str);

	String getComparisonTag();

	String getDescription();

	void setDescription(String value);

	long getFoundedDate();

	void setFoundedDate(long newDate);

	boolean isNormal();

	boolean isWilderness();

	int getKills();

	int getDeaths();

	boolean hasAccess(Permissible permissible, PermissibleAction permissibleAction);

	boolean hasAccess(FPlayer player, PermissibleAction permissibleAction);

	boolean setPermission(Permissible permissible, PermissibleAction permissibleAction, boolean value);

	void checkPerms();

	void resetPerms();

	Map<Permissible, Map<PermissibleAction, Boolean>> getPermissions();

	int getLandRounded();

//	int getLandRoundedInWorld(String worldName);

	// -------------------------------
	// Relation and relation colors
	// -------------------------------

	Relation getRelationWish(Faction otherFaction);

	void setRelationWish(Faction otherFaction, Relation relation);

	int getRelationCount(Relation relation);

	// ----------------------------------------------//
	// DTR
	// ----------------------------------------------//

//	double getDTR();
//
//	double getDTRWithoutUpdate();
//
//	void setDTR(double dtr);
//
//	long getLastDTRUpdateTime();
//
//	long getFrozenDTRUntilTime();
//
//	void setFrozenDTR(long time);
//
//	boolean isFrozenDTR();

	// ----------------------------------------------//
	// Power
	// ----------------------------------------------//

//	double getPower();
//
//	double getPowerMax();
//
//	int getPowerRounded();
//
//	int getPowerMaxRounded();
//
//	Integer getPermanentPower();
//
//	void setPermanentPower(Integer permanentPower);
//
//	boolean hasPermanentPower();
//
//	double getPowerBoost();
//
//	void setPowerBoost(double powerBoost);
//
//	boolean hasLandInflation();
//
//	boolean isPowerFrozen();

	// -------------------------------
	// FPlayers
	// -------------------------------

	boolean addFPlayer(FPlayer fplayer);

	boolean removeFPlayer(FPlayer fplayer);

	Set<FPlayer> getFPlayers();

	Set<FPlayer> getFPlayersWhereOnline(boolean online);

	Set<FPlayer> getFPlayersWhereOnline(boolean online, FPlayer viewer);

	FPlayer getFPlayerAdmin();

	List<FPlayer> getFPlayersWhereRole(Role role);

	List<Player> getOnlinePlayers();

	// slightly faster check than getOnlinePlayers() if you just want to see if
	// there are any players online
	boolean hasPlayersOnline();

	void memberLoggedOff();

	// used when current leader is about to be removed from the faction;
	// promotes new leader, or disbands faction if no other members left
	void promoteNewLeader();

	Role getDefaultRole();

	void setDefaultRole(Role role);

	void sendMessage(String message);

	// ----------------------------------------------//
	// Persistance and entity management
	// ----------------------------------------------//
	void remove();

	Set<FLocation> getAllClaims();

	void setId(String id);
}
