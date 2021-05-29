package com.massivecraft.factions.cmd;

import com.massivecraft.factions.FactionsPlugin;
import com.massivecraft.factions.perms.Role;
import com.massivecraft.factions.struct.Permission;
import com.massivecraft.factions.util.TL;

public class CmdBoom extends FCommand {

	public CmdBoom() {
		super();
		this.aliases.add("noboom");
		this.aliases.add("explosions");
		this.aliases.add("toggleexplosions");

		this.optionalArgs.put("on/off", "flip");

		this.requirements = new CommandRequirements.Builder(Permission.NO_BOOM)
				.memberOnly()
				.withRole(Role.MODERATOR)
				.build();
	}

	@Override
	public void perform(CommandContext context) {
		if(!context.faction.isPeaceful()) {
			context.msg(TL.COMMAND_BOOM_PEACEFULONLY);
			return;
		}

		context.faction.setPeacefulExplosionsEnabled(context.argAsBool(0, !context.faction.getPeacefulExplosionsEnabled()));

		String enabled = context.faction.noExplosionsInTerritory() ? TL.GENERIC_DISABLED.toString() : TL.GENERIC_ENABLED.toString();

		// Inform
		context.faction.msg(TL.COMMAND_BOOM_ENABLED, context.fPlayer.describeTo(context.faction), enabled);
	}

	@Override
	public TL getUsageTranslation() {
		return TL.COMMAND_BOOM_DESCRIPTION;
	}
}
