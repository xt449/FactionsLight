package com.massivecraft.factions.cmd;

import com.massivecraft.factions.perms.PermissibleAction;
import com.massivecraft.factions.struct.Permission;
import com.massivecraft.factions.util.TL;

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
			context.faction.removeWarp(warp);
			context.msg(TL.COMMAND_DELFWARP_DELETED, warp);
		} else {
			context.msg(TL.COMMAND_DELFWARP_INVALID, warp);
		}
	}

	@Override
	public TL getUsageTranslation() {
		return TL.COMMAND_DELFWARP_DESCRIPTION;
	}
}
