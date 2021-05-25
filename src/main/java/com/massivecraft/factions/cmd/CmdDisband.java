package com.massivecraft.factions.cmd;

import com.massivecraft.factions.*;
import com.massivecraft.factions.event.FPlayerLeaveEvent;
import com.massivecraft.factions.event.FactionDisbandEvent;
import com.massivecraft.factions.integration.Econ;
import com.massivecraft.factions.perms.PermissibleAction;
import com.massivecraft.factions.scoreboards.FTeamWrapper;
import com.massivecraft.factions.struct.Permission;
import com.massivecraft.factions.util.Localization;
import org.bukkit.Bukkit;


public class CmdDisband extends FCommand {

	public CmdDisband() {
		super();
		this.aliases.add("disband");

		this.optionalArgs.put("faction", "yours");

		this.requirements = new CommandRequirements.Builder(Permission.DISBAND).build();
	}

	@Override
	public void perform(CommandContext context) {
		// The faction, default to your own.. but null if console sender.
		IFaction faction = context.argAsFaction(0, context.fPlayer == null ? null : context.faction);
		if(faction == null) {
			return;
		}

		boolean isfaction = context.fPlayer != null && faction == context.faction;

		if(isfaction) {
			if(!faction.hasAccess(context.fPlayer, PermissibleAction.DISBAND)) {
				context.msg(Localization.GENERIC_NOPERMISSION.format(PermissibleAction.DISBAND));
				return;
			}
		} else {
			if(!Permission.DISBAND_ANY.has(context.sender, true)) {
				return;
			}
		}

		if(!faction.isNormal()) {
			context.msg(Localization.COMMAND_DISBAND_IMMUTABLE.toString());
			return;
		}
		if(faction.isPermanent()) {
			context.msg(Localization.COMMAND_DISBAND_MARKEDPERMANENT.toString());
			return;
		}
		if(!FactionsPlugin.getInstance().getLandRaidControl().canDisbandFaction(faction, context)) {
			return;
		}

		FactionDisbandEvent disbandEvent = new FactionDisbandEvent(context.player, faction.getId());
		Bukkit.getServer().getPluginManager().callEvent(disbandEvent);
		if(disbandEvent.isCancelled()) {
			return;
		}

		// Send FPlayerLeaveEvent for each player in the faction
		for(IFactionPlayer fplayer : faction.getFPlayers()) {
			Bukkit.getServer().getPluginManager().callEvent(new FPlayerLeaveEvent(fplayer, faction, FPlayerLeaveEvent.PlayerLeaveReason.DISBAND));
		}

		// Inform all players
		for(IFactionPlayer fplayer : IFactionPlayerManager.getInstance().getOnlinePlayers()) {
			String who = context.player == null ? Localization.GENERIC_SERVERADMIN.toString() : context.fPlayer.describeTo(fplayer);
			if(fplayer.getFaction() == faction) {
				fplayer.msg(Localization.COMMAND_DISBAND_BROADCAST_YOURS, who);
			} else {
				fplayer.msg(Localization.COMMAND_DISBAND_BROADCAST_NOTYOURS, who, faction.getTag(fplayer));
			}
		}
		if(FactionsPlugin.getInstance().conf().logging().isFactionDisband()) {
			//TODO: Format this correctly and translate.
			FactionsPlugin.getInstance().log("The faction " + faction.getTag() + " (" + faction.getId() + ") was disbanded by " + (context.player == null ? "console command" : context.fPlayer.getName()) + ".");
		}

		if(Econ.shouldBeUsed() && context.player != null) {
			//Give all the faction's money to the disbander
			double amount = Econ.getBalance(faction);
			Econ.transferMoney(context.fPlayer, faction, context.fPlayer, amount, false);

			if(amount > 0.0) {
				String amountString = Econ.moneyString(amount);
				context.msg(Localization.COMMAND_DISBAND_HOLDINGS, amountString);
				//TODO: Format this correctly and translate
				FactionsPlugin.getInstance().log(context.fPlayer.getName() + " has been given bank holdings of " + amountString + " from disbanding " + faction.getTag() + ".");
			}
		}

		Factions.getInstance().removeFaction(faction.getId());
		FTeamWrapper.applyUpdates(faction);
	}

	@Override
	public Localization getUsageTranslation() {
		return Localization.COMMAND_DISBAND_DESCRIPTION;
	}
}
