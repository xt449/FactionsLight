package com.massivecraft.factions.data;

import com.massivecraft.factions.*;
import com.massivecraft.factions.configuration.MainConfiguration;
import com.massivecraft.factions.event.FPlayerLeaveEvent;
import com.massivecraft.factions.event.FactionAutoDisbandEvent;
import com.massivecraft.factions.event.LandClaimEvent;
import com.massivecraft.factions.integration.LWCIntegration;
import com.massivecraft.factions.perms.PermissibleAction;
import com.massivecraft.factions.perms.Relation;
import com.massivecraft.factions.perms.Role;
import com.massivecraft.factions.tag.Tag;
import com.massivecraft.factions.util.RelationUtil;
import com.massivecraft.factions.util.TL;
import com.massivecraft.factions.util.TextUtil;
import mkremins.fanciful.FancyMessage;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.*;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;


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

public abstract class MemoryFPlayer implements FPlayer {

	protected int factionId;
	protected Role role;
	protected String title;
	protected long lastLoginTime;
	protected long lastGraceTime;
	protected String id;
	protected String name;
	protected boolean showScoreboard = true;
	protected boolean isAdminBypassing = false;
	protected int kills, deaths;

	protected transient FLocation lastStoodAt = new FLocation(); // Where did this player stand the last time we checked?
	protected transient boolean mapAutoUpdating;
	protected transient Faction autoClaimFor;
	protected transient long lastFrostwalkerMessage;
	protected transient boolean shouldTakeFallDamage = true;

	public MemoryFPlayer(String id) {
		this.id = id;
		this.factionId = 0;
		this.lastLoginTime = System.currentTimeMillis();
		this.lastGraceTime = lastLoginTime;
		this.mapAutoUpdating = false;
		this.autoClaimFor = null;
		this.kills = 0;
		this.deaths = 0;

		this.resetFactionData();
	}

	@Override
	public void login() {
		this.kills = getPlayer().getStatistic(Statistic.PLAYER_KILLS);
		this.deaths = getPlayer().getStatistic(Statistic.DEATHS);
	}

	@Override
	public void logout() {
		this.kills = getPlayer().getStatistic(Statistic.PLAYER_KILLS);
		this.deaths = getPlayer().getStatistic(Statistic.DEATHS);
	}

	@Override
	public Faction getFaction() {
		Faction faction = Factions.getInstance().getFactionById(this.factionId);
		if(faction == null) {
			FactionsPlugin.getInstance().getLogger().warning("Found null faction (id " + this.factionId + ") for player " + this.getName());
			this.factionId = 0;
			faction = Factions.getInstance().getWilderness();
		}
//		faction.initFix();
		return faction;
	}

	@Override
	public int getFactionId() {
		return factionId;
	}

	@Override
	public boolean hasFaction() {
		return factionId != 0;
	}

	@Override
	public void setFaction(Faction faction) {
		Faction oldFaction = this.getFaction();
		if(oldFaction != null) {
			oldFaction.removeFPlayer(this);
		}
		faction.addFPlayer(this);
		this.factionId = faction.getId();
	}

	@Override
	public Role getRole() {
		// Hack to fix null roles..
		if(role == null) {
			this.role = Role.NORMAL;
		}

		return this.role;
	}

	@Override
	public void setRole(Role role) {
		this.role = role;
	}

	@Override
	public long getLastFrostwalkerMessage() {
		return this.lastFrostwalkerMessage;
	}

	@Override
	public void setLastFrostwalkerMessage() {
		this.lastFrostwalkerMessage = System.currentTimeMillis();
	}

	@Override
	public Faction getAutoClaimFor() {
		return autoClaimFor;
	}

	@Override
	public void setAutoClaimFor(Faction faction) {
		this.autoClaimFor = faction;
	}

	@Override
	public boolean isAdminBypassing() {
		return this.isAdminBypassing;
	}

	@Override
	public void setIsAdminBypassing(boolean val) {
		this.isAdminBypassing = val;
	}

	@Override
	public void resetFactionData() {
		// clean up any territory ownership in old faction, if there is one
		final Faction currentFaction = Factions.getInstance().getFactionById(factionId);
		if(currentFaction != null) {
			currentFaction.removeFPlayer(this);
		}

		this.factionId = 0; // The wilderness faction
		this.role = Role.NORMAL;
		this.title = "";
		this.autoClaimFor = null;
	}

	// -------------------------------------------- //
	// Getters And Setters
	// -------------------------------------------- //

	@Override
	public long getLastLoginTime() {
		return lastLoginTime;
	}

	@Override
	public void resetLastLoginTime() {
		this.lastLoginTime = System.currentTimeMillis();
		this.lastGraceTime = this.lastLoginTime;
	}

	@Override
	public void resetLastGraceTime() {
		this.lastGraceTime = System.currentTimeMillis();
	}

	@Override
	public boolean isMapAutoUpdating() {
		return this.mapAutoUpdating;
	}

	@Override
	public void setMapAutoUpdating(boolean mapAutoUpdating) {
		this.mapAutoUpdating = mapAutoUpdating;
	}

	@Override
	public boolean inGracePeriod() {
		final long time = System.currentTimeMillis();
		final MainConfiguration.Factions.Combat combatConfig = FactionsPlugin.getInstance().configMain.factions().combat();

		return (this.lastLoginTime + (combatConfig.gracePeriodOnLogin() * 1000L) >= time) ||
				(this.lastGraceTime + (combatConfig.gracePeriodOnRespawn() * 1000L) >= time);
	}

	@Override
	public FLocation getLastStoodAt() {
		return this.lastStoodAt;
	}

	@Override
	public void setLastStoodAt(FLocation flocation) {
		this.lastStoodAt = flocation;
	}

	//----------------------------------------------//
	// Title, Name, Faction Tag and Chat
	//----------------------------------------------//

	// Base:

	@Override
	public String getTitle() {
		return this.hasFaction() ? title : TL.NOFACTION_PREFIX.toString();
	}

	@Override
	public void setTitle(CommandSender sender, String title) {
		// Check if the setter has it.
		if(sender.hasPermission(Permission.TITLE_COLOR.node)) {
			title = ChatColor.translateAlternateColorCodes('&', title);
		}

		this.title = title;
	}

