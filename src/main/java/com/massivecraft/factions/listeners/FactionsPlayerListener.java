package com.massivecraft.factions.listeners;

import com.massivecraft.factions.*;
import com.massivecraft.factions.configuration.MainConfiguration;
import com.massivecraft.factions.data.MemoryFPlayer;
import com.massivecraft.factions.gui.GUI;
import com.massivecraft.factions.perms.PermissibleAction;
import com.massivecraft.factions.perms.Relation;
import com.massivecraft.factions.util.TL;
import com.massivecraft.factions.util.TextUtil;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.player.*;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;

import java.util.Collection;
import java.util.HashMap;
import java.util.UUID;
import java.util.logging.Level;


public class FactionsPlayerListener extends AbstractListener {

	private final FactionsPlugin plugin;

	public FactionsPlayerListener(FactionsPlugin plugin) {
		this.plugin = plugin;
		for(Player player : plugin.getServer().getOnlinePlayers()) {
			initPlayer(player);
		}
	}

	@EventHandler(priority = EventPriority.NORMAL)
	public void onPlayerJoin(PlayerJoinEvent event) {
		initPlayer(event.getPlayer());
	}

	private void initPlayer(Player player) {
		// Make sure that all online players do have a fplayer.
		final FPlayer me = FPlayers.getInstance().getByPlayer(player);
		((MemoryFPlayer) me).setName(player.getName());

		// Update the lastLoginTime for this fplayer
		me.resetLastLoginTime();

		// Store player's current FLocation and notify them where they are
		me.setLastStoodAt(new FLocation(player.getLocation()));

		me.login(); // set kills / deaths

		if(me.isAdminBypassing() && !player.hasPermission(Permission.BYPASS.node)) {
			me.setIsAdminBypassing(false);
			FactionsPlugin.getInstance().log(Level.INFO, "Found %s on admin Bypass without permission on login. Disabled it for them.", player.getName());
		}

		if(plugin.configMain.restrictWorlds().isEnabled(player.getWorld())) {
			this.initFactionWorld(me);
		}
	}

	private void initFactionWorld(FPlayer me) {
		me.setTakeFallDamage(true);
	}

	@EventHandler(priority = EventPriority.NORMAL)
	public void onPlayerQuit(PlayerQuitEvent event) {
		FPlayer me = FPlayers.getInstance().getByPlayer(event.getPlayer());

		me.logout(); // cache kills and deaths

		// if player is waiting for fstuck teleport but leaves, remove
		if(FactionsPlugin.getInstance().stuckMap.containsKey(me.getPlayer().getUniqueId())) {
			FPlayers.getInstance().getByPlayer(me.getPlayer()).msg(TL.COMMAND_STUCK_CANCELLED);
			FactionsPlugin.getInstance().stuckMap.remove(me.getPlayer().getUniqueId());
			FactionsPlugin.getInstance().timers.remove(me.getPlayer().getUniqueId());
		}

		Faction myFaction = me.getFaction();
		if(!myFaction.isWilderness()) {
			myFaction.memberLoggedOff();
		}
	}

	// Holds the next time a player can have a map shown.
	private final HashMap<UUID, Long> showTimes = new HashMap<>();

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onPlayerMove(PlayerMoveEvent event) {
		if(!plugin.configMain.restrictWorlds().isEnabled(event.getPlayer().getWorld())) {
			return;
		}

		// quick check to make sure player is moving between chunks; good performance boost
		if(event.getFrom().getBlockX() >> 4 == event.getTo().getBlockX() >> 4 && event.getFrom().getBlockZ() >> 4 == event.getTo().getBlockZ() >> 4 && event.getFrom().getWorld() == event.getTo().getWorld()) {
			return;
		}

		final Player player = event.getPlayer();
		final FPlayer me = FPlayers.getInstance().getByPlayer(player);

		final FLocation from = me.getLastStoodAt();
		final FLocation to = new FLocation(event.getTo());

		// Did we change chunk?
		if(from.equals(to)) {
			return;
		}

		me.setLastStoodAt(to);

		if(me.getAutoClaimFor() != null) {
			me.attemptClaim(me.getAutoClaimFor(), event.getTo(), true);
		}

		if(me.isMapAutoUpdating()) {
			if(!showTimes.containsKey(player.getUniqueId()) || (showTimes.get(player.getUniqueId()) < System.currentTimeMillis())) {
				me.sendFancyMessage(Board.getInstance().getMap(me, to));
				showTimes.put(player.getUniqueId(), System.currentTimeMillis() + FactionsPlugin.getInstance().configMain.commands().map().cooldown());
			}
		} else if(Board.getInstance().getFactionAt(to) != Board.getInstance().getFactionAt(from)) {
			me.sendFactionHereMessage();
		}
	}

