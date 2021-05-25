package com.massivecraft.factions.integration;

import com.massivecraft.factions.IFactionPlayer;
import com.massivecraft.factions.IFaction;
import com.massivecraft.factions.FactionsPlugin;
import com.massivecraft.factions.IEconomyParticipator;
import com.massivecraft.factions.perms.Role;
import com.massivecraft.factions.struct.Permission;
import com.massivecraft.factions.util.RelationUtil;
import com.massivecraft.factions.util.TL;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.RegisteredServiceProvider;

import java.text.DecimalFormat;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.logging.Level;

public class Econ {

	private static Economy econ = null;

	public static void setup() {
		if(isSetup()) {
			return;
		}

		String integrationFail = "Economy integration is " + (FactionsPlugin.getInstance().conf().economy().isEnabled() ? "enabled, but" : "disabled, and") + " the plugin \"Vault\" ";

		if(Bukkit.getServer().getPluginManager().getPlugin("Vault") == null) {
			FactionsPlugin.getInstance().getLogger().info(integrationFail + "is not installed.");
			return;
		}

		RegisteredServiceProvider<Economy> rsp = Bukkit.getServer().getServicesManager().getRegistration(Economy.class);
		if(rsp == null) {
			FactionsPlugin.getInstance().getLogger().info(integrationFail + "is not hooked into an economy plugin.");
			return;
		}
		econ = rsp.getProvider();

		FactionsPlugin.getInstance().getLogger().info("Found economy plugin through Vault: " + econ.getName());

		if(!FactionsPlugin.getInstance().conf().economy().isEnabled()) {
			FactionsPlugin.getInstance().getLogger().info("NOTE: Economy is disabled. You can enable it in config/main.conf");
		}

		//P.getInstance().cmdBase.cmdHelp.updateHelp();
	}

	public static boolean shouldBeUsed() {
		return FactionsPlugin.getInstance().conf().economy().isEnabled() && econ != null && econ.isEnabled();
	}

	public static boolean isSetup() {
		return econ != null;
	}

	public static Economy getEcon() {
		return econ;
	}

	private static String getWorld(OfflinePlayer op) {
		return (op instanceof Player) ? ((Player) op).getWorld().getName() : FactionsPlugin.getInstance().conf().economy().getDefaultWorld();
	}

	public static void modifyUniverseMoney(double delta) {
		if(!shouldBeUsed()) {
			return;
		}

		if(FactionsPlugin.getInstance().conf().economy().getUniverseAccount() == null) {
			return;
		}
		if(FactionsPlugin.getInstance().conf().economy().getUniverseAccount().length() == 0) {
			return;
		}
		if(!hasAccount(FactionsPlugin.getInstance().conf().economy().getUniverseAccount())) {
			return;
		}

		modifyBalance(FactionsPlugin.getInstance().conf().economy().getUniverseAccount(), delta);
	}

	public static void sendBalanceInfo(IFactionPlayer to, IEconomyParticipator about) {
		if(!shouldBeUsed()) {
			FactionsPlugin.getInstance().log(Level.WARNING, "Vault does not appear to be hooked into an economy plugin.");
			return;
		}
		to.msg(TL.ECON_BALANCE, about.describeTo(to, true), Econ.moneyString(getBalance(about)));
	}

	public static void sendBalanceInfo(CommandSender to, IFaction about) {
		if(!shouldBeUsed()) {
			FactionsPlugin.getInstance().log(Level.WARNING, "Vault does not appear to be hooked into an economy plugin.");
			return;
		}
		to.sendMessage(ChatColor.stripColor(String.format(TL.ECON_BALANCE.toString(), about.getTag(), Econ.moneyString(getBalance(about)))));
	}

	public static boolean canIControlYou(IEconomyParticipator i, IEconomyParticipator you) {
		IFaction fI = RelationUtil.getFaction(i);
		IFaction fYou = RelationUtil.getFaction(you);

		// This is a system invoker. Accept it.
		if(fI == null) {
			return true;
		}

		// Bypassing players can do any kind of transaction
		if(i instanceof IFactionPlayer && ((IFactionPlayer) i).isAdminBypassing()) {
			return true;
		}

		// Players with the any withdraw can do.
		if(i instanceof IFactionPlayer && Permission.MONEY_WITHDRAW_ANY.has(((IFactionPlayer) i).getPlayer())) {
			return true;
		}

		// You can deposit to anywhere you feel like. It's your loss if you can't withdraw it again.
		if(i == you) {
			return true;
		}

		// A faction can always transfer away the money of it's members and its own money...
		// This will however probably never happen as a faction does not have free will.
		// Ohh by the way... Yes it could. For daily rent to the faction.
		if(i == fI && fI == fYou) {
			return true;
		}

		// Factions can be controlled by members that are moderators... or any member if any member can withdraw.
		if(you instanceof IFaction && fI == fYou && (FactionsPlugin.getInstance().conf().economy().isBankMembersCanWithdraw() || ((IFactionPlayer) i).getRole().value >= Role.MODERATOR.value)) {
			return true;
		}

		// Otherwise you may not! ;,,;
		i.msg(TL.ECON_NOPERM, i.describeTo(i, true), you.describeTo(i));
		return false;
	}

	public static boolean transferMoney(IEconomyParticipator invoker, IEconomyParticipator from, IEconomyParticipator to, double amount) {
		return transferMoney(invoker, from, to, amount, true);
	}

	public static boolean transferMoney(IEconomyParticipator invoker, IEconomyParticipator from, IEconomyParticipator to, double amount, boolean notify) {
		if(!shouldBeUsed()) {
			invoker.msg(TL.ECON_DISABLED);
			return false;
		}

		// The amount must be positive.
		// If the amount is negative we must flip and multiply amount with -1.
		if(amount < 0) {
			amount *= -1;
			IEconomyParticipator temp = from;
			from = to;
			to = temp;
		}

		// Check the rights
		if(!canIControlYou(invoker, from)) {
			return false;
		}

		OfflinePlayer fromAcc = checkStatus(from.getOfflinePlayer());
		OfflinePlayer toAcc = checkStatus(to.getOfflinePlayer());

		// Is there enough money for the transaction to happen?
		if(!has(fromAcc, amount)) {
			// There was not enough money to pay
			if(invoker != null && notify) {
				invoker.msg(TL.ECON_CANTAFFORD_TRANSFER, from.describeTo(invoker, true), moneyString(amount), to.describeTo(invoker));
			}

			return false;
		}

		// Check if the new balance is over Essential's money cap.
		if(Essentials.isOverBalCap(to, getBalance(toAcc) + amount)) {
			invoker.msg(TL.ECON_OVER_BAL_CAP, amount);
			return false;
		}

		// Transfer money

		if(withdraw(fromAcc, amount)) {
			if(deposit(toAcc, amount)) {
				if(notify) {
					sendTransferInfo(invoker, from, to, amount);
				}
				return true;
			} else {
				// transaction failed, refund account
				deposit(fromAcc, amount);
			}
		}

		// if we get here something with the transaction failed
		if(notify) {
			invoker.msg(TL.ECON_TRANSFER_UNABLE, moneyString(amount), to.describeTo(invoker), from.describeTo(invoker, true));
		}

		return false;
	}

	public static Set<IFactionPlayer> getFplayers(IEconomyParticipator ep) {
		Set<IFactionPlayer> fplayers = new HashSet<>();

		if(ep != null) {
			if(ep instanceof IFactionPlayer) {
				fplayers.add((IFactionPlayer) ep);
			} else if(ep instanceof IFaction) {
				fplayers.addAll(((IFaction) ep).getFPlayers());
			}
		}

		return fplayers;
	}

	public static void sendTransferInfo(IEconomyParticipator invoker, IEconomyParticipator from, IEconomyParticipator to, double amount) {
		Set<IFactionPlayer> recipients = new HashSet<>();
		recipients.addAll(getFplayers(invoker));
		recipients.addAll(getFplayers(from));
		recipients.addAll(getFplayers(to));

		if(invoker == null) {
			for(IFactionPlayer recipient : recipients) {
				recipient.msg(TL.ECON_TRANSFER_NOINVOKER, moneyString(amount), from.describeTo(recipient), to.describeTo(recipient));
			}
		} else if(invoker == from) {
			for(IFactionPlayer recipient : recipients) {
				recipient.msg(TL.ECON_TRANSFER_GAVE, from.describeTo(recipient, true), moneyString(amount), to.describeTo(recipient));
			}
		} else if(invoker == to) {
			for(IFactionPlayer recipient : recipients) {
				recipient.msg(TL.ECON_TRANSFER_TOOK, to.describeTo(recipient, true), moneyString(amount), from.describeTo(recipient));
			}
		} else {
			for(IFactionPlayer recipient : recipients) {
				recipient.msg(TL.ECON_TRANSFER_TRANSFER, invoker.describeTo(recipient, true), moneyString(amount), from.describeTo(recipient), to.describeTo(recipient));
			}
		}
	}

	public static boolean hasAtLeast(IEconomyParticipator ep, double delta, String toDoThis) {
		if(!shouldBeUsed()) {
			return true;
		}

		boolean affordable = false;
		double currentBalance = getBalance(ep);

		if(currentBalance >= delta) {
			affordable = true;
		}

		if(!affordable) {
			if(toDoThis != null && !toDoThis.isEmpty()) {
				ep.msg(TL.ECON_CANTAFFORD_AMOUNT, ep.describeTo(ep, true), moneyString(delta), toDoThis);
			}
			return false;
		}
		return true;
	}

	public static boolean modifyMoney(IEconomyParticipator ep, double delta, String toDoThis, String forDoingThis) {
		if(!shouldBeUsed()) {
			return false;
		}

		if(delta == 0) {
			// no money actually transferred?
//			ep.msg("<h>%s<i> didn't have to pay anything %s.", You, forDoingThis);  // might be for gains, might be for losses
			return true;
		}

		OfflinePlayer acc = checkStatus(ep.getOfflinePlayer());

		String You = ep.describeTo(ep, true);

		if(delta > 0) {
			// The player should gain money
			// The account might not have enough space
			if(deposit(acc, delta)) {
				modifyUniverseMoney(-delta);
				if(forDoingThis != null && !forDoingThis.isEmpty()) {
					ep.msg(TL.ECON_GAIN_SUCCESS, You, moneyString(delta), forDoingThis);
				}
				return true;
			} else {
				// transfer to account failed
				if(forDoingThis != null && !forDoingThis.isEmpty()) {
					ep.msg(TL.ECON_GAIN_FAILURE, You, moneyString(delta), forDoingThis);
				}
				return false;
			}
		} else {
			// The player should loose money
			// The player might not have enough.

			if(has(acc, -delta) && withdraw(acc, -delta)) {
				// There is enough money to pay
				modifyUniverseMoney(-delta);
				if(forDoingThis != null && !forDoingThis.isEmpty()) {
					ep.msg(TL.ECON_LOST_SUCCESS, You, moneyString(-delta), forDoingThis);
				}
				return true;
			} else {
				// There was not enough money to pay
				if(toDoThis != null && !toDoThis.isEmpty()) {
					ep.msg(TL.ECON_LOST_FAILURE, You, moneyString(-delta), toDoThis);
				}
				return false;
			}
		}
	}

	public static String moneyString(double amount) {
		return format.format(amount);
	}

	// calculate the cost for claiming land
	public static double calculateClaimCost(int ownedLand, boolean takingFromAnotherFaction) {
		if(!shouldBeUsed()) {
			return 0d;
		}

		// basic claim cost, plus land inflation cost, minus the potential bonus given for claiming from another faction
		return FactionsPlugin.getInstance().conf().economy().getCostClaimWilderness() + (FactionsPlugin.getInstance().conf().economy().getCostClaimWilderness() * FactionsPlugin.getInstance().conf().economy().getClaimAdditionalMultiplier() * ownedLand) - (takingFromAnotherFaction ? FactionsPlugin.getInstance().conf().economy().getCostClaimFromFactionBonus() : 0);
	}

	// calculate refund amount for unclaiming land
	public static double calculateClaimRefund(int ownedLand) {
		return calculateClaimCost(ownedLand - 1, false) * FactionsPlugin.getInstance().conf().economy().getClaimRefundMultiplier();
	}

	// calculate value of all owned land
	public static double calculateTotalLandValue(int ownedLand) {
		double amount = 0;
		for(int x = 0; x < ownedLand; x++) {
			amount += calculateClaimCost(x, false);
		}
		return amount;
	}

	// calculate refund amount for all owned land
	public static double calculateTotalLandRefund(int ownedLand) {
		return calculateTotalLandValue(ownedLand) * FactionsPlugin.getInstance().conf().economy().getClaimRefundMultiplier();
	}


	// -------------------------------------------- //
	// Standard account management methods
	// -------------------------------------------- //

	private static OfflinePlayer getOfflinePlayerForName(String name) {
		try {
			return Bukkit.getOfflinePlayer(UUID.fromString(name));
		} catch(IllegalArgumentException ex) {
			return Bukkit.getOfflinePlayer(name);
		}
	}

	@Deprecated
	public static boolean hasAccount(String name) {
		return hasAccount(getOfflinePlayerForName(name));
	}

	public static boolean hasAccount(IEconomyParticipator ep) {
		return hasAccount(ep.getOfflinePlayer());
	}

