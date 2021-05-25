package com.massivecraft.factions;

import com.massivecraft.factions.perms.Permissible;
import com.massivecraft.factions.perms.PermissibleAction;
import com.massivecraft.factions.perms.Relation;
import com.massivecraft.factions.perms.Role;
import com.massivecraft.factions.struct.BanInfo;
import com.massivecraft.factions.util.LazyLocation;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.Map;
import java.util.Set;

public interface IFaction extends IEconomyParticipator {
	Map<String, List<String>> getAnnouncements();

	Map<String, LazyLocation> getWarps();

	LazyLocation getWarp(String name);

	void setWarp(String name, LazyLocation loc);

	boolean isWarp(String name);

	boolean hasWarpPassword(String warp);

	boolean isWarpPassword(String warp, String password);

	void setWarpPassword(String warp, String password);

	boolean removeWarp(String name);

	void clearWarps();

	int getMaxVaults();

	void setMaxVaults(int value);

	void addAnnouncement(IFactionPlayer fPlayer, String msg);

	void sendUnreadAnnouncements(IFactionPlayer fPlayer);

	void removeAnnouncements(IFactionPlayer fPlayer);

	Set<String> getInvites();

	String getId();

	void invite(IFactionPlayer fplayer);

	void deinvite(IFactionPlayer fplayer);

	boolean isInvited(IFactionPlayer fplayer);

	void ban(IFactionPlayer target, IFactionPlayer banner);

	void unban(IFactionPlayer player);

	boolean isBanned(IFactionPlayer player);

	Set<BanInfo> getBannedPlayers();

	boolean getOpen();

	void setOpen(boolean isOpen);

	boolean isPeaceful();

	void setPeaceful(boolean isPeaceful);

	void setPeacefulExplosionsEnabled(boolean val);

	boolean getPeacefulExplosionsEnabled();

	boolean noExplosionsInTerritory();

	boolean isPermanent();

	void setPermanent(boolean isPermanent);

	String getTag();

	String getTag(String prefix);

	String getTag(IFaction otherFaction);

	String getTag(IFactionPlayer otherFplayer);

	void setTag(String str);

	String getComparisonTag();

	String getDescription();

	void setDescription(String value);

	void setHome(Location home);

	void delHome();

	boolean hasHome();

	Location getHome();

	long getFoundedDate();

	void setFoundedDate(long newDate);

	void confirmValidHome();

	boolean noPvPInTerritory();

	boolean noMonstersInTerritory();

	boolean isNormal();

	@Deprecated
	boolean isNone();

	boolean isWilderness();

	boolean isSafeZone();

	boolean isWarZone();

	boolean isPlayerFreeType();

	void setLastDeath(long time);

	int getKills();

	int getDeaths();

	boolean hasAccess(boolean online, Permissible permissible, PermissibleAction permissibleAction);

	boolean hasAccess(IFactionPlayer player, PermissibleAction permissibleAction);

	boolean isLocked(boolean online, Permissible permissible, PermissibleAction permissibleAction);

	boolean setPermission(boolean online, Permissible permissible, PermissibleAction permissibleAction, boolean value);

	void checkPerms();

	void resetPerms();

	Map<Permissible, Map<PermissibleAction, Boolean>> getPermissions();

	int getLandRounded();

	int getLandRoundedInWorld(String worldName);

	int getTNTBank();

	void setTNTBank(int amount);

	// -------------------------------
	// Relation and relation colors
	// -------------------------------

	Relation getRelationWish(IFaction otherFaction);

	void setRelationWish(IFaction otherFaction, Relation relation);

	int getRelationCount(Relation relation);

	// ----------------------------------------------//
	// DTR
	// ----------------------------------------------//

	double getDTR();

	double getDTRWithoutUpdate();

	void setDTR(double dtr);

	long getLastDTRUpdateTime();

	long getFrozenDTRUntilTime();

	void setFrozenDTR(long time);

	boolean isFrozenDTR();

	// ----------------------------------------------//
	// Power
	// ----------------------------------------------//
	double getPower();

	double getPowerMax();

	int getPowerRounded();

	int getPowerMaxRounded();

	Integer getPermanentPower();

	void setPermanentPower(Integer permanentPower);

	boolean hasPermanentPower();

	double getPowerBoost();

	void setPowerBoost(double powerBoost);

	boolean hasLandInflation();

	boolean isPowerFrozen();

	// -------------------------------
	// FPlayers
	// -------------------------------

	// maintain the reference list of FPlayers in this faction
	void refreshFPlayers();

	boolean addFPlayer(IFactionPlayer fplayer);

	boolean removeFPlayer(IFactionPlayer fplayer);

	int getSize();

	Set<IFactionPlayer> getFPlayers();

	Set<IFactionPlayer> getFPlayersWhereOnline(boolean online);

	Set<IFactionPlayer> getFPlayersWhereOnline(boolean online, IFactionPlayer viewer);

	IFactionPlayer getFPlayerAdmin();

	List<IFactionPlayer> getFPlayersWhereRole(Role role);

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

	void sendMessage(List<String> messages);

	// ----------------------------------------------//
	// Ownership of specific claims
	// ----------------------------------------------//

	Map<FactionClaim, Set<String>> getClaimOwnership();

	void clearAllClaimOwnership();

	void clearClaimOwnership(FactionClaim loc);

	void clearClaimOwnership(IFactionPlayer player);

	int getCountOfClaimsWithOwners();

	boolean doesLocationHaveOwnersSet(FactionClaim loc);

	boolean isPlayerInOwnerList(IFactionPlayer player, FactionClaim loc);

	void setPlayerAsOwner(IFactionPlayer player, FactionClaim loc);

	void removePlayerAsOwner(IFactionPlayer player, FactionClaim loc);

	Set<String> getOwnerList(FactionClaim loc);

	String getOwnerListString(FactionClaim loc);

	boolean playerHasOwnershipRights(IFactionPlayer fplayer, FactionClaim loc);

	// ----------------------------------------------//
	// Persistance and entity management
	// ----------------------------------------------//
	void remove();

	Set<FactionClaim> getAllClaims();

	void setId(String id);

	OfflinePlayer getOfflinePlayer();
}