	@EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
	public void onPlayerInteract(PlayerInteractEvent event) {
		if(!plugin.configMain.restrictWorlds().isEnabled(event.getPlayer().getWorld())) {
			return;
		}

		// only need to check right-clicks and physical as of MC 1.4+; good performance boost
		if(event.getAction() != Action.RIGHT_CLICK_BLOCK && event.getAction() != Action.PHYSICAL) {
			return;
		}

		Block block = event.getClickedBlock();
		Player player = event.getPlayer();

		if(block == null) {
			return;  // clicked in air, apparently
		}

		if(event.getAction() == Action.PHYSICAL && block.getType().name().contains("SOIL")) {
			if(!FactionsBlockListener.playerCanBuildDestroyBlock(player, block.getLocation(), PermissibleAction.DESTROY, false)) {
				event.setCancelled(true);
			}
		}

		if(!canUseBlock(player, block.getType(), block.getLocation())) {
			event.setCancelled(true);
			if(block.getType().name().endsWith("_PLATE")) {
				return;
			}
			return;
		}

		if(event.getAction() != Action.RIGHT_CLICK_BLOCK) {
			return;  // only interested on right-clicks for below
		}

		if(!playerCanUseItemHere(player, block.getLocation(), event.getMaterial(), false)) {
			event.setCancelled(true);
		}
	}

	public boolean playerCanUseItemHere(Player player, Location location, Material material, boolean justCheck) {

		FPlayer me = FPlayers.getInstance().getByPlayer(player);
		if(me.isAdminBypassing()) {
			return true;
		}

		FLocation loc = new FLocation(location);
		Faction otherFaction = Board.getInstance().getFactionAt(loc);

		if(otherFaction.isWilderness()) {
			return true;
		}

		if(!otherFaction.hasAccess(me, PermissibleAction.ITEM)) {
			if(!justCheck) {
				me.msg(TL.PLAYER_USE_TERRITORY, TextUtil.getMaterialName(material), otherFaction.getTag(me.getFaction()));
			}
			return false;
		}

		return true;
	}

	@EventHandler(priority = EventPriority.HIGH)
	public void onPlayerRespawn(PlayerRespawnEvent event) {
		if(!plugin.configMain.restrictWorlds().isEnabled(event.getPlayer().getWorld())) {
			return;
		}

		FPlayer me = FPlayers.getInstance().getByPlayer(event.getPlayer());
		me.resetLastGraceTime();
	}

	@EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
	public void onTeleport(PlayerTeleportEvent event) {
		if(!plugin.configMain.restrictWorlds().isEnabled(event.getTo().getWorld())) {
			return;
		}

		FPlayer me = FPlayers.getInstance().getByPlayer(event.getPlayer());
		if(!event.getFrom().getWorld().equals(event.getTo().getWorld()) && !plugin.configMain.restrictWorlds().isEnabled(event.getPlayer().getWorld())) {
			this.initFactionWorld(me);
		}

		FLocation to = new FLocation(event.getTo());
		me.setLastStoodAt(to);
	}

	// For some reason onPlayerInteract() sometimes misses bucket events depending on distance (something like 2-3 blocks away isn't detected),
	// but these separate bucket events below always fire without fail
	@EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
	public void onPlayerBucketEmpty(PlayerBucketEmptyEvent event) {
		if(!plugin.configMain.restrictWorlds().isEnabled(event.getPlayer().getWorld())) {
			return;
		}

		Block block = event.getBlockClicked();
		Player player = event.getPlayer();

		if(!playerCanUseItemHere(player, block.getRelative(event.getBlockFace()).getLocation(), event.getBucket(), false)) {
			event.setCancelled(true);
		}
	}

	@EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
	public void onPlayerBucketFill(PlayerBucketFillEvent event) {
		if(!plugin.configMain.restrictWorlds().isEnabled(event.getPlayer().getWorld())) {
			return;
		}

		Block block = event.getBlockClicked();
		Player player = event.getPlayer();

		if(!playerCanUseItemHere(player, block.getLocation(), event.getBucket(), false)) {
			event.setCancelled(true);
		}
	}

