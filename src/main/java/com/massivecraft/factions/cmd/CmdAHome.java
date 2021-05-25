package com.massivecraft.factions.cmd;

import com.massivecraft.factions.IFactionPlayer;
import com.massivecraft.factions.IFaction;
import com.massivecraft.factions.FactionsPlugin;
import com.massivecraft.factions.event.FPlayerTeleportEvent;
import com.massivecraft.factions.struct.Permission;
import com.massivecraft.factions.util.TL;
import org.bukkit.Bukkit;
import org.bukkit.Location;

public class CmdAHome extends FCommand {

	public CmdAHome() {
		super();
		this.aliases.add("ahome");

		this.requiredArgs.add("player");

		this.requirements = new CommandRequirements.Builder(Permission.AHOME).noDisableOnLock().build();
	}

	@Override
	public void perform(CommandContext context) {
		IFactionPlayer target = context.argAsBestFPlayerMatch(0);
		if(target == null) {
			context.msg(TL.GENERIC_NOPLAYERMATCH, context.argAsString(0));
			return;
		}

		if(target.isOnline()) {
			IFaction faction = target.getFaction();
			if(faction.hasHome()) {
				Location destination = faction.getHome();
				FPlayerTeleportEvent tpEvent = new FPlayerTeleportEvent(target, destination, FPlayerTeleportEvent.PlayerTeleportReason.AHOME);
				Bukkit.getServer().getPluginManager().callEvent(tpEvent);
				if(tpEvent.isCancelled()) {
					return;
				}
				FactionsPlugin.getInstance().teleport(target.getPlayer(), destination).thenAccept(success -> {
					if(success) {
						context.msg(TL.COMMAND_AHOME_SUCCESS, target.getName());
						target.msg(TL.COMMAND_AHOME_TARGET);
					}
				});

			} else {
				context.msg(TL.COMMAND_AHOME_NOHOME, target.getName());
			}
		} else {
			context.msg(TL.COMMAND_AHOME_OFFLINE, target.getName());
		}
	}

	@Override
	public TL getUsageTranslation() {
		return TL.COMMAND_AHOME_DESCRIPTION;
	}
}
