package com.massivecraft.factions.cmd.claim;

import com.massivecraft.factions.FactionsPlugin;
import com.massivecraft.factions.IFaction;
import com.massivecraft.factions.cmd.CommandContext;
import com.massivecraft.factions.cmd.CommandRequirements;
import com.massivecraft.factions.cmd.FCommand;
import com.massivecraft.factions.struct.Permission;
import com.massivecraft.factions.util.Localization;
import org.bukkit.Location;
import org.bukkit.block.BlockFace;

public class CmdClaimLine extends FCommand {

	public static final BlockFace[] axis = {BlockFace.SOUTH, BlockFace.WEST, BlockFace.NORTH, BlockFace.EAST};

	public CmdClaimLine() {

		// Aliases
		this.aliases.add("claimline");
		this.aliases.add("cl");

		// Args
		this.optionalArgs.put("amount", "1");
		this.optionalArgs.put("direction", "facing");
		this.optionalArgs.put("faction", "you");

		this.requirements = new CommandRequirements.Builder(Permission.CLAIM_LINE)
				.playerOnly()
				.build();
	}

	@Override
	public void perform(CommandContext context) {
		// Args
		Integer amount = context.argAsInt(0, 1); // Default to 1

		if(amount > FactionsPlugin.getInstance().conf().factions().claims().getLineClaimLimit()) {
			context.msg(Localization.COMMAND_CLAIMLINE_ABOVEMAX, FactionsPlugin.getInstance().conf().factions().claims().getLineClaimLimit());
			return;
		}

		String direction = context.argAsString(1);
		BlockFace blockFace;

		if(direction == null) {
			blockFace = axis[Math.round(context.player.getLocation().getYaw() / 90f) & 0x3];
		} else if(direction.equalsIgnoreCase("north")) {
			blockFace = BlockFace.NORTH;
		} else if(direction.equalsIgnoreCase("east")) {
			blockFace = BlockFace.EAST;
		} else if(direction.equalsIgnoreCase("south")) {
			blockFace = BlockFace.SOUTH;
		} else if(direction.equalsIgnoreCase("west")) {
			blockFace = BlockFace.WEST;
		} else {
			context.fPlayer.msg(Localization.COMMAND_CLAIMLINE_NOTVALID, direction);
			return;
		}

		final IFaction forFaction = context.argAsFaction(2, context.faction);
		Location location = context.player.getLocation();

		// TODO: make this a task like claiming a radius?
		for(int i = 0; i < amount; i++) {
			context.fPlayer.attemptClaim(forFaction, location, true);
			location = location.add(blockFace.getModX() * 16, 0, blockFace.getModZ() * 16);
		}
	}

	@Override
	public Localization getUsageTranslation() {
		return Localization.COMMAND_CLAIMLINE_DESCRIPTION;
	}
}
