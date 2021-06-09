package com.massivecraft.factions.data;

import com.massivecraft.factions.*;
import com.massivecraft.factions.combat.Setting;
import com.massivecraft.factions.configuration.DefaultPermissionsConfiguration;
import com.massivecraft.factions.event.FactionAutoDisbandEvent;
import com.massivecraft.factions.perms.Permissible;
import com.massivecraft.factions.perms.PermissibleAction;
import com.massivecraft.factions.perms.Relation;
import com.massivecraft.factions.perms.Role;
import com.massivecraft.factions.util.MiscUtil;
import com.massivecraft.factions.util.RelationUtil;
import com.massivecraft.factions.util.TL;
import com.massivecraft.factions.util.TextUtil;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.*;

public class MemoryFaction implements Faction {
	protected int id;
	//	protected boolean peacefulExplosionsEnabled;
	protected boolean permanent;
	protected String tag;
	protected String description;
	protected boolean open;
	//	protected boolean peaceful;
	//	protected Integer permanentPower;
	protected long foundedDate;
	protected transient long lastPlayerLoggedOffTime;
	//	protected double powerBoost;
	protected final Map<Integer, Relation> relationWish = new HashMap<>();
	protected final Set<String> invites = new HashSet<>();
	//	private long lastDeath;
	protected Role defaultRole;
	protected final Map<Permissible, Map<PermissibleAction, Boolean>> permissions = new HashMap<>();
	protected final Set<BanInfo> bans = new HashSet<>();
	//	protected double dtr;
//	protected long lastDTRUpdateTime;
//	protected long frozenDTRUntilTime;
	private final transient Set<FPlayer> fplayers;

	// Needed for GSON deserialization
	@SuppressWarnings("unused")
	public MemoryFaction() {
		fplayers = new HashSet<>();
	}

	public MemoryFaction(int id) {
		this.id = id;
		this.open = false;
		this.tag = "???";
		this.description = TL.GENERIC_DEFAULTDESCRIPTION.toString();
		this.lastPlayerLoggedOffTime = 0;
//		this.peaceful = FactionsPlugin.getInstance().configMain.factions().limits().isNewFactionsDefaultPeaceful();
//		this.peacefulExplosionsEnabled = false;
		this.permanent = false;
//		this.powerBoost = 0.0;
		this.foundedDate = System.currentTimeMillis();
		this.defaultRole = FactionsPlugin.getInstance().configMain.factions().roles().defaultRole();
//		this.dtr = FactionsPlugin.getInstance().configMain.factions().landRaidControl().dtr().getStartingDTR();

		fplayers = new HashSet<>();
		resetPerms(); // Reset on new Faction so it has default values.
	}

