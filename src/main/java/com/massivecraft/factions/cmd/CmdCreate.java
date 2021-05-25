package com.massivecraft.factions.cmd;

import com.massivecraft.factions.*;
import com.massivecraft.factions.event.FPlayerJoinEvent;
import com.massivecraft.factions.event.FactionAttemptCreateEvent;
import com.massivecraft.factions.event.FactionCreateEvent;
import com.massivecraft.factions.perms.Role;
import com.massivecraft.factions.struct.Permission;
import com.massivecraft.factions.util.Localization;
import com.massivecraft.factions.util.MiscUtil;
import org.bukkit.Bukkit;

import java.util.ArrayList;


public class CmdCreate extends FCommand {

	public CmdCreate() {
		super();
		this.aliases.add("create");

		this.requiredArgs.add("faction tag");

		this.requirements = new CommandRequirements.Builder(Permission.CREATE)
				.playerOnly()
				.build();
	}

	@Override
	public void perform(CommandContext context) {
		String tag = context.argAsString(0);

		if(context.fPlayer.hasFaction()) {
			context.msg(Localization.COMMAND_CREATE_MUSTLEAVE);
			return;
		}

		if(Factions.getInstance().isTagTaken(tag)) {
			context.msg(Localization.COMMAND_CREATE_INUSE);
			return;
		}

		ArrayList<String> tagValidationErrors = MiscUtil.validateTag(tag);
		if(tagValidationErrors.size() > 0) {
			context.sendMessage(tagValidationErrors);
			return;
		}

		// if economy is enabled, they're not on the bypass list, and this command has a cost set, make sure they can pay
		if(!context.canAffordCommand(FactionsPlugin.getInstance().conf().economy().getCostCreate(), Localization.COMMAND_CREATE_TOCREATE.toString())) {
			return;
		}

		FactionAttemptCreateEvent attemptEvent = new FactionAttemptCreateEvent(context.player, tag);
		Bukkit.getServer().getPluginManager().callEvent(attemptEvent);
		if(attemptEvent.isCancelled()) {
			return;
		}

		// then make 'em pay (if applicable)
		if(!context.payForCommand(FactionsPlugin.getInstance().conf().economy().getCostCreate(), Localization.COMMAND_CREATE_TOCREATE, Localization.COMMAND_CREATE_FORCREATE)) {
			return;
		}

		IFaction faction = Factions.getInstance().createFaction();

		// TODO: Why would this even happen??? Auto increment clash??
		if(faction == null) {
			context.msg(Localization.COMMAND_CREATE_ERROR);
			return;
		}

		// finish setting up the Faction
		faction.setTag(tag);

		// trigger the faction join event for the creator
		FPlayerJoinEvent joinEvent = new FPlayerJoinEvent(IFactionPlayerManager.getInstance().getByPlayer(context.player), faction, FPlayerJoinEvent.PlayerJoinReason.CREATE);
		Bukkit.getServer().getPluginManager().callEvent(joinEvent);
		// join event cannot be cancelled or you'll have an empty faction

		// finish setting up the FPlayer
		context.fPlayer.setRole(Role.ADMIN);
		context.fPlayer.setFaction(faction);

		// trigger the faction creation event
		FactionCreateEvent createEvent = new FactionCreateEvent(context.player, tag, faction);
		Bukkit.getServer().getPluginManager().callEvent(createEvent);

		for(IFactionPlayer follower : IFactionPlayerManager.getInstance().getOnlinePlayers()) {
			follower.msg(Localization.COMMAND_CREATE_CREATED, context.fPlayer.describeTo(follower, true), faction.getTag(follower));
		}

		context.msg(Localization.COMMAND_CREATE_YOUSHOULD, FCmdRoot.getInstance().cmdDescription.getUsageTemplate(context));

		if(FactionsPlugin.getInstance().conf().logging().isFactionCreate()) {
			FactionsPlugin.getInstance().log(context.fPlayer.getName() + Localization.COMMAND_CREATE_CREATEDLOG + tag);
		}
	}

	@Override
	public Localization getUsageTranslation() {
		return Localization.COMMAND_CREATE_DESCRIPTION;
	}

}
