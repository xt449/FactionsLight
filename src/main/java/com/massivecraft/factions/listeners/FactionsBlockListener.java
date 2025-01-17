package com.massivecraft.factions.listeners;

import com.massivecraft.factions.*;
import com.massivecraft.factions.configuration.MainConfiguration;
import com.massivecraft.factions.perms.PermissibleAction;
import com.massivecraft.factions.util.TL;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockDamageEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.block.EntityBlockFormEvent;
import org.bukkit.event.world.PortalCreateEvent;

public class FactionsBlockListener implements Listener {

	public final FactionsPlugin plugin;

	public FactionsBlockListener(FactionsPlugin plugin) {
		this.plugin = plugin;
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onPortalCreate(PortalCreateEvent event) {
		event.setCancelled(true);
	}

	@EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
	public void onBlockPlace(BlockPlaceEvent event) {
		if(!plugin.configMain.restrictWorlds().isEnabled(event.getBlock().getWorld())) {
			return;
		}

		if(!event.canBuild()) {
			return;
		}

		// special case for flint&steel, which should only be prevented by DenyUsage list
		if(event.getBlockPlaced().getType() == Material.FIRE) {
			return;
		}

		if(!playerCanBuildDestroyBlock(event.getPlayer(), event.getBlock().getLocation(), PermissibleAction.BUILD, false)) {
			event.setCancelled(true);
		}
	}

	@EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
	public void onBlockBreak(BlockBreakEvent event) {
		if(!plugin.configMain.restrictWorlds().isEnabled(event.getBlock().getWorld())) {
			return;
		}

		if(!playerCanBuildDestroyBlock(event.getPlayer(), event.getBlock().getLocation(), PermissibleAction.DESTROY, false)) {
			event.setCancelled(true);
		}
	}

	@EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
	public void onBlockDamage(BlockDamageEvent event) {
		if(!plugin.configMain.restrictWorlds().isEnabled(event.getBlock().getWorld())) {
			return;
		}

		if(event.getInstaBreak() && !playerCanBuildDestroyBlock(event.getPlayer(), event.getBlock().getLocation(), PermissibleAction.DESTROY, false)) {
			event.setCancelled(true);
		}
	}

	@EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
	public void onFrostWalker(EntityBlockFormEvent event) {
		if(!plugin.configMain.restrictWorlds().isEnabled(event.getBlock().getWorld())) {
			return;
		}

		if(event.getEntity().getType() != EntityType.PLAYER) {
			return;
		}

		Player player = (Player) event.getEntity();
		Location location = event.getBlock().getLocation();

		// only notify every 10 seconds
		FPlayer fPlayer = FPlayers.getInstance().getByPlayer(player);
		boolean justCheck = fPlayer.getLastFrostwalkerMessage() + 10000 > System.currentTimeMillis();
		if(!justCheck) {
			fPlayer.setLastFrostwalkerMessage();
		}

		// Check if they have build permissions here. If not, block this from happening.
		if(!playerCanBuildDestroyBlock(player, location, PermissibleAction.FROSTWALK, justCheck)) {
			event.setCancelled(true);
		}
	}

	public static boolean playerCanBuildDestroyBlock(Player player, Location location, PermissibleAction permissibleAction, boolean justCheck) {
		MainConfiguration conf = FactionsPlugin.getInstance().configMain;

		FPlayer me = FPlayers.getInstance().getById(player.getUniqueId().toString());
		if(me.isAdminBypassing()) {
			return true;
		}

		FLocation loc = new FLocation(location);
		Faction otherFaction = Board.getInstance().getFactionAt(loc);

		if(otherFaction.isWilderness()) {
			if(conf.worldGuard().isBuildPriority() && FactionsPlugin.getInstance().getWorldguard() != null && FactionsPlugin.getInstance().getWorldguard().playerCanBuild(player)) {
				return true;
			}

			return true; // This is not faction territory. Use whatever you like here.
		}

		Faction myFaction = me.getFaction();
		boolean pain = !justCheck && otherFaction.hasAccess(me, PermissibleAction.PAINBUILD);

		// If the faction hasn't: defined access or denied, fallback to config values
		if(!otherFaction.hasAccess(me, permissibleAction)) {
			if(pain && permissibleAction != PermissibleAction.FROSTWALK) {
				me.msg(TL.PERM_DENIED_PAINTERRITORY, permissibleAction.descriptionShort, otherFaction.getTag(myFaction));
				return true;
			} else if(!justCheck) {
				me.msg(TL.PERM_DENIED_TERRITORY, permissibleAction.descriptionShort, otherFaction.getTag(myFaction));
			}
			return false;
		}

		return true;
	}
}
