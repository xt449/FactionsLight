package com.massivecraft.factions.cmd.money;

import com.massivecraft.factions.FactionsPlugin;
import com.massivecraft.factions.IEconomyParticipator;
import com.massivecraft.factions.cmd.CommandContext;
import com.massivecraft.factions.cmd.CommandRequirements;
import com.massivecraft.factions.integration.Econ;
import com.massivecraft.factions.perms.PermissibleAction;
import com.massivecraft.factions.struct.Permission;
import com.massivecraft.factions.util.Localization;
import org.bukkit.ChatColor;


public class CmdMoneyWithdraw extends MoneyCommand {

	public CmdMoneyWithdraw() {
		this.aliases.add("w");
		this.aliases.add("withdraw");

		this.requiredArgs.add("amount");
		this.optionalArgs.put("faction", "yours");

		this.requirements = new CommandRequirements.Builder(Permission.MONEY_F2P)
				.playerOnly()
				.build();
	}

	@Override
	public void perform(CommandContext context) {
		double amount = context.argAsDouble(0, 0d);
		IEconomyParticipator faction = context.argAsFaction(1, context.faction);
		if(faction == null) {
			return;
		}

		if(!context.faction.hasAccess(context.fPlayer, PermissibleAction.ECONOMY)) {
			context.msg(Localization.GENERIC_NOPERMISSION, "withdraw");
			return;
		}

		boolean success = Econ.transferMoney(context.fPlayer, faction, context.fPlayer, amount);

		if(success && FactionsPlugin.getInstance().conf().logging().isMoneyTransactions()) {
			FactionsPlugin.getInstance().log(ChatColor.stripColor(FactionsPlugin.getInstance().txt().parse(Localization.COMMAND_MONEYWITHDRAW_WITHDRAW.toString(), context.fPlayer.getName(), Econ.moneyString(amount), faction.describeTo(null))));
		}
	}

	@Override
	public Localization getUsageTranslation() {
		return Localization.COMMAND_MONEYWITHDRAW_DESCRIPTION;
	}
}
