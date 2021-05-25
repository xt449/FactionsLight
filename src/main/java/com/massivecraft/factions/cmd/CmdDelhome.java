package com.massivecraft.factions.cmd;

import com.massivecraft.factions.FactionsPlugin;
import com.massivecraft.factions.perms.PermissibleAction;
import com.massivecraft.factions.perms.Role;
import com.massivecraft.factions.struct.Permission;
import com.massivecraft.factions.util.Localization;

public class CmdDelhome extends FCommand {

	public CmdDelhome() {
		this.aliases.add("delhome");

		this.requirements = new CommandRequirements.Builder(Permission.DELHOME)
				.memberOnly()
				.withAction(PermissibleAction.SETHOME)
				.build();
	}

	@Override
	public void perform(CommandContext context) {
		if(!FactionsPlugin.getInstance().conf().factions().homes().isEnabled()) {
			context.msg(Localization.COMMAND_SETHOME_DISABLED);
			return;
		}

		if(!context.faction.hasHome()) {
			context.msg(Localization.COMMAND_HOME_NOHOME + (context.fPlayer.getRole().value < Role.MODERATOR.value ? Localization.GENERIC_ASKYOURLEADER.toString() : Localization.GENERIC_YOUSHOULD.toString()));
			context.sendMessage(FCmdRoot.getInstance().cmdSethome.getUsageTemplate(context));
			return;
		}

		if(!context.payForCommand(FactionsPlugin.getInstance().conf().economy().getCostDelhome(), Localization.COMMAND_DELHOME_TOSET, Localization.COMMAND_DELHOME_FORSET)) {
			return;
		}

		context.faction.delHome();

		context.faction.msg(Localization.COMMAND_DELHOME_DEL, context.fPlayer.describeTo(context.faction, true));
	}

	@Override
	public Localization getUsageTranslation() {
		return Localization.COMMAND_DELHOME_DESCRIPTION;
	}

}
