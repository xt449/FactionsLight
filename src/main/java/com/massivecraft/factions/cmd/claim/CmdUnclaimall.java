package com.massivecraft.factions.cmd.claim;

import com.massivecraft.factions.Board;
import com.massivecraft.factions.FactionsPlugin;
import com.massivecraft.factions.cmd.CommandContext;
import com.massivecraft.factions.cmd.CommandRequirements;
import com.massivecraft.factions.cmd.FCommand;
import com.massivecraft.factions.event.LandUnclaimAllEvent;
import com.massivecraft.factions.perms.PermissibleAction;
import com.massivecraft.factions.struct.Permission;
import com.massivecraft.factions.util.TL;
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
			context.msg(TL.CLAIM_CANTCLAIM, context.faction.describeTo(context.fPlayer));
			return;
		}

		LandUnclaimAllEvent unclaimAllEvent = new LandUnclaimAllEvent(context.faction, context.fPlayer);
		Bukkit.getServer().getPluginManager().callEvent(unclaimAllEvent);
		if(unclaimAllEvent.isCancelled()) {
			return;
		}

		Board.getInstance().unclaimAll(context.faction.getId());
		context.faction.msg(TL.COMMAND_UNCLAIMALL_UNCLAIMED, context.fPlayer.describeTo(context.faction, true));

		if(FactionsPlugin.getInstance().configMain.logging().isLandUnclaims()) {
			FactionsPlugin.getInstance().log(TL.COMMAND_UNCLAIMALL_LOG.format(context.fPlayer.getName(), context.faction.getTag()));
		}
	}

	@Override
	public TL getUsageTranslation() {
		return TL.COMMAND_UNCLAIMALL_DESCRIPTION;
	}

}