	private static boolean hasAccount(OfflinePlayer op) {
		return econ.hasAccount(op, getWorld(op));
	}

	@Deprecated
	public static double getBalance(String account) {
		return getBalance(getOfflinePlayerForName(account));
	}

	public static double getBalance(IEconomyParticipator ep) {
		return getBalance(ep.getOfflinePlayer());
	}

	private static double getBalance(OfflinePlayer op) {
		return econ.getBalance(checkStatus(op), getWorld(op));
	}

	public static boolean has(IEconomyParticipator ep, double amount) {
		return has(ep.getOfflinePlayer(), amount);
	}

	private static boolean has(OfflinePlayer op, double amount) {
		return econ.has(checkStatus(op), getWorld(op), amount);
	}

	private static final DecimalFormat format = new DecimalFormat(TL.ECON_FORMAT.toString());

	@Deprecated
	public static String getFriendlyBalance(UUID uuid) {
		OfflinePlayer offline = Bukkit.getOfflinePlayer(uuid);
		if(offline.getName() == null) {
			return "0";
		}
		return format.format(getBalance(offline));
	}

	public static String getFriendlyBalance(IFactionPlayer player) {
		OfflinePlayer p;
		if((p = player.getPlayer()) == null) {
			return "0";
		}
		return format.format(getBalance(p));
	}

	@Deprecated
	public static boolean setBalance(String account, double amount) {
		return setBalance(getOfflinePlayerForName(account), amount);
	}

	public static boolean setBalance(IEconomyParticipator ep, double amount) {
		return setBalance(ep.getOfflinePlayer(), amount);
	}

	private static boolean setBalance(OfflinePlayer op, double amount) {
		double current = getBalance(op); // Already checks status
		if(current > amount) {
			return econ.withdrawPlayer(op, getWorld(op), current - amount).transactionSuccess();
		} else {
			return econ.depositPlayer(op, getWorld(op), amount - current).transactionSuccess();
		}
	}

	@Deprecated
	public static boolean modifyBalance(String account, double amount) {
		return modifyBalance(getOfflinePlayerForName(account), amount);
	}

	public static boolean modifyBalance(IEconomyParticipator ep, double amount) {
		return modifyBalance(ep.getOfflinePlayer(), amount);
	}

	private static boolean modifyBalance(OfflinePlayer op, double amount) {
		if(amount < 0) {
			return econ.withdrawPlayer(checkStatus(op), getWorld(op), -amount).transactionSuccess();
		} else {
			return econ.depositPlayer(checkStatus(op), getWorld(op), amount).transactionSuccess();
		}
	}

	@Deprecated
	public static boolean deposit(String account, double amount) {
		return deposit(getOfflinePlayerForName(account), amount);
	}

	public static boolean deposit(IEconomyParticipator ep, double amount) {
		return deposit(ep.getOfflinePlayer(), amount);
	}

	private static boolean deposit(OfflinePlayer op, double amount) {
		return econ.depositPlayer(checkStatus(op), getWorld(op), amount).transactionSuccess();
	}

	@Deprecated
	public static boolean withdraw(String account, double amount) {
		return withdraw(getOfflinePlayerForName(account), amount);
	}

	public static boolean withdraw(IEconomyParticipator ep, double amount) {
		return withdraw(ep.getOfflinePlayer(), amount);
	}

	private static boolean withdraw(OfflinePlayer op, double amount) {
		return econ.withdrawPlayer(checkStatus(op), getWorld(op), amount).transactionSuccess();
	}

	@Deprecated
	public static void createAccount(String name) {
		createAccount(getOfflinePlayerForName(name));
	}

	public static void createAccount(IEconomyParticipator ep) {
		createAccount(ep.getOfflinePlayer());
	}

	private static void createAccount(OfflinePlayer op) {
		if(!econ.createPlayerAccount(op, getWorld(op))) {
			FactionsPlugin.getInstance().getLogger().warning("FAILED TO CREATE ECONOMY ACCOUNT " + op.getName() + '/' + op.getUniqueId());
		}
	}

	public static OfflinePlayer checkStatus(OfflinePlayer op) {
		if(op.getName() == null || !op.getName().startsWith("faction-")) {
			return op;
		}
		// We need to override the default money given to players.
		String world = getWorld(op);
		if(!hasAccount(op)) {
			createAccount(op);
			setBalance(op, 0);
		}
		return op;
	}

	@Deprecated
	public static boolean isUUID(String uuid) {
		try {
			UUID.fromString(uuid);
			return true;
		} catch(IllegalArgumentException ex) {
			return false;
		}
	}
}
