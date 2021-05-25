package com.massivecraft.factions.cmd;

import com.massivecraft.factions.IFactionPlayer;
import com.massivecraft.factions.struct.Permission;
import com.massivecraft.factions.util.Localization;

public class CmdModifyPower extends FCommand {

	public CmdModifyPower() {
		super();

		this.aliases.add("pm");
		this.aliases.add("mp");
		this.aliases.add("modifypower");
		this.aliases.add("modpower");

		this.requiredArgs.add("name");
		this.requiredArgs.add("power");

		this.requirements = new CommandRequirements.Builder(Permission.MODIFY_POWER).build();
	}

	@Override
	public void perform(CommandContext context) {
		// /f modify <name> #
		IFactionPlayer player = context.argAsBestFPlayerMatch(0);
		Double number = context.argAsDouble(1); // returns null if not a Double.

		if(player == null || number == null) {
			context.sender.sendMessage(getHelpShort());
			return;
		}

		player.alterPower(number);
		int newPower = player.getPowerRounded(); // int so we don't have super long doubles.
		context.msg(Localization.COMMAND_MODIFYPOWER_ADDED, number, player.getName(), newPower);
	}

	@Override
	public Localization getUsageTranslation() {
		return Localization.COMMAND_MODIFYPOWER_DESCRIPTION;
	}
}
