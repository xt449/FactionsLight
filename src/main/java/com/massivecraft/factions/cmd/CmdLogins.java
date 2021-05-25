package com.massivecraft.factions.cmd;

import com.massivecraft.factions.struct.Permission;
import com.massivecraft.factions.util.Localization;

public class CmdLogins extends FCommand {

	public CmdLogins() {
		super();
		this.aliases.add("login");
		this.aliases.add("logins");
		this.aliases.add("logout");
		this.aliases.add("logouts");

		this.requirements = new CommandRequirements.Builder(Permission.MONITOR_LOGINS)
				.memberOnly()
				.build();
	}

	@Override
	public void perform(CommandContext context) {
		boolean monitor = context.fPlayer.isMonitoringJoins();
		context.msg(Localization.COMMAND_LOGINS_TOGGLE, String.valueOf(!monitor));
		context.fPlayer.setMonitorJoins(!monitor);
	}

	@Override
	public Localization getUsageTranslation() {
		return Localization.COMMAND_LOGINS_DESCRIPTION;
	}
}
