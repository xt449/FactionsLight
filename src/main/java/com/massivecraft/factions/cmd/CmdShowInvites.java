package com.massivecraft.factions.cmd;

import com.massivecraft.factions.FPlayer;
import com.massivecraft.factions.FPlayers;
import com.massivecraft.factions.Permission;
import com.massivecraft.factions.util.TL;
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
		FancyMessage msg = new FancyMessage(TL.COMMAND_SHOWINVITES_PENDING.toString()).color(ChatColor.GOLD);
		for(String id : context.faction.getInvites()) {
			FPlayer fp = FPlayers.getInstance().getById(id);
			String name = fp != null ? fp.getName() : id;
			msg.then(name + " ").color(ChatColor.WHITE).tooltip(TL.COMMAND_SHOWINVITES_CLICKTOREVOKE.format(name)).command("/f deinvite " + name);
		}

		context.sendFancyMessage(msg);
	}

	@Override
	public TL getUsageTranslation() {
		return TL.COMMAND_SHOWINVITES_DESCRIPTION;
	}


}