	public static boolean preventCommand(String fullCmd, Player player) {
		MainConfiguration.Factions.CommandBlacklist commandBlacklist = FactionsPlugin.getInstance().configMain.factions().commandBlacklist();
		if((commandBlacklist.getInNeutralClaim().isEmpty() &&
				commandBlacklist.getInEnemyClaim().isEmpty() &&
				commandBlacklist.getInWilderness().isEmpty() &&
				commandBlacklist.getInFriendlyClaim().isEmpty() &&
				commandBlacklist.getInPermanentClaim().isEmpty())) {
			return false;
		}

		fullCmd = fullCmd.toLowerCase();

		FPlayer me = FPlayers.getInstance().getByPlayer(player);

		String shortCmd;  // command without the slash at the beginning
		if(fullCmd.startsWith("/")) {
			shortCmd = fullCmd.substring(1);
		} else {
			shortCmd = fullCmd;
			fullCmd = "/" + fullCmd;
		}

		if(me.hasFaction() &&
				!me.isAdminBypassing() &&
				me.getFaction().isPermanent() &&
				isCommandInCollection(fullCmd, shortCmd, commandBlacklist.getInPermanentClaim())) {
			me.msg(TL.PLAYER_COMMAND_PERMANENT, fullCmd);
			return true;
		}

		Faction at = Board.getInstance().getFactionAt(new FLocation(player.getLocation()));
		if(at.isWilderness() && !commandBlacklist.getInWilderness().isEmpty() && !me.isAdminBypassing() && isCommandInCollection(fullCmd, shortCmd, commandBlacklist.getInWilderness())) {
			me.msg(TL.PLAYER_COMMAND_WILDERNESS, fullCmd);
			return true;
		}

		Relation rel = at.getRelationTo(me);
		if(at.isNormal() && rel.isAlly() && !commandBlacklist.getInFriendlyClaim().isEmpty() && !me.isAdminBypassing() && isCommandInCollection(fullCmd, shortCmd, commandBlacklist.getInFriendlyClaim())) {
			me.msg(TL.PLAYER_COMMAND_ALLY, fullCmd);
			return true;
		}

		if(at.isNormal() && rel.isNeutral() && !commandBlacklist.getInNeutralClaim().isEmpty() && !me.isAdminBypassing() && isCommandInCollection(fullCmd, shortCmd, commandBlacklist.getInNeutralClaim())) {
			me.msg(TL.PLAYER_COMMAND_NEUTRAL, fullCmd);
			return true;
		}

		if(at.isNormal() && rel.isEnemy() && !commandBlacklist.getInEnemyClaim().isEmpty() && !me.isAdminBypassing() && isCommandInCollection(fullCmd, shortCmd, commandBlacklist.getInEnemyClaim())) {
			me.msg(TL.PLAYER_COMMAND_ENEMY, fullCmd);
			return true;
		}

		return false;
	}

	private static boolean isCommandInCollection(String fullCmd, String shortCmd, Collection<String> set) {
		for(String string : set) {
			if(string == null) {
				continue;
			}
			string = string.toLowerCase();
			if(fullCmd.startsWith(string) || shortCmd.startsWith(string)) {
				return true;
			}
		}
		return false;
	}

	@EventHandler(priority = EventPriority.HIGH)
	public void onPlayerInteractGUI(InventoryClickEvent event) {
		if(!plugin.configMain.restrictWorlds().isEnabled(event.getWhoClicked().getWorld())) {
			return;
		}

		Inventory clickedInventory = getClickedInventory(event);
		if(clickedInventory == null) {
			return;
		}
		if(clickedInventory.getHolder() instanceof GUI) {
			event.setCancelled(true);
			GUI<?> ui = (GUI<?>) clickedInventory.getHolder();
			ui.click(event.getRawSlot(), event.getClick());
		}
	}

	private Inventory getClickedInventory(InventoryClickEvent event) {
		int rawSlot = event.getRawSlot();
		InventoryView view = event.getView();
		if(rawSlot < 0 || rawSlot >= view.countSlots()) { // < 0 check also covers situation of InventoryView.OUTSIDE (-999)
			return null;
		}
		if(rawSlot < view.getTopInventory().getSize()) {
			return view.getTopInventory();
		} else {
			return view.getBottomInventory();
		}
	}

	@EventHandler(priority = EventPriority.HIGH)
	public void onPlayerMoveGUI(InventoryDragEvent event) {
		if(!plugin.configMain.restrictWorlds().isEnabled(event.getWhoClicked().getWorld())) {
			return;
		}

		if(event.getInventory().getHolder() instanceof GUI) {
			event.setCancelled(true);
		}
	}

	@EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
	public void onPlayerCommandPreprocess(PlayerCommandPreprocessEvent event) {
		if(!plugin.configMain.restrictWorlds().isEnabled(event.getPlayer().getWorld())) {
			return;
		}

		if(FactionsPlayerListener.preventCommand(event.getMessage(), event.getPlayer())) {
			event.setCancelled(true);
		}
	}

	@EventHandler(priority = EventPriority.LOWEST)
	public void onPlayerPreLogin(PlayerLoginEvent event) {
		FPlayers.getInstance().getByPlayer(event.getPlayer());
	}
}
