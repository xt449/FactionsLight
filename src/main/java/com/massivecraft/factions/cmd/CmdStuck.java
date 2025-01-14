package com.massivecraft.factions.cmd;

import com.massivecraft.factions.*;
import com.massivecraft.factions.event.FPlayerTeleportEvent;
import com.massivecraft.factions.util.SpiralTask;
import com.massivecraft.factions.util.TL;
import io.papermc.lib.PaperLib;
import org.apache.commons.lang.time.DurationFormatUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class CmdStuck extends FCommand {

	public CmdStuck() {
		super();

		this.aliases.add("stuck");
		this.aliases.add("halp!"); // halp! c:

		this.requirements = new CommandRequirements.Builder(Permission.STUCK).build();
	}

	@Override
	public void perform(final CommandContext context) {
		final Player player = context.fPlayer.getPlayer();
		final Location sentAt = player.getLocation();
		final FLocation chunk = context.fPlayer.getLastStoodAt();
		// TODO handle delay 0
		final long delay = FactionsPlugin.getInstance().configMain.commands().stuck().delay();
		final int radius = FactionsPlugin.getInstance().configMain.commands().stuck().radius();

		if(FactionsPlugin.getInstance().stuckMap.containsKey(player.getUniqueId())) {
			long wait = FactionsPlugin.getInstance().timers.get(player.getUniqueId()) - System.currentTimeMillis();
			String time = DurationFormatUtils.formatDuration(wait, TL.COMMAND_STUCK_TIMEFORMAT.toString(), true);
			context.msg(TL.COMMAND_STUCK_EXISTS, time);
		} else {

			FPlayerTeleportEvent tpEvent = new FPlayerTeleportEvent(context.fPlayer, null, FPlayerTeleportEvent.PlayerTeleportReason.STUCK);
			Bukkit.getServer().getPluginManager().callEvent(tpEvent);
			if(tpEvent.isCancelled()) {
				return;
			}

			final int id = new BukkitRunnable() {
				@Override
				public void run() {
					if(!FactionsPlugin.getInstance().stuckMap.containsKey(player.getUniqueId())) {
						return;
					}

					// check for world difference or radius exceeding
					final World world = chunk.getWorld();
					if(world.getUID() != player.getWorld().getUID() || sentAt.distance(player.getLocation()) > radius) {
						context.msg(TL.COMMAND_STUCK_OUTSIDE.format(radius));
						FactionsPlugin.getInstance().timers.remove(player.getUniqueId());
						FactionsPlugin.getInstance().stuckMap.remove(player.getUniqueId());
						return;
					}

					final Board board = Board.getInstance();
					// spiral task to find nearest wilderness chunk
					new SpiralTask(new FLocation(context.player), radius * 2) {

						@Override
						public boolean work() {
							FLocation chunk = currentFLocation();

							Faction faction = board.getFactionAt(chunk);
							if(faction.isWilderness()) {
								int cx = FLocation.chunkToBlock((int) chunk.getX());
								int cz = FLocation.chunkToBlock((int) chunk.getZ());
								int y = world.getHighestBlockYAt(cx, cz);
								Location tp = new Location(world, cx, y, cz);
								context.msg(TL.COMMAND_STUCK_TELEPORT, tp.getBlockX(), tp.getBlockY(), tp.getBlockZ());
								FactionsPlugin.getInstance().timers.remove(player.getUniqueId());
								FactionsPlugin.getInstance().stuckMap.remove(player.getUniqueId());
								PaperLib.teleportAsync(player, tp);
								this.stop();
								return false;
							}
							return true;
						}
					};
				}
			}.runTaskLater(FactionsPlugin.getInstance(), delay * 20).getTaskId();

			FactionsPlugin.getInstance().timers.put(player.getUniqueId(), System.currentTimeMillis() + (delay * 1000));
			long wait = FactionsPlugin.getInstance().timers.get(player.getUniqueId()) - System.currentTimeMillis();
			String time = DurationFormatUtils.formatDuration(wait, TL.COMMAND_STUCK_TIMEFORMAT.toString(), true);
			context.msg(TL.COMMAND_STUCK_START, time);
			FactionsPlugin.getInstance().stuckMap.put(player.getUniqueId(), id);
		}
	}

	@Override
	public TL getUsageTranslation() {
		return TL.COMMAND_STUCK_DESCRIPTION;
	}
}
