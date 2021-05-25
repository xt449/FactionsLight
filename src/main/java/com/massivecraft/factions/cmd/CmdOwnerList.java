package com.massivecraft.factions.cmd;

import com.massivecraft.factions.FactionClaim;
import com.massivecraft.factions.FactionsPlugin;
import com.massivecraft.factions.IFactionClaimManager;
import com.massivecraft.factions.struct.Permission;
import com.massivecraft.factions.util.Localization;


public class CmdOwnerList extends FCommand {

	public CmdOwnerList() {
		super();
		this.aliases.add("ownerlist");

		this.requirements = new CommandRequirements.Builder(Permission.OWNERLIST)
				.playerOnly()
				.noDisableOnLock()
				.build();
	}

	@Override
	public void perform(CommandContext context) {
		boolean hasBypass = context.fPlayer.isAdminBypassing();

		if(!hasBypass && !context.assertHasFaction()) {
			return;
		}

		if(!FactionsPlugin.getInstance().conf().factions().ownedArea().isEnabled()) {
			context.msg(Localization.COMMAND_OWNERLIST_DISABLED);
			return;
		}

		FactionClaim flocation = new FactionClaim(context.fPlayer);

		if(IFactionClaimManager.getInstance().getFactionAt(flocation) != context.faction) {
			if(!hasBypass) {
				context.msg(Localization.COMMAND_OWNERLIST_WRONGFACTION);
				return;
			}
			//TODO: This code won't ever be called.
			context.faction = IFactionClaimManager.getInstance().getFactionAt(flocation);
			if(!context.faction.isNormal()) {
				context.msg(Localization.COMMAND_OWNERLIST_NOTCLAIMED);
				return;
			}
		}

		String owners = context.faction.getOwnerListString(flocation);

		if(owners == null || owners.isEmpty()) {
			context.msg(Localization.COMMAND_OWNERLIST_NONE);
			return;
		}

		context.msg(Localization.COMMAND_OWNERLIST_OWNERS, owners);
	}

	@Override
	public Localization getUsageTranslation() {
		return Localization.COMMAND_OWNERLIST_DESCRIPTION;
	}
}
