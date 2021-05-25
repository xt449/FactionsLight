package com.massivecraft.factions.cmd;

import com.massivecraft.factions.FactionsPlugin;
import com.massivecraft.factions.struct.Permission;
import com.massivecraft.factions.util.Localization;


public class CmdVersion extends FCommand {

	public CmdVersion() {
		this.aliases.add("version");
		this.aliases.add("ver");

		this.requirements = new CommandRequirements.Builder(Permission.VERSION).noDisableOnLock().build();
	}

	@Override
	public void perform(CommandContext context) {
		context.msg(Localization.COMMAND_VERSION_VERSION, FactionsPlugin.getInstance().getDescription().getFullName());
	}

	@Override
	public Localization getUsageTranslation() {
		return Localization.COMMAND_VERSION_DESCRIPTION;
	}
}
