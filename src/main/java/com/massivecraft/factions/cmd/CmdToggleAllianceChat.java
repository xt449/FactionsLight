package com.massivecraft.factions.cmd;

import com.massivecraft.factions.FactionsPlugin;
import com.massivecraft.factions.struct.Permission;
import com.massivecraft.factions.util.Localization;

public class CmdToggleAllianceChat extends FCommand {

	public CmdToggleAllianceChat() {
		super();
		this.aliases.add("tac");
		this.aliases.add("togglealliancechat");
		this.aliases.add("ac");

		this.requirements = new CommandRequirements.Builder(Permission.TOGGLE_ALLIANCE_CHAT)
				.memberOnly()
				.noDisableOnLock()
				.build();
	}

	@Override
	public Localization getUsageTranslation() {
		return Localization.COMMAND_TOGGLEALLIANCECHAT_DESCRIPTION;
	}

	@Override
	public void perform(CommandContext context) {
		if(!FactionsPlugin.getInstance().conf().factions().chat().isFactionOnlyChat()) {
			context.msg(Localization.COMMAND_CHAT_DISABLED.toString());
			return;
		}

		boolean ignoring = context.fPlayer.isIgnoreAllianceChat();

		context.msg(ignoring ? Localization.COMMAND_TOGGLEALLIANCECHAT_UNIGNORE : Localization.COMMAND_TOGGLEALLIANCECHAT_IGNORE);
		context.fPlayer.setIgnoreAllianceChat(!ignoring);
	}
}
