package com.massivecraft.factions.cmd;

import com.massivecraft.factions.*;
import com.massivecraft.factions.event.FPlayerTeleportEvent;
import com.massivecraft.factions.perms.PermissibleAction;
import com.massivecraft.factions.perms.Relation;
import com.massivecraft.factions.struct.Permission;
import com.massivecraft.factions.util.TL;
import com.massivecraft.factions.util.WarmUpUtil;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;


public class CmdHome extends FCommand {

	public CmdHome() {
		super();
		this.aliases.add("home");

		this.optionalArgs.put("faction", "yours");

		this.requirements = new CommandRequirements.Builder(Permission.HOME)
				.playerOnly()
				.noDisableOnLock()
				.build();
	}

	@Override
	public void perform(final CommandContext context) {
		// TODO: Hide this command on help also.

		Faction targetFaction = context.argAsFaction(0, context.fPlayer == null ? null : context.faction);

		if(targetFaction != context.faction && context.fPlayer.isAdminBypassing()) {
			if(targetFaction.hasHome()) {
				FactionsPlugin.getInstance().teleport(context.player, targetFaction.getHome());
			} else {
				context.fPlayer.msg(TL.COMMAND_HOME_NOHOME.toString());
			}
			return;
		}

		if(!FactionsPlugin.getInstance().conf().factions().homes().isEnabled()) {
			context.fPlayer.msg(TL.COMMAND_HOME_DISABLED);
			return;
		}

		if(!FactionsPlugin.getInstance().conf().factions().homes().isTeleportCommandEnabled()) {
			context.fPlayer.msg(TL.COMMAND_HOME_TELEPORTDISABLED);
			return;
		}

		if(!targetFaction.hasHome()) {
			if(targetFaction == context.faction) {
				if(context.faction.hasAccess(context.fPlayer, PermissibleAction.SETHOME)) {
					context.fPlayer.msg(TL.COMMAND_HOME_NOHOME.toString() + TL.GENERIC_YOUSHOULD);
				} else {
					context.fPlayer.msg(TL.COMMAND_HOME_NOHOME.toString() + TL.GENERIC_ASKYOURLEADER);
				}
				context.fPlayer.sendMessage(FCmdRoot.getInstance().cmdSethome.getUsageTemplate(context));
			} else {
				context.fPlayer.msg(TL.COMMAND_HOME_NOHOME.toString());
			}
			return;
		}

		if(!targetFaction.hasAccess(context.fPlayer, PermissibleAction.HOME)) {
			context.fPlayer.msg(TL.COMMAND_HOME_DENIED, targetFaction.getTag(context.fPlayer));
			return;
		}

		if(!FactionsPlugin.getInstance().conf().factions().homes().isTeleportAllowedFromEnemyTerritory() && context.fPlayer.isInEnemyTerritory()) {
			context.fPlayer.msg(TL.COMMAND_HOME_INENEMY);
			return;
		}

		if(!FactionsPlugin.getInstance().conf().factions().homes().isTeleportAllowedFromDifferentWorld() && context.player.getWorld().getUID() != targetFaction.getHome().getWorld().getUID()) {
			context.fPlayer.msg(TL.COMMAND_HOME_WRONGWORLD);
			return;
		}

		Faction faction = Board.getInstance().getFactionAt(new FLocation(context.player.getLocation()));
		final Location loc = context.player.getLocation().clone();

		// if player is not in a safe zone or their own faction territory, only allow teleport if no enemies are nearby
		if(FactionsPlugin.getInstance().conf().factions().homes().getTeleportAllowedEnemyDistance() > 0 &&
				!faction.isSafeZone() &&
				(!context.fPlayer.isInOwnTerritory() || !FactionsPlugin.getInstance().conf().factions().homes().isTeleportIgnoreEnemiesIfInOwnTerritory())) {
			World w = loc.getWorld();
			double x = loc.getX();
			double y = loc.getY();
			double z = loc.getZ();

			for(Player p : context.player.getServer().getOnlinePlayers()) {
				if(p == null || !p.isOnline() || p.isDead() || p == context.player || p.getWorld() != w) {
					continue;
				}

				if(context.fPlayer.getRelationTo(FPlayers.getInstance().getByPlayer(p)) != Relation.ENEMY) {
					continue;
				}

				Location l = p.getLocation();
				double dx = Math.abs(x - l.getX());
				double dy = Math.abs(y - l.getY());
				double dz = Math.abs(z - l.getZ());
				double max = FactionsPlugin.getInstance().conf().factions().homes().getTeleportAllowedEnemyDistance();

				// box-shaped distance check
				if(dx > max || dy > max || dz > max) {
					continue;
				}

				context.fPlayer.msg(TL.COMMAND_HOME_ENEMYNEAR, String.valueOf(FactionsPlugin.getInstance().conf().factions().homes().getTeleportAllowedEnemyDistance()));
				return;
			}
		}

		Location destination = targetFaction.getHome();
		FPlayerTeleportEvent tpEvent = new FPlayerTeleportEvent(context.fPlayer, destination, FPlayerTeleportEvent.PlayerTeleportReason.HOME);
		Bukkit.getServer().getPluginManager().callEvent(tpEvent);
		if(tpEvent.isCancelled()) {
			return;
		}

		context.doWarmUp(WarmUpUtil.Warmup.HOME, TL.WARMUPS_NOTIFY_TELEPORT, "Home", () -> {
			FactionsPlugin.getInstance().teleport(context.player, destination);
		}, this.plugin.conf().commands().home().getDelay());
	}

	@Override
	public TL getUsageTranslation() {
		return TL.COMMAND_HOME_DESCRIPTION;
	}

}
