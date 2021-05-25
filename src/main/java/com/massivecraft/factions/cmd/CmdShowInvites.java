package com.massivecraft.factions.cmd;

import com.massivecraft.factions.FactionsPlugin;
import com.massivecraft.factions.IFactionPlayer;
import com.massivecraft.factions.IFactionPlayerManager;
import com.massivecraft.factions.struct.Permission;
import com.massivecraft.factions.util.Localization;
import mkremins.fanciful.FancyMessage;
import org.bukkit.ChatColor;

public class CmdShowInvites extends FCommand {

	public CmdShowInvites() {
		super();
		this.aliases.add("showinvites");

		this.requirements = new CommandRequirements.Builder(Permission.SHOW_INVITES)
				.memberOnly()
				.build();
	}

	@Override
	public void perform(CommandContext context) {
		FancyMessage msg = new FancyMessage(Localization.COMMAND_SHOWINVITES_PENDING.toString()).color(ChatColor.GOLD);
		for(String id : context.faction.getInvites()) {
			IFactionPlayer fp = IFactionPlayerManager.getInstance().getById(id);
			String name = fp != null ? fp.getName() : id;
			msg.then(name + " ").color(ChatColor.WHITE).tooltip(Localization.COMMAND_SHOWINVITES_CLICKTOREVOKE.format(name)).command("/" + FactionsPlugin.getInstance().conf().getCommandBase().get(0) + " deinvite " + name);
		}

		context.sendFancyMessage(msg);
	}

	@Override
	public Localization getUsageTranslation() {
		return Localization.COMMAND_SHOWINVITES_DESCRIPTION;
	}


}
