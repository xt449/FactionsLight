package com.massivecraft.factions.cmd;

import com.massivecraft.factions.FactionsPlugin;
import com.massivecraft.factions.struct.Permission;
import com.massivecraft.factions.util.Localization;

public class CmdBypass extends FCommand {

	public CmdBypass() {
		super();
		this.aliases.add("bypass");

		this.optionalArgs.put("on/off", "flip");

		this.requirements = new CommandRequirements.Builder(Permission.BYPASS)
				.playerOnly()
				.noDisableOnLock()
				.build();
	}

	@Override
	public void perform(CommandContext context) {
		context.fPlayer.setIsAdminBypassing(context.argAsBool(0, !context.fPlayer.isAdminBypassing()));

		// TODO: Move this to a transient field in the model??
		if(context.fPlayer.isAdminBypassing()) {
			context.fPlayer.msg(Localization.COMMAND_BYPASS_ENABLE.toString());
			FactionsPlugin.getInstance().log(context.fPlayer.getName() + Localization.COMMAND_BYPASS_ENABLELOG);
		} else {
			context.fPlayer.msg(Localization.COMMAND_BYPASS_DISABLE.toString());
			FactionsPlugin.getInstance().log(context.fPlayer.getName() + Localization.COMMAND_BYPASS_DISABLELOG);
		}
	}

	@Override
	public Localization getUsageTranslation() {
		return Localization.COMMAND_BYPASS_DESCRIPTION;
	}
}
