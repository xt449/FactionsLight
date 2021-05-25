package com.massivecraft.factions.cmd;

import com.massivecraft.factions.perms.Role;
import com.massivecraft.factions.struct.Permission;
import com.massivecraft.factions.util.Localization;

public class CmdSetDefaultRole extends FCommand {

	public CmdSetDefaultRole() {
		super();

		this.aliases.add("defaultrole");
		this.aliases.add("defaultrank");
		this.aliases.add("default");
		this.aliases.add("def");
		this.requiredArgs.add("role");

		this.requirements = new CommandRequirements.Builder(Permission.DEFAULTRANK)
				.memberOnly()
				.withRole(Role.ADMIN)
				.build();
	}

	@Override
	public void perform(CommandContext context) {
		Role target = Role.fromString(context.argAsString(0).toUpperCase());
		if(target == null) {
			context.msg(Localization.COMMAND_SETDEFAULTROLE_INVALIDROLE, context.argAsString(0));
			return;
		}

		if(target == Role.ADMIN) {
			context.msg(Localization.COMMAND_SETDEFAULTROLE_NOTTHATROLE, context.argAsString(0));
			return;
		}

		context.faction.setDefaultRole(target);
		context.msg(Localization.COMMAND_SETDEFAULTROLE_SUCCESS, target.nicename);
	}

	@Override
	public Localization getUsageTranslation() {
		return Localization.COMMAND_SETDEFAULTROLE_DESCRIPTION;
	}
}
