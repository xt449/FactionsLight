package com.massivecraft.factions.landraidcontrol;

import com.massivecraft.factions.*;
import com.massivecraft.factions.cmd.CommandContext;
import com.massivecraft.factions.config.file.MainConfig;
import com.massivecraft.factions.event.PowerLossEvent;
import com.massivecraft.factions.perms.Relation;
import com.massivecraft.factions.util.Localization;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class PowerControl implements LandRaidControl {
	@Override
	public boolean isRaidable(IFaction faction) {
		return FactionsPlugin.getInstance().conf().factions().landRaidControl().power().isRaidability() && faction.isNormal() && !faction.isPeaceful() &&
				(FactionsPlugin.getInstance().conf().factions().landRaidControl().power().isRaidabilityOnEqualLandAndPower() ?
						(faction.getLandRounded() >= faction.getPowerRounded()) :
						(faction.getLandRounded() > faction.getPowerRounded())
				);
	}

	@Override
	public boolean hasLandInflation(IFaction faction) {
		return !faction.isPeaceful() && faction.getLandRounded() > faction.getPowerRounded();
	}

	@Override
	public int getLandLimit(IFaction faction) {
		return faction.getPowerRounded();
	}

	@Override
	public boolean canJoinFaction(IFaction faction, IFactionPlayer player, CommandContext context) {
		if(!FactionsPlugin.getInstance().conf().factions().landRaidControl().power().canLeaveWithNegativePower() && player.getPower() < 0) {
			if(context != null) {
				context.msg(Localization.COMMAND_JOIN_NEGATIVEPOWER, player.describeTo(context.fPlayer, true));
			}
			return false;
		}
		return true;
	}

	@Override
	public boolean canLeaveFaction(IFactionPlayer player) {
		if(!FactionsPlugin.getInstance().conf().factions().landRaidControl().power().canLeaveWithNegativePower() && player.getPower() < 0) {
			player.msg(Localization.LEAVE_NEGATIVEPOWER);
			return false;
		}
		return true;
	}

	@Override
	public boolean canDisbandFaction(IFaction faction, CommandContext context) {
		return true;
	}

	@Override
	public boolean canKick(IFactionPlayer toKick, CommandContext context) {
		if(!FactionsPlugin.getInstance().conf().factions().landRaidControl().power().canLeaveWithNegativePower() && toKick.getPower() < 0) {
			context.msg(Localization.COMMAND_KICK_NEGATIVEPOWER);
			return false;
		}
		if(!FactionsPlugin.getInstance().conf().commands().kick().isAllowKickInEnemyTerritory() &&
				IFactionClaimManager.getInstance().getFactionAt(toKick.getLastStoodAt()).getRelationTo(toKick.getFaction()) == Relation.ENEMY) {
			context.msg(Localization.COMMAND_KICK_ENEMYTERRITORY);
			return false;
		}
		return true;
	}

	@Override
	public void onRespawn(IFactionPlayer player) {
		this.update(player); // update power, so they won't have gained any while dead
	}

	@Override
	public void onQuit(IFactionPlayer player) {
		this.update(player); // Make sure player's power is up to date when they log off.
	}

	@Override
	public void update(IFactionPlayer player) {
		player.updatePower();
	}

	@Override
	public void onJoin(IFactionPlayer player) {
		player.losePowerFromBeingOffline();
	}

	@Override
	public void onDeath(Player player) {
		IFactionPlayer fplayer = IFactionPlayerManager.getInstance().getByPlayer(player);
		IFaction faction = IFactionClaimManager.getInstance().getFactionAt(new FactionClaim(player.getLocation()));

		MainConfig.Factions.LandRaidControl.Power powerConf = FactionsPlugin.getInstance().conf().factions().landRaidControl().power();
		PowerLossEvent powerLossEvent = new PowerLossEvent(faction, fplayer);
		// Check for no power loss conditions
		if(faction.isWarZone()) {
			// war zones always override worldsNoPowerLoss either way, thus this layout
			if(!powerConf.isWarZonePowerLoss()) {
				powerLossEvent.setMessage(Localization.PLAYER_POWER_NOLOSS_WARZONE.toString());
				powerLossEvent.setCancelled(true);
			}
			if(powerConf.getWorldsNoPowerLoss().contains(player.getWorld().getName())) {
				powerLossEvent.setMessage(Localization.PLAYER_POWER_LOSS_WARZONE.toString());
			}
		} else if(faction.isWilderness() && !powerConf.isWildernessPowerLoss() && !FactionsPlugin.getInstance().conf().factions().protection().getWorldsNoWildernessProtection().contains(player.getWorld().getName())) {
			powerLossEvent.setMessage(Localization.PLAYER_POWER_NOLOSS_WILDERNESS.toString());
			powerLossEvent.setCancelled(true);
		} else if(powerConf.getWorldsNoPowerLoss().contains(player.getWorld().getName())) {
			powerLossEvent.setMessage(Localization.PLAYER_POWER_NOLOSS_WORLD.toString());
			powerLossEvent.setCancelled(true);
		} else if(powerConf.isPeacefulMembersDisablePowerLoss() && fplayer.hasFaction() && fplayer.getFaction().isPeaceful()) {
			powerLossEvent.setMessage(Localization.PLAYER_POWER_NOLOSS_PEACEFUL.toString());
			powerLossEvent.setCancelled(true);
		} else {
			powerLossEvent.setMessage(Localization.PLAYER_POWER_NOW.toString());
		}

		// call Event
		Bukkit.getPluginManager().callEvent(powerLossEvent);

		fplayer.onDeath();
		if(!powerLossEvent.isCancelled()) {
			double startingPower = fplayer.getPower();
			fplayer.alterPower(-powerConf.getLossPerDeath());
			double powerDiff = fplayer.getPower() - startingPower;
			double vamp = powerConf.getVampirism();
			Player killer = player.getKiller();
			if(killer != null && vamp != 0D && powerDiff > 0) {
				double powerChange = vamp * powerDiff;
				IFactionPlayer fKiller = IFactionPlayerManager.getInstance().getByPlayer(killer);
				fKiller.alterPower(powerChange);
				fKiller.msg(Localization.PLAYER_POWER_VAMPIRISM_GAIN, powerChange, fplayer.describeTo(fKiller), fKiller.getPowerRounded(), fKiller.getPowerMaxRounded());
			}
		}
		// Send the message from the powerLossEvent
		final String msg = powerLossEvent.getMessage();
		if(msg != null && !msg.isEmpty()) {
			fplayer.msg(msg, fplayer.getPowerRounded(), fplayer.getPowerMaxRounded());
		}
	}
}
