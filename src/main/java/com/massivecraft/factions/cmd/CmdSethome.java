package com.massivecraft.factions.cmd;

import com.massivecraft.factions.FactionClaim;
import com.massivecraft.factions.FactionsPlugin;
import com.massivecraft.factions.IFactionClaimManager;
import com.massivecraft.factions.perms.PermissibleAction;
import com.massivecraft.factions.struct.Permission;
import com.massivecraft.factions.util.Localization;

public class CmdSethome extends FCommand {

	public CmdSethome() {
		this.aliases.add("sethome");

		this.requirements = new CommandRequirements.Builder(Permission.SETHOME)
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

		// Can the player set the faction home HERE?
		if(!Permission.BYPASS.has(context.player) &&
				FactionsPlugin.getInstance().conf().factions().homes().isMustBeInClaimedTerritory() &&
				IFactionClaimManager.getInstance().getFactionAt(new FactionClaim(context.player)) != context.faction) {
			context.msg(Localization.COMMAND_SETHOME_NOTCLAIMED);
			return;
		}

		// if economy is enabled, they're not on the bypass list, and this command has a cost set, make 'em pay
		if(!context.payForCommand(FactionsPlugin.getInstance().conf().economy().getCostSethome(), Localization.COMMAND_SETHOME_TOSET, Localization.COMMAND_SETHOME_FORSET)) {
			return;
		}

		context.faction.setHome(context.player.getLocation());

		context.faction.msg(Localization.COMMAND_SETHOME_SET, context.fPlayer.describeTo(context.faction, true));
		context.faction.sendMessage(FCmdRoot.getInstance().cmdHome.getUsageTemplate(context));
        /*
        if (faction != context.faction) {
            context.msg(TL.COMMAND_SETHOME_SETOTHER, faction.getTag(context.fPlayer));
        }*/
	}

	@Override
	public Localization getUsageTranslation() {
		return Localization.COMMAND_SETHOME_DESCRIPTION;
	}

}
