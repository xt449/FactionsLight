package com.massivecraft.factions.cmd;

import com.massivecraft.factions.FactionsPlugin;
import com.massivecraft.factions.struct.Permission;
import com.massivecraft.factions.util.Localization;

public class CmdChatSpy extends FCommand {

	public CmdChatSpy() {
		super();
		this.aliases.add("chatspy");

		this.optionalArgs.put("on/off", "flip");

		this.requirements = new CommandRequirements.Builder(Permission.CHATSPY)
				.playerOnly()
				.noDisableOnLock()
				.build();
	}

	@Override
	public void perform(CommandContext context) {
		context.fPlayer.setSpyingChat(context.argAsBool(0, !context.fPlayer.isSpyingChat()));

		if(context.fPlayer.isSpyingChat()) {
			context.fPlayer.msg(Localization.COMMAND_CHATSPY_ENABLE);
			FactionsPlugin.getInstance().log(context.fPlayer.getName() + Localization.COMMAND_CHATSPY_ENABLELOG);
		} else {
			context.fPlayer.msg(Localization.COMMAND_CHATSPY_DISABLE);
			FactionsPlugin.getInstance().log(context.fPlayer.getName() + Localization.COMMAND_CHATSPY_DISABLELOG);
		}
	}

	@Override
	public Localization getUsageTranslation() {
		return Localization.COMMAND_CHATSPY_DESCRIPTION;
	}
}