package com.massivecraft.factions.cmd.claim;

import com.massivecraft.factions.FactionsPlugin;
import com.massivecraft.factions.IFactionClaimManager;
import com.massivecraft.factions.cmd.CommandContext;
import com.massivecraft.factions.cmd.CommandRequirements;
import com.massivecraft.factions.cmd.FCommand;
import com.massivecraft.factions.event.LandUnclaimAllEvent;
import com.massivecraft.factions.integration.Econ;
import com.massivecraft.factions.perms.PermissibleAction;
import com.massivecraft.factions.struct.Permission;
import com.massivecraft.factions.util.Localization;
import org.bukkit.Bukkit;

public class CmdUnclaimall extends FCommand {

	public CmdUnclaimall() {
		this.aliases.add("unclaimall");
		this.aliases.add("declaimall");

		this.requirements = new CommandRequirements.Builder(Permission.UNCLAIM_ALL)
				.memberOnly()
				.build();
	}

	@Override
	public void perform(CommandContext context) {
		if(!context.faction.hasAccess(context.fPlayer, PermissibleAction.TERRITORY)) {
			context.msg(Localization.CLAIM_CANTCLAIM, context.faction.describeTo(context.fPlayer));
			return;
		}

		if(Econ.shouldBeUsed()) {
			double refund = Econ.calculateTotalLandRefund(context.faction.getLandRounded());
			if(FactionsPlugin.getInstance().conf().economy().isBankEnabled() && FactionsPlugin.getInstance().conf().economy().isBankFactionPaysLandCosts()) {
				if(!Econ.modifyMoney(context.faction, refund, Localization.COMMAND_UNCLAIMALL_TOUNCLAIM.toString(), Localization.COMMAND_UNCLAIMALL_FORUNCLAIM.toString())) {
					return;
				}
			} else {
				if(!Econ.modifyMoney(context.fPlayer, refund, Localization.COMMAND_UNCLAIMALL_TOUNCLAIM.toString(), Localization.COMMAND_UNCLAIMALL_FORUNCLAIM.toString())) {
					return;
				}
			}
		}

		LandUnclaimAllEvent unclaimAllEvent = new LandUnclaimAllEvent(context.faction, context.fPlayer);
		Bukkit.getServer().getPluginManager().callEvent(unclaimAllEvent);
		if(unclaimAllEvent.isCancelled()) {
			return;
		}

		IFactionClaimManager.getInstance().unclaimAll(context.faction.getId());
		context.faction.msg(Localization.COMMAND_UNCLAIMALL_UNCLAIMED, context.fPlayer.describeTo(context.faction, true));

		if(FactionsPlugin.getInstance().conf().logging().isLandUnclaims()) {
			FactionsPlugin.getInstance().log(Localization.COMMAND_UNCLAIMALL_LOG.format(context.fPlayer.getName(), context.faction.getTag()));
		}
	}

	@Override
	public Localization getUsageTranslation() {
		return Localization.COMMAND_UNCLAIMALL_DESCRIPTION;
	}

}
