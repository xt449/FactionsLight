package com.massivecraft.factions.landraidcontrol;

import com.massivecraft.factions.*;
import com.massivecraft.factions.cmd.CommandContext;
import com.massivecraft.factions.config.file.MainConfig;
import com.massivecraft.factions.event.DTRLossEvent;
import com.massivecraft.factions.perms.Relation;
import com.massivecraft.factions.util.Localization;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class DTRControl implements LandRaidControl {
	private static FactionsPlugin plugin;

	public static String round(double dtr) {
		return BigDecimal.valueOf(dtr).setScale(conf().getDecimalDigits(), RoundingMode.UP).toPlainString();
	}

	private static MainConfig.Factions.LandRaidControl.DTR conf() {
		return plugin.conf().factions().landRaidControl().dtr();
	}

	public DTRControl() {
		plugin = FactionsPlugin.getInstance();
	}

	@Override
	public boolean isRaidable(IFaction faction) {
		return !faction.isPeaceful() && faction.getDTR() <= 0;
	}

	@Override
	public boolean hasLandInflation(IFaction faction) {
		return false; // fail all attempts at claiming
	}

	@Override
	public int getLandLimit(IFaction faction) {
		return conf().getLandStarting() + (faction.getFPlayers().size() * conf().getLandPerPlayer());
	}

	@Override
	public boolean canJoinFaction(IFaction faction, IFactionPlayer player, CommandContext context) {
		if(faction.isFrozenDTR() && conf().isFreezePreventsJoin()) {
			context.msg(Localization.DTR_CANNOT_FROZEN);
			return false;
		}
		return true;
	}

	@Override
	public boolean canLeaveFaction(IFactionPlayer player) {
		if(player.getFaction().isFrozenDTR() && conf().isFreezePreventsLeave()) {
			player.msg(Localization.DTR_CANNOT_FROZEN);
			return false;
		}
		return true;
	}

	@Override
	public boolean canDisbandFaction(IFaction faction, CommandContext context) {
		if(faction.isFrozenDTR() && conf().isFreezePreventsDisband()) {
			context.msg(Localization.DTR_CANNOT_FROZEN);
			return false;
		}
		return true;
	}

	@Override
	public boolean canKick(IFactionPlayer toKick, CommandContext context) {
		if(toKick.getFaction().isNormal()) {
			IFaction faction = toKick.getFaction();
			if(!FactionsPlugin.getInstance().conf().commands().kick().isAllowKickInEnemyTerritory() &&
					IFactionClaimManager.getInstance().getFactionAt(toKick.getLastStoodAt()).getRelationTo(faction) == Relation.ENEMY) {
				context.msg(Localization.COMMAND_KICK_ENEMYTERRITORY);
				return false;
			}
			if(faction.isFrozenDTR() && conf().getFreezeKickPenalty() > 0) {
				faction.setDTR(Math.min(conf().getMinDTR(), faction.getDTR() - conf().getFreezeKickPenalty()));
				context.msg(Localization.DTR_KICK_PENALTY);
			}
		}
		return true;
	}

	@Override
	public void onRespawn(IFactionPlayer player) {
		// Handled on death
	}

	@Override
	public void update(IFactionPlayer player) {
		if(player.getFaction().isNormal()) {
			this.updateDTR(player.getFaction());
		}
	}

	@Override
	public void onDeath(Player player) {
		IFactionPlayer fplayer = IFactionPlayerManager.getInstance().getByPlayer(player);
		IFaction faction = fplayer.getFaction();
		if(!faction.isNormal()) {
			return;
		}

		DTRLossEvent dtrLossEvent = new DTRLossEvent(faction, fplayer);

		// call Event
		Bukkit.getPluginManager().callEvent(dtrLossEvent);

		if(!dtrLossEvent.isCancelled()) {
			faction.setDTR(Math.max(conf().getMinDTR(), faction.getDTR() - conf().getLossPerDeath(player.getWorld())));
			faction.setFrozenDTR(System.currentTimeMillis() + (conf().getFreezeTime() * 1000));
		}
	}

	@Override
	public void onQuit(IFactionPlayer player) {
		this.update(player);
	}

	@Override
	public void onJoin(IFactionPlayer player) {
		if(player.getFaction().isNormal()) {
			this.updateDTR(player.getFaction(), 1);
		}
	}

	public void updateDTR(IFaction faction) {
		this.updateDTR(faction, 0);
	}

	public void updateDTR(IFaction faction, int minusPlayer) {
		long now = System.currentTimeMillis();
		if(faction.getFrozenDTRUntilTime() > now) {
			// Not yet time to regen
			return;
		}
		long millisPassed = now - Math.max(faction.getLastDTRUpdateTime(), faction.getFrozenDTRUntilTime());
		long onlineInEnabledWorlds = faction.getOnlinePlayers().stream().filter(p -> plugin.worldUtil().isEnabled(p.getWorld())).count();
		double rate = Math.min(conf().getRegainPerMinuteMaxRate(), Math.max(0, onlineInEnabledWorlds - minusPlayer) * conf().getRegainPerMinutePerPlayer());
		double regain = (millisPassed / (60D * 1000D)) * rate;
		faction.setDTR(Math.min(faction.getDTRWithoutUpdate() + regain, this.getMaxDTR(faction)));
	}

	public double getMaxDTR(IFaction faction) {
		return Math.min(conf().getStartingDTR() + (conf().getPerPlayer() * faction.getFPlayers().size()), conf().getMaxDTR());
	}
}
