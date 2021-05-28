package com.massivecraft.factions.integration;

import com.elmakers.mine.bukkit.api.entity.TeamProvider;
import com.elmakers.mine.bukkit.api.magic.MagicAPI;
import com.elmakers.mine.bukkit.api.protection.BlockBreakManager;
import com.elmakers.mine.bukkit.api.protection.BlockBuildManager;
import com.elmakers.mine.bukkit.api.protection.EntityTargetingManager;
import com.elmakers.mine.bukkit.api.protection.PVPManager;
import com.massivecraft.factions.*;
import com.massivecraft.factions.config.file.MainConfig;
import com.massivecraft.factions.listeners.FactionsBlockListener;
import com.massivecraft.factions.listeners.FactionsEntityListener;
import com.massivecraft.factions.perms.PermissibleAction;
import com.massivecraft.factions.perms.Relation;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;

public class Magic implements BlockBuildManager, BlockBreakManager, PVPManager, TeamProvider, EntityTargetingManager, Listener {
	public static void init(Plugin plugin) {
		if(plugin instanceof MagicAPI) {
			try {
				int v = Integer.parseInt(plugin.getDescription().getVersion().split("\\.")[0]);
				if(v < 8) {
					FactionsPlugin.getInstance().getLogger().info("Found Magic, but only supporting version 8+");
					return;
				}
			} catch(NumberFormatException ignored) {
				FactionsPlugin.getInstance().getLogger().info("Found Magic, but could not determine version");
				return;
			}
			FactionsPlugin.getInstance().getLogger().info("Integrating with Magic!");
			((MagicAPI) plugin).getController().register(new Magic());
		}
	}

	@Override
	public boolean hasBuildPermission(Player player, Block block) {
		if(block == null) {
			return true;
		}
		if(player == null) {
			return Board.getInstance().getFactionAt(new FLocation(block)).isWilderness();
		}
		return FactionsBlockListener.playerCanBuildDestroyBlock(player, block.getLocation(), PermissibleAction.BUILD, true);
	}

	@Override
	public boolean hasBreakPermission(Player player, Block block) {
		if(block == null) {
			return true;
		}
		if(player == null) {
			return Board.getInstance().getFactionAt(new FLocation(block)).isWilderness();
		}
		return FactionsBlockListener.playerCanBuildDestroyBlock(player, block.getLocation(), PermissibleAction.DESTROY, true);
	}

	@Override
	public boolean isPVPAllowed(Player player, Location location) {
		MainConfig.Factions facConf = FactionsPlugin.getInstance().conf().factions();
		if(facConf.pvp().getWorldsIgnorePvP().contains(location.getWorld().getName())) {
			return true;
		}
		if(facConf.protection().getPlayersWhoBypassAllProtection().contains(player.getName())) {
			return true;
		}
		Faction defFaction = Board.getInstance().getFactionAt(new FLocation(location));
		if(defFaction.noPvPInTerritory()) {
			return false;
		}
		FPlayer attacker = FPlayers.getInstance().getByPlayer(player);
		if(attacker.hasLoginPvpDisabled()) {
			return false;
		}
		Faction locFaction = Board.getInstance().getFactionAt(new FLocation(attacker));
		if(locFaction.noPvPInTerritory()) {
			return false;
		}
		return !locFaction.isSafeZone();
	}

	@Override
	public boolean isFriendly(Entity attacker, Entity entity) {
		if(!(attacker instanceof Player && entity instanceof Player)) {
			return false;
		}
		FPlayer attack = FPlayers.getInstance().getByPlayer((Player) attacker);
		FPlayer defend = FPlayers.getInstance().getByPlayer((Player) entity);
		if(attack.getFaction().isWilderness() || defend.getFaction().isWilderness()) {
			return false;
		}
		return attack.getRelationTo(defend).isAtLeast(Relation.TRUCE);
	}

	@Override
	public boolean canTarget(Entity source, Entity target) {
		return FactionsEntityListener.canDamage(source, target, false);
	}
}