	public Set<String> getInvites() {
		return invites;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public void invite(FPlayer fplayer) {
		this.invites.add(fplayer.getId());
	}

	public void deinvite(FPlayer fplayer) {
		this.invites.remove(fplayer.getId());
	}

	public boolean isInvited(FPlayer fplayer) {
		return this.invites.contains(fplayer.getId());
	}

	public void ban(FPlayer target, FPlayer banner) {
		BanInfo info = new BanInfo(banner.getId(), target.getId(), System.currentTimeMillis());
		this.bans.add(info);
	}

	public void unban(FPlayer player) {
		bans.removeIf(banInfo -> banInfo.getBanned().equalsIgnoreCase(player.getId()));
	}

	public boolean isBanned(FPlayer player) {
		for(BanInfo info : bans) {
			if(info.getBanned().equalsIgnoreCase(player.getId())) {
				return true;
			}
		}

		return false;
	}

	public Set<BanInfo> getBannedPlayers() {
		return this.bans;
	}

	public boolean getOpen() {
		return open;
	}

	public void setOpen(boolean isOpen) {
		open = isOpen;
	}

	@Override
	public Setting getCombatSetting() {
		// TODO
		return Setting.DEFAULT;
	}

//	public boolean isPeaceful() {
//		return this.peaceful;
//	}

//	public void setPeaceful(boolean isPeaceful) {
//		this.peaceful = isPeaceful;
//	}

//	public void setPeacefulExplosionsEnabled(boolean val) {
//		peacefulExplosionsEnabled = val;
//	}

//	public boolean getPeacefulExplosionsEnabled() {
//		return this.peacefulExplosionsEnabled;
//	}

//	public boolean noExplosionsInTerritory() {
//		return this.peaceful && !peacefulExplosionsEnabled;
//	}

	public boolean isPermanent() {
		return permanent || !this.isNormal();
	}

	public void setPermanent(boolean isPermanent) {
		permanent = isPermanent;
	}

	public String getTag() {
		return this.tag;
	}

	public String getTag(Faction otherFaction) {
		if(otherFaction == null) {
			return getTag();
		}
		return this.getColorTo(otherFaction).toString() + this.tag;
	}

	public String getTag(FPlayer otherFplayer) {
		if(otherFplayer == null) {
			return getTag();
		}
		return this.getColorTo(otherFplayer).toString() + this.tag;
	}

	public void setTag(String str) {
		this.tag = str;
	}

	public String getComparisonTag() {
		return MiscUtil.getComparisonString(this.tag);
	}

	public String getDescription() {
		return this.description;
	}

	public void setDescription(String value) {
		this.description = value;
	}

	public long getFoundedDate() {
		if(this.foundedDate == 0) {
			setFoundedDate(System.currentTimeMillis());
		}
		return this.foundedDate;
	}

	public void setFoundedDate(long newDate) {
		this.foundedDate = newDate;
	}

	public int getKills() {
		int kills = 0;
		for(FPlayer fp : getFPlayers()) {
			kills += fp.getKills();
		}

		return kills;
	}

	public int getDeaths() {
		int deaths = 0;
		for(FPlayer fp : getFPlayers()) {
			deaths += fp.getDeaths();
		}

		return deaths;
	}

	// -------------------------------------------- //
	// F Permissions stuff
	// -------------------------------------------- //


	public boolean hasAccess(Permissible permissible, PermissibleAction permissibleAction) {
		if(permissible == null || permissibleAction == null) {
			return false; // Fail in a safe way
		}
		if(permissible == Role.ADMIN) {
			return true;
		}

		Map<Permissible, Map<PermissibleAction, Boolean>> permissionsMap = this.getPermissionsMap();

		DefaultPermissionsConfiguration.PermissiblePermInfo permInfo = this.getPermInfo(permissible, permissibleAction);
		if(permInfo == null) { // Not valid lookup, like a role-only lookup of a relation
			return false;
		}

		Map<PermissibleAction, Boolean> accessMap = permissionsMap.get(permissible);
		if(accessMap != null && accessMap.containsKey(permissibleAction)) {
			return accessMap.get(permissibleAction);
		}

		return permInfo.defaultAllowed(); // Fall back on default if something went wrong
	}

	private DefaultPermissionsConfiguration.PermissiblePermInfo getPermInfo(Permissible permissible, PermissibleAction permissibleAction) {
		return permissibleAction.getPermInfo(FactionsPlugin.getInstance().configDefaultPermissions).get(permissible);
	}

	private Map<Permissible, Map<PermissibleAction, Boolean>> getPermissionsMap() {
		return this.permissions;
	}

	/**
	 * Get the Access of a player. Will use player's Role if they are a faction member. Otherwise, uses their Relation.
	 *
	 * @param player            player
	 * @param permissibleAction permissible
	 * @return player's access
	 */
	public boolean hasAccess(FPlayer player, PermissibleAction permissibleAction) {
		if(player == null || permissibleAction == null) {
			return false; // Fail in a safe way
		}

		Permissible perm;
		if(player.getFaction() == this) {
			perm = player.getRole();
		} else {
			perm = player.getFaction().getRelationTo(this);
		}

		return this.hasAccess(perm, permissibleAction);
	}

	public boolean setPermission(Permissible permissible, PermissibleAction permissibleAction, boolean value) {
		Map<Permissible, Map<PermissibleAction, Boolean>> permissionsMap = this.getPermissionsMap();

		DefaultPermissionsConfiguration.PermissiblePermInfo permInfo = this.getPermInfo(permissible, permissibleAction);
		if(permInfo == null) {
			return false;
		}

		Map<PermissibleAction, Boolean> accessMap = permissionsMap.get(permissible);
		if(accessMap == null) {
			accessMap = new HashMap<>();
		}

		accessMap.put(permissibleAction, value);
		return true;
	}

	public void checkPerms() {
		if(this.permissions.isEmpty()) {
			this.resetPerms();
		} else {
			this.updatePerms(this.permissions, FactionsPlugin.getInstance().configDefaultPermissions);
		}
	}

	public void resetPerms() {
		this.resetPerms(this.permissions, FactionsPlugin.getInstance().configDefaultPermissions);
	}

	private void resetPerms(Map<Permissible, Map<PermissibleAction, Boolean>> permissions, DefaultPermissionsConfiguration defaults) {
		permissions.clear();

//		for(Relation relation : Relation.values()) {
//			if(relation != Relation.MEMBER) {
//				permissions.put(relation, new HashMap<>());
//			}
//		}
		for(Role role : Role.values()) {
			if(role != Role.ADMIN) {
				permissions.put(role, new HashMap<>());
			}
		}

		for(Map.Entry<Permissible, Map<PermissibleAction, Boolean>> entry : permissions.entrySet()) {
			for(PermissibleAction permissibleAction : PermissibleAction.values()) {
				entry.getValue().put(permissibleAction, permissibleAction.getPermInfo(defaults).get(entry.getKey()).defaultAllowed());
			}
		}
	}

	private void updatePerms(Map<Permissible, Map<PermissibleAction, Boolean>> permissions, DefaultPermissionsConfiguration defaults) {
//		for(Relation relation : Relation.values()) {
//			if(relation != Relation.MEMBER) {
//				permissions.computeIfAbsent(relation, p -> new HashMap<>());
//			}
//		}
		for(Role role : Role.values()) {
			if(role != Role.ADMIN) {
				permissions.computeIfAbsent(role, p -> new HashMap<>());
			}
		}

		for(Map.Entry<Permissible, Map<PermissibleAction, Boolean>> entry : permissions.entrySet()) {
			for(PermissibleAction permissibleAction : PermissibleAction.values()) {
				entry.getValue().computeIfAbsent(permissibleAction, p -> p.getPermInfo(defaults).get(entry.getKey()).defaultAllowed());
			}
			entry.getValue().remove(null);
		}
	}

	/**
	 * Read only map of Permissions.
	 *
	 * @return map of permissions
	 */
	public Map<Permissible, Map<PermissibleAction, Boolean>> getPermissions() {
		return Collections.unmodifiableMap(permissions);
	}

	public Role getDefaultRole() {
		return this.defaultRole;
	}

	public void setDefaultRole(Role role) {
		this.defaultRole = role;
	}

	// -------------------------------
	// Understand the type
	// -------------------------------

	public boolean isNormal() {
		return id != 0;
	}

	public boolean isWilderness() {
		return id == 0;
	}

	// -------------------------------
	// Relation and relation colors
	// -------------------------------

	@Override
	public String describeTo(RelationParticipator that, boolean ucfirst) {
		return RelationUtil.describeThatToMe(this, that, ucfirst);
	}

	@Override
	public String describeTo(RelationParticipator that) {
		return RelationUtil.describeThatToMe(this, that);
	}

	@Override
	public Relation getRelationTo(RelationParticipator rp) {
		return RelationUtil.getRelationTo(this, rp);
	}

	@Override
	public Relation getRelationTo(RelationParticipator rp, boolean ignorePeaceful) {
		return RelationUtil.getRelationTo(this, rp, ignorePeaceful);
	}

	@Override
	public ChatColor getColorTo(RelationParticipator rp) {
		return RelationUtil.getColorOfThatToMe(this, rp);
	}

	public Relation getRelationWish(Faction otherFaction) {
		if(this.relationWish.containsKey(otherFaction.getId())) {
			return this.relationWish.get(otherFaction.getId());
		}
		return FactionsPlugin.getInstance().configMain.factions().roles().defaultRelation(); // Always default to old behavior.
	}

	public void setRelationWish(Faction otherFaction, Relation relation) {
		if(this.relationWish.containsKey(otherFaction.getId()) && relation.equals(Relation.NEUTRAL)) {
			this.relationWish.remove(otherFaction.getId());
		} else {
			this.relationWish.put(otherFaction.getId(), relation);
		}
	}

	public int getRelationCount(Relation relation) {
		int count = 0;
		for(Faction faction : Factions.getInstance().getAllFactions()) {
			if(faction.getRelationTo(this) == relation) {
				count++;
			}
		}
		return count;
	}

	// ----------------------------------------------//
	// DTR
	// ----------------------------------------------//

//	@Override
//	public double getDTR() {
//		LandRaidControl lrc = FactionsPlugin.getInstance().getLandRaidControl();
//		if(lrc instanceof DTRControl) {
//			((DTRControl) lrc).updateDTR(this);
//		}
//		return this.dtr;
//	}
//
//	@Override
//	public double getDTRWithoutUpdate() {
//		return this.dtr;
//	}
//
//	@Override
//	public void setDTR(double dtr) {
//		this.dtr = dtr;
//		this.lastDTRUpdateTime = System.currentTimeMillis();
//	}
//
//	@Override
//	public long getLastDTRUpdateTime() {
//		return this.lastDTRUpdateTime;
//	}
//
//	@Override
//	public long getFrozenDTRUntilTime() {
//		return this.frozenDTRUntilTime;
//	}
//
//	@Override
//	public void setFrozenDTR(long time) {
//		this.frozenDTRUntilTime = time;
//	}
//
//	@Override
//	public boolean isFrozenDTR() {
//		return System.currentTimeMillis() < this.frozenDTRUntilTime;
//	}

	// ----------------------------------------------//
	// Power
	// ----------------------------------------------//

//	@Deprecated
//	public double getPower() {
//		if(this.hasPermanentPower()) {
//			return this.getPermanentPower();
//		}
//
//		double ret = 0;
//		for(FPlayer fplayer : fplayers) {
//			ret += fplayer.getPower();
//		}
//		if(FactionsPlugin.getInstance().configMain.factions().landRaidControl().power().getFactionMax() > 0 && ret > FactionsPlugin.getInstance().configMain.factions().landRaidControl().power().getFactionMax()) {
//			ret = FactionsPlugin.getInstance().configMain.factions().landRaidControl().power().getFactionMax();
//		}
//		return ret + this.powerBoost;
//	}
//
//	@Deprecated
//	public double getPowerMax() {
//		if(this.hasPermanentPower()) {
//			return this.getPermanentPower();
//		}
//
//		double ret = 0;
//		for(FPlayer fplayer : fplayers) {
//			ret += fplayer.getPowerMax();
//		}
//		if(FactionsPlugin.getInstance().configMain.factions().landRaidControl().power().getFactionMax() > 0 && ret > FactionsPlugin.getInstance().configMain.factions().landRaidControl().power().getFactionMax()) {
//			ret = FactionsPlugin.getInstance().configMain.factions().landRaidControl().power().getFactionMax();
//		}
//		return ret + this.powerBoost;
//	}
//
//	public int getPowerRounded() {
//		return (int) Math.round(this.getPower());
//	}
//
//	public int getPowerMaxRounded() {
//		return (int) Math.round(this.getPowerMax());
//	}
//
//	public boolean hasLandInflation() {
//		return FactionsPlugin.getInstance().getLandRaidControl().hasLandInflation(this);
//	}
//
//	public Integer getPermanentPower() {
//		return this.permanentPower;
//	}
//
//	public void setPermanentPower(Integer permanentPower) {
//		this.permanentPower = permanentPower;
//	}
//
//	public boolean hasPermanentPower() {
//		return this.permanentPower != null;
//	}
//
//	public double getPowerBoost() {
//		return this.powerBoost;
//	}
//
//	public void setPowerBoost(double powerBoost) {
//		this.powerBoost = powerBoost;
//	}
//
//	public boolean isPowerFrozen() {
//		int freezeSeconds = FactionsPlugin.getInstance().configMain.factions().landRaidControl().power().getPowerFreeze();
//		return freezeSeconds != 0 && System.currentTimeMillis() - lastDeath < freezeSeconds * 1000L;
//	}

	public int getLandRounded() {
		return Board.getInstance().getFactionCoordCount(this);
	}

//	public int getLandRoundedInWorld(String worldName) {
//		return Board.getInstance().getFactionCoordCountInWorld(this, worldName);
//	}

//	@Override
//	public void initFix() {
//		if(fplayers == null) {
//			fplayers = new HashSet<>();
//		}
//	}

	// -------------------------------
	// FPlayers
	// -------------------------------

	public boolean addFPlayer(FPlayer fplayer) {
		return fplayers.add(fplayer);
	}

	public boolean removeFPlayer(FPlayer fplayer) {
		return fplayers.remove(fplayer);
	}

	public Set<FPlayer> getFPlayers() {
		// return a shallow copy of the FPlayer list, to prevent tampering and
		// concurrency issues
		return new HashSet<>(fplayers);
	}

	public Set<FPlayer> getFPlayersWhereOnline(boolean online) {
		Set<FPlayer> ret = new HashSet<>();
		if(!this.isNormal()) {
			return ret;
		}

		for(FPlayer fplayer : fplayers) {
			if(fplayer.isOnline() == online) {
				ret.add(fplayer);
			}
		}

		return ret;
	}

	public Set<FPlayer> getFPlayersWhereOnline(boolean online, FPlayer viewer) {
		Set<FPlayer> ret = new HashSet<>();
		if(!this.isNormal()) {
			return ret;
		}

		for(FPlayer viewed : fplayers) {
			// Add if their online status is what we want
			if(viewed.isOnline() == online) {
				// If we want online, check to see if we are able to see this player
				// This checks if they are in vanish.
				if(online
						&& viewed.getPlayer() != null
						&& viewer.getPlayer() != null
						&& viewer.getPlayer().canSee(viewed.getPlayer())) {
					ret.add(viewed);
					// If we want offline, just add them.
					// Prob a better way to do this but idk.
				} else if(!online) {
					ret.add(viewed);
				}
			}
		}

		return ret;
	}

	public FPlayer getFPlayerAdmin() {
		if(!this.isNormal()) {
			return null;
		}

		for(FPlayer fplayer : fplayers) {
			if(fplayer.getRole() == Role.ADMIN) {
				return fplayer;
			}
		}
		return null;
	}

	public ArrayList<FPlayer> getFPlayersWhereRole(Role role) {
		ArrayList<FPlayer> ret = new ArrayList<>();
		if(!this.isNormal()) {
			return ret;
		}

		for(FPlayer fplayer : fplayers) {
			if(fplayer.getRole() == role) {
				ret.add(fplayer);
			}
		}

		return ret;
	}

	public ArrayList<Player> getOnlinePlayers() {
		ArrayList<Player> ret = new ArrayList<>();

		for(Player player : FactionsPlugin.getInstance().getServer().getOnlinePlayers()) {
			FPlayer fplayer = FPlayers.getInstance().getByPlayer(player);
			if(fplayer.getFaction() == this) {
				ret.add(player);
			}
		}

		return ret;
	}

	// slightly faster check than getOnlinePlayers() if you just want to see if
	// there are any players online
	// TODO
	public boolean hasPlayersOnline() {
		// only real factions can have players online, not safe zone / war zone

		for(Player player : FactionsPlugin.getInstance().getServer().getOnlinePlayers()) {
			FPlayer fplayer = FPlayers.getInstance().getByPlayer(player);
			if(fplayer != null && fplayer.getFaction() == this) {
				return true;
			}
		}

		// even if all players are technically logged off, maybe someone was on
		// recently enough to not consider them officially offline yet
		return System.currentTimeMillis() < lastPlayerLoggedOffTime + 60000;
	}

	public void memberLoggedOff() {
		if(this.isNormal()) {
			lastPlayerLoggedOffTime = System.currentTimeMillis();
		}
	}

	// used when current leader is about to be removed from the faction;
	// promotes new leader, or disbands faction if no other members left
	public void promoteNewLeader() {
		if(!this.isNormal()) {
			return;
		}
		if(this.isPermanent()) {
			return;
		}

		FPlayer oldLeader = this.getFPlayerAdmin();

		// get list of coleaders, or mods, or list of normal members if there are no moderators
		ArrayList<FPlayer> replacements = this.getFPlayersWhereRole(Role.COLEADER);
		if(replacements == null || replacements.isEmpty()) {
			replacements = this.getFPlayersWhereRole(Role.MODERATOR);
		}

		if(replacements == null || replacements.isEmpty()) {
			replacements = this.getFPlayersWhereRole(Role.NORMAL);
		}

		if(replacements == null || replacements.isEmpty()) { // faction admin  is the only  member; one-man  faction
			if(this.isPermanent()) {
				if(oldLeader != null) {
					oldLeader.setRole(Role.NORMAL);
				}
				return;
			}

			// no members left and faction isn't permanent, so disband it
			if(FactionsPlugin.getInstance().configMain.logging().factionDisband()) {
				FactionsPlugin.getInstance().log("The faction " + this.getTag() + " (" + this.getId() + ") has been disbanded since it has no members left.");
			}

			for(FPlayer fplayer : FPlayers.getInstance().getOnlinePlayers()) {
				fplayer.msg(TL.LEAVE_DISBANDED, this.getTag(fplayer));
			}

			FactionsPlugin.getInstance().getServer().getPluginManager().callEvent(new FactionAutoDisbandEvent(this));

			Factions.getInstance().removeFaction(getId());
		} else { // promote new faction admin
			if(oldLeader != null) {
				oldLeader.setRole(Role.COLEADER);
			}
			replacements.get(0).setRole(Role.ADMIN);
			//TODO:TL
			this.msg(ChatColor.YELLOW + "Faction admin " + ChatColor.LIGHT_PURPLE + "%s" + ChatColor.YELLOW + " has been removed. " + ChatColor.LIGHT_PURPLE + "%s" + ChatColor.YELLOW + " has been promoted as the new faction admin.", oldLeader == null ? "" : oldLeader.getName(), replacements.get(0).getName());
			FactionsPlugin.getInstance().log("Faction " + this.getTag() + " (" + this.getId() + ") admin was removed. Replacement admin: " + replacements.get(0).getName());
		}
	}

	// ----------------------------------------------//
	// Messages
	// ----------------------------------------------//
	public void msg(String message, Object... args) {
		message = TextUtil.parse(message, args);

		for(FPlayer fplayer : this.getFPlayersWhereOnline(true)) {
			fplayer.sendMessage(message);
		}
	}

	public void msg(TL translation, Object... args) {
		msg(translation.toString(), args);
	}

	public void sendMessage(String message) {
		for(FPlayer fplayer : this.getFPlayersWhereOnline(true)) {
			fplayer.sendMessage(message);
		}
	}

	// ----------------------------------------------//
	// Persistance and entity management
	// ----------------------------------------------//
	public void remove() {
		// Clean the board
		((MemoryBoard) Board.getInstance()).clean(id);

		for(FPlayer fPlayer : fplayers) {
			fPlayer.resetFactionData();
		}
	}

	public Set<FLocation> getAllClaims() {
		return Board.getInstance().getAllClaims(this);
	}
}
