package com.massivecraft.factions.cmd;

import com.massivecraft.factions.FactionsPlugin;
import com.massivecraft.factions.IFactionPlayer;
import com.massivecraft.factions.perms.PermissibleAction;
import com.massivecraft.factions.struct.Permission;
import com.massivecraft.factions.util.Localization;

public class CmdDelWarp extends FCommand {

	public CmdDelWarp() {
		super();
		this.aliases.add("delwarp");
		this.aliases.add("dw");
		this.aliases.add("deletewarp");

		this.requiredArgs.add("warp");

		this.requirements = new CommandRequirements.Builder(Permission.SETWARP)
				.memberOnly()
				.withAction(PermissibleAction.SETWARP)
				.build();
	}

	@Override
	public void perform(CommandContext context) {
		String warp = context.argAsString(0);
		if(context.faction.isWarp(warp)) {
			if(!transact(context.fPlayer, context)) {
				return;
			}
			context.faction.removeWarp(warp);
			context.msg(Localization.COMMAND_DELFWARP_DELETED, warp);
		} else {
			context.msg(Localization.COMMAND_DELFWARP_INVALID, warp);
		}
	}

	private boolean transact(IFactionPlayer player, CommandContext context) {
		return player.isAdminBypassing() || context.payForCommand(FactionsPlugin.getInstance().conf().economy().getCostDelWarp(), Localization.COMMAND_DELFWARP_TODELETE.toString(), Localization.COMMAND_DELFWARP_FORDELETE.toString());
	}

	@Override
	public Localization getUsageTranslation() {
		return Localization.COMMAND_DELFWARP_DESCRIPTION;
	}
}