	@Override
	public String getName() {
		if(this.name == null) {
			// Older versions of FactionsUUID don't save the name,
			// so `name` will be null the first time it's retrieved
			// after updating
			OfflinePlayer offline = Bukkit.getOfflinePlayer(UUID.fromString(getId()));
			this.name = offline.getName() != null ? offline.getName() : getId();
		}
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Override
	public String getTag() {
		return this.hasFaction() ? this.getFaction().getTag() : "";
	}

	// Base concatenations:

	@Override
	public String getNameAndSomething(String something) {
		String ret = this.role.getPrefix();
		if(something != null && something.length() > 0) {
			ret += something + " ";
		}
		ret += this.getName();
		return ret;
	}

	@Override
	public String getNameAndTitle() {
		return this.getNameAndSomething(this.getTitle());
	}

	@Override
	public String getNameAndTag() {
		return this.getNameAndSomething(this.getTag());
	}

	@Override
	public int getKills() {
		return isOnline() ? getPlayer().getStatistic(Statistic.PLAYER_KILLS) : this.kills;
	}

	@Override
	public int getDeaths() {
		return isOnline() ? getPlayer().getStatistic(Statistic.DEATHS) : this.deaths;

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

	//----------------------------------------------//
	// Territory
	//----------------------------------------------//

	@Override
	public void sendFactionHereMessage() {
		Faction toShow = Board.getInstance().getFactionAt(getLastStoodAt());
		Player player = getPlayer();

		if(player != null) {
			String title = Tag.parsePlain(toShow, this, "{faction-relation-color}{faction}");
			player.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(title));
		}
	}

	// -------------------------------
	// Actions
	// -------------------------------

	@Override
	public void leave() {
		Faction myFaction = this.getFaction();

		if(myFaction == null) {
			resetFactionData();
			return;
		}

		boolean perm = myFaction.isPermanent();

		if(!perm && this.getRole() == Role.ADMIN && myFaction.getFPlayers().size() > 1) {
			msg(TL.LEAVE_PASSADMIN);
			return;
		}

		FPlayerLeaveEvent leaveEvent = new FPlayerLeaveEvent(this, myFaction, FPlayerLeaveEvent.PlayerLeaveReason.LEAVE);
		Bukkit.getServer().getPluginManager().callEvent(leaveEvent);
		if(leaveEvent.isCancelled()) {
			return;
		}

		if(myFaction.isNormal()) {
			for(FPlayer fplayer : myFaction.getFPlayersWhereOnline(true)) {
				fplayer.msg(TL.LEAVE_LEFT, this.describeTo(fplayer, true), myFaction.describeTo(fplayer));
			}

			if(FactionsPlugin.getInstance().configMain.logging().factionLeave()) {
				FactionsPlugin.getInstance().log(TL.LEAVE_LEFT.format(this.getName(), myFaction.getTag()));
			}
		}

		this.resetFactionData();

		if(myFaction.isNormal() && !perm && myFaction.getFPlayers().isEmpty()) {
			// Remove this faction
			for(FPlayer fplayer : FPlayers.getInstance().getOnlinePlayers()) {
				fplayer.msg(TL.LEAVE_DISBANDED, myFaction.describeTo(fplayer, true));
			}

			FactionsPlugin.getInstance().getServer().getPluginManager().callEvent(new FactionAutoDisbandEvent(myFaction));
			Factions.getInstance().removeFaction(myFaction.getId());
			if(FactionsPlugin.getInstance().configMain.logging().factionDisband()) {
				FactionsPlugin.getInstance().log(TL.LEAVE_DISBANDEDLOG.format(myFaction.getTag(), myFaction.getId(), this.getName()));
			}
		}
	}

	@Override
	public boolean canClaimForFaction(Faction forFaction) {
		return this.isAdminBypassing() || !forFaction.isWilderness() && (forFaction == this.getFaction() && this.getFaction().hasAccess(this, PermissibleAction.TERRITORY));
	}

	@Override
	public boolean canClaimForFactionAtLocation(Faction forFaction, FLocation flocation, boolean notifyFailure) {
		FactionsPlugin plugin = FactionsPlugin.getInstance();
		String denyReason = null;
		Faction currentFaction = Board.getInstance().getFactionAt(flocation);

		if(plugin.configMain.worldGuard().isEnabled() && plugin.getWorldguard() != null && plugin.getWorldguard().checkForRegionsInChunk(flocation.getChunk())) {
			// Checks for WorldGuard regions in the chunk attempting to be claimed
			denyReason = TextUtil.parse(TL.CLAIM_PROTECTED.toString());
//		} else if(plugin.configMain.factions().claims().getWorldsNoClaiming().contains(flocation.getWorldName())) {
//			// Cannot claim in this world
//			denyReason = TextUtil.parse(TL.CLAIM_DISABLED.toString());
		} else if(this.isAdminBypassing()) {
			// Admin bypass
			return true;
		} else if(!forFaction.hasAccess(this, PermissibleAction.TERRITORY)) {
			// Lacking perms to territory claim
			denyReason = TextUtil.parse(TL.CLAIM_CANTCLAIM.toString(), forFaction.describeTo(this));
		} else if(forFaction == currentFaction) {
			// Already owned by this faction, nitwit
			denyReason = TextUtil.parse(TL.CLAIM_ALREADYOWN.toString(), forFaction.describeTo(this, true));
//		} else if(forFaction.getFPlayers().size() < plugin.configMain.factions().claims().getRequireMinFactionMembers()) {
//			// Need more members in order to claim land
//			denyReason = TextUtil.parse(TL.CLAIM_MEMBERS.toString(), plugin.configMain.factions().claims().getRequireMinFactionMembers());
//		} else if(plugin.getLandRaidControl() instanceof PowerControl && ownedLand >= forFaction.getPowerRounded()) {
//			// Already own at least as much land as power
//			denyReason = TextUtil.parse(TL.CLAIM_POWER.toString());
//		} else if(plugin.getLandRaidControl() instanceof DTRControl && ownedLand >= plugin.getLandRaidControl().getLandLimit(forFaction)) {
//			// Already own at least as much land as land limit (DTR)
//			denyReason = TextUtil.parse(TL.CLAIM_DTR_LAND.toString());
//		} else if(plugin.configMain.factions().claims().getLandsMax() != 0 && ownedLand >= plugin.configMain.factions().claims().getLandsMax() && forFaction.isNormal()) {
//			// Land limit reached
//			denyReason = TextUtil.parse(TL.CLAIM_LIMIT.toString());
		} else if(currentFaction.getRelationTo(forFaction) == Relation.ALLY) {
			// // Can't claim ally
			denyReason = TextUtil.parse(TL.CLAIM_ALLY.toString());
		}
//		else if(currentFaction.isNormal()) {
////			if(myFaction.isPeaceful()) {
////				// Cannot claim as peaceful
////				denyReason = TextUtil.parse(TL.CLAIM_PEACEFUL.toString(), currentFaction.getTag(this));
////			} else if(currentFaction.isPeaceful()) {
////				// Cannot claim from peaceful
////				denyReason = TextUtil.parse(TL.CLAIM_PEACEFULTARGET.toString(), currentFaction.getTag(this));
////			} else if(!currentFaction.hasLandInflation()) {
////				// Cannot claim other faction (perhaps based on power/land ratio)
////				// TODO more messages WARN current faction most importantly
////				denyReason = TextUtil.parse(TL.CLAIM_THISISSPARTA.toString(), currentFaction.getTag(this));
////			} else if(currentFaction.hasLandInflation() && !plugin.configMain.factions().claims().isAllowOverClaim()) {
////				// deny over claim when it normally would be allowed.
////				denyReason = TextUtil.parse(TL.CLAIM_OVERCLAIM_DISABLED.toString());
////			} else if(!Board.getInstance().isBorderLocation(flocation)) {
////				denyReason = TextUtil.parse(TL.CLAIM_BORDER.toString());
////			}
//		}
		// TODO: Add more else if statements.

		if(notifyFailure && denyReason != null) {
			msg(denyReason);
		}
		return denyReason == null;
	}

	@Override
	public boolean attemptClaim(Faction forFaction, Location location, boolean notifyFailure) {
		return attemptClaim(forFaction, new FLocation(location), notifyFailure);
	}

	@Override
	public boolean attemptClaim(Faction forFaction, FLocation flocation, boolean notifyFailure) {
		// notifyFailure is false if called by auto-claim; no need to notify on every failure for it
		// return value is false on failure, true on success

		Faction currentFaction = Board.getInstance().getFactionAt(flocation);

		if(!this.canClaimForFactionAtLocation(forFaction, flocation, notifyFailure)) {
			return false;
		}

		LandClaimEvent claimEvent = new LandClaimEvent(flocation, forFaction, this);
		Bukkit.getServer().getPluginManager().callEvent(claimEvent);
		if(claimEvent.isCancelled()) {
			return false;
		}

		if(LWCIntegration.getEnabled() && forFaction.isNormal() && FactionsPlugin.getInstance().configMain.lwc().isResetLocksOnCapture()) {
			LWCIntegration.clearOtherLocks(flocation, this.getFaction());
		}

		// announce success
		Set<FPlayer> informTheseFPlayers = new HashSet<>();
		informTheseFPlayers.add(this);
		informTheseFPlayers.addAll(forFaction.getFPlayersWhereOnline(true));
		for(FPlayer fp : informTheseFPlayers) {
			fp.msg(TL.CLAIM_CLAIMED, this.describeTo(fp, true), forFaction.describeTo(fp), currentFaction.describeTo(fp));
		}

		Board.getInstance().setFactionAt(forFaction, flocation);

		if(FactionsPlugin.getInstance().configMain.logging().landClaim()) {
			FactionsPlugin.getInstance().log(TL.CLAIM_CLAIMEDLOG.toString(), this.getName(), flocation.getCoordString(), forFaction.getTag());
		}

		return true;
	}

	public boolean shouldBeSaved() {
		// TODO DTR
//		return this.hasFaction() || (this.getPowerRounded() != this.getPowerMaxRounded() && this.getPowerRounded() != (int) Math.round(FactionsPlugin.getInstance().configMain.factions().landRaidControl().power().getPlayerStarting()));
		return this.hasFaction();
	}

	@Override
	public void msg(String str, Object... args) {
		this.sendMessage(TextUtil.parse(str, args));
	}

	@Override
	public void msg(TL translation, Object... args) {
		this.msg(translation.toString(), args);
	}

	@Override
	public Player getPlayer() {
		return Bukkit.getPlayer(UUID.fromString(this.getId()));
	}

	@Override
	public boolean isOnline() {
		Player player = this.getPlayer();
		return player != null && FactionsPlugin.getInstance().configMain.restrictWorlds().isEnabled(player.getWorld());
	}

	@Override
	public boolean shouldTakeFallDamage() {
		return this.shouldTakeFallDamage;
	}

	@Override
	public void setTakeFallDamage(boolean fallDamage) {
		this.shouldTakeFallDamage = fallDamage;
	}

	// -------------------------------------------- //
	// Message Sending Helpers
	// -------------------------------------------- //

	@Override
	public void sendMessage(String msg) {
		if(msg.contains("{null}")) {
			return; // user wants this message to not send
		}
		if(msg.contains("/n/")) {
			for(String s : msg.split("/n/")) {
				sendMessage(s);
			}
			return;
		}
		Player player = this.getPlayer();
		if(player == null) {
			return;
		}
		player.sendMessage(msg);
	}

	@Override
	public void sendMessage(List<String> msgs) {
		for(String msg : msgs) {
			this.sendMessage(msg);
		}
	}

	@Override
	public void sendFancyMessage(List<FancyMessage> messages) {
		Player player = getPlayer();
		if(player == null) {
			return;
		}

		for(FancyMessage msg : messages) {
			msg.send(player);
		}
	}

	@Override
	public String getId() {
		return id;
	}

	@Override
	public void setId(String id) {
		this.id = id;
	}
}
