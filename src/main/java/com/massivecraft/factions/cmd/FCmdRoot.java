package com.massivecraft.factions.cmd;

import com.massivecraft.factions.FactionsPlugin;
import com.massivecraft.factions.cmd.claim.*;
import com.massivecraft.factions.cmd.relations.CmdRelationAlly;
import com.massivecraft.factions.cmd.relations.CmdRelationEnemy;
import com.massivecraft.factions.cmd.relations.CmdRelationNeutral;
import com.massivecraft.factions.cmd.relations.CmdRelationTruce;
import com.massivecraft.factions.cmd.role.CmdDemote;
import com.massivecraft.factions.cmd.role.CmdPromote;
import com.massivecraft.factions.util.TL;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

public class FCmdRoot extends FCommand implements CommandExecutor {

	private static FCmdRoot cmdBase;

	public static FCmdRoot getInstance() {
		return cmdBase;
	}

	public final CmdAdmin cmdAdmin = new CmdAdmin();
	public final CmdAutoClaim cmdAutoClaim = new CmdAutoClaim();
	public final CmdBypass cmdBypass = new CmdBypass();
	public final CmdClaim cmdClaim = new CmdClaim();
	public final CmdCoords cmdCoords = new CmdCoords();
	public final CmdCreate cmdCreate = new CmdCreate();
	public final CmdDeinvite cmdDeinvite = new CmdDeinvite();
	public final CmdDescription cmdDescription = new CmdDescription();
	public final CmdDisband cmdDisband = new CmdDisband();
	public final CmdHelp cmdHelp = new CmdHelp();
	public final CmdInvite cmdInvite = new CmdInvite();
	public final CmdJoin cmdJoin = new CmdJoin();
	public final CmdKick cmdKick = new CmdKick();
	public final CmdLeave cmdLeave = new CmdLeave();
	public final CmdList cmdList = new CmdList();
	public final CmdLock cmdLock = new CmdLock();
	public final CmdMap cmdMap = new CmdMap();
	public final CmdMod cmdMod = new CmdMod();
	public final CmdOpen cmdOpen = new CmdOpen();
	public final CmdPermanent cmdPermanent = new CmdPermanent();
	//	public final CmdPermanentPower cmdPermanentPower = new CmdPermanentPower();
//	public final CmdPowerBoost cmdPowerBoost = new CmdPowerBoost();
//	public final CmdPower cmdPower = new CmdPower();
//	public final CmdDTR cmdDTR = new CmdDTR();
	public final CmdRelationAlly cmdRelationAlly = new CmdRelationAlly();
	public final CmdRelationEnemy cmdRelationEnemy = new CmdRelationEnemy();
	public final CmdRelationNeutral cmdRelationNeutral = new CmdRelationNeutral();
	public final CmdRelationTruce cmdRelationTruce = new CmdRelationTruce();
	public final CmdReload cmdReload = new CmdReload();
	public final CmdSaveAll cmdSaveAll = new CmdSaveAll();
	public final CmdShow cmdShow = new CmdShow();
	public final CmdStatus cmdStatus = new CmdStatus();
	public final CmdStuck cmdStuck = new CmdStuck();
	public final CmdRename cmdRename = new CmdRename();
	public final CmdTitle cmdTitle = new CmdTitle();
	public final CmdUnclaim cmdUnclaim = new CmdUnclaim();
	public final CmdUnclaimall cmdUnclaimall = new CmdUnclaimall();
	public final CmdVersion cmdVersion = new CmdVersion();
	public final CmdSB cmdSB = new CmdSB();
	public final CmdShowInvites cmdShowInvites = new CmdShowInvites();
	//	public final CmdModifyPower cmdModifyPower = new CmdModifyPower();
	public final CmdClaimLine cmdClaimLine = new CmdClaimLine();
	public final CmdClaimFill cmdClaimFill = new CmdClaimFill();
	public final CmdTop cmdTop = new CmdTop();
	public final CmdPerm cmdPerm = new CmdPerm();
	public final CmdPromote cmdPromote = new CmdPromote();
	public final CmdDemote cmdDemote = new CmdDemote();
	public final CmdSetDefaultRole cmdSetDefaultRole = new CmdSetDefaultRole();
	public final CmdClaimAt cmdClaimAt = new CmdClaimAt();
	public final CmdBan cmdban = new CmdBan();
	public final CmdUnban cmdUnban = new CmdUnban();
	public final CmdBanlist cmdbanlist = new CmdBanlist();
	public final CmdColeader cmdColeader = new CmdColeader();
	public final CmdListClaims cmdListClaims = new CmdListClaims();

	public FCmdRoot() {
		super();

		cmdBase = this;

		this.aliases.add("f");
		this.aliases.removeAll(Collections.<String>singletonList(null));  // remove any nulls from extra commas

		this.setHelpShort("The faction base command");
		this.helpLong.add(ChatColor.YELLOW + "This command contains all faction stuff.");

		this.addSubCommand(this.cmdAdmin);
		this.addSubCommand(this.cmdAutoClaim);
		this.addSubCommand(this.cmdBypass);
		this.addSubCommand(this.cmdClaim);
		this.addSubCommand(this.cmdCoords);
		this.addSubCommand(this.cmdCreate);
		this.addSubCommand(this.cmdDeinvite);
		this.addSubCommand(this.cmdDescription);
		this.addSubCommand(this.cmdDisband);
		this.addSubCommand(this.cmdHelp);
		this.addSubCommand(this.cmdInvite);
		this.addSubCommand(this.cmdJoin);
		this.addSubCommand(this.cmdKick);
		this.addSubCommand(this.cmdLeave);
		this.addSubCommand(this.cmdList);
		this.addSubCommand(this.cmdLock);
		this.addSubCommand(this.cmdMap);
		this.addSubCommand(this.cmdMod);
		this.addSubCommand(this.cmdOpen);
		this.addSubCommand(this.cmdPermanent);
		this.addSubCommand(this.cmdRelationAlly);
		this.addSubCommand(this.cmdRelationEnemy);
		this.addSubCommand(this.cmdRelationNeutral);
		this.addSubCommand(this.cmdRelationTruce);
		this.addSubCommand(this.cmdReload);
		this.addSubCommand(this.cmdSaveAll);
		this.addSubCommand(this.cmdShow);
		this.addSubCommand(this.cmdStatus);
		this.addSubCommand(this.cmdStuck);
		this.addSubCommand(this.cmdRename);
		this.addSubCommand(this.cmdTitle);
		this.addSubCommand(this.cmdUnclaim);
		this.addSubCommand(this.cmdUnclaimall);
		this.addSubCommand(this.cmdVersion);
		this.addSubCommand(this.cmdSB);
		this.addSubCommand(this.cmdShowInvites);
		this.addSubCommand(this.cmdClaimLine);
		this.addSubCommand(this.cmdClaimFill);
		this.addSubCommand(this.cmdPerm);
		this.addSubCommand(this.cmdPromote);
		this.addSubCommand(this.cmdDemote);
		this.addSubCommand(this.cmdSetDefaultRole);
		this.addSubCommand(this.cmdClaimAt);
		this.addSubCommand(this.cmdban);
		this.addSubCommand(this.cmdUnban);
		this.addSubCommand(this.cmdbanlist);
		this.addSubCommand(this.cmdColeader);
		this.addSubCommand(this.cmdListClaims);
//		if(FactionsPlugin.getInstance().getLandRaidControl() instanceof PowerControl) {
//			FactionsPlugin.getInstance().getLogger().info("Using POWER for land/raid control. Enabling power commands.");
//			this.addSubCommand(this.cmdPermanentPower);
//			this.addSubCommand(this.cmdPower);
//			this.addSubCommand(this.cmdPowerBoost);
//			this.addSubCommand(this.cmdModifyPower);
//		} else if(FactionsPlugin.getInstance().getLandRaidControl() instanceof DTRControl) {
//			FactionsPlugin.getInstance().getLogger().info("Using DTR for land/raid control. Enabling DTR commands.");
//			this.addSubCommand(this.cmdDTR);
//		}
	}

	public void done() {
		if(Bukkit.getServer().getPluginManager().isPluginEnabled("FactionsTop")) {
			FactionsPlugin.getInstance().getLogger().info("Found FactionsTop plugin. Disabling our own /f top command.");
		} else {
			this.addSubCommand(this.cmdTop);
		}
	}

	@Override
	public void perform(CommandContext context) {
		context.commandChain.add(this);
		this.cmdHelp.execute(context);
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		this.execute(new CommandContext(sender, new ArrayList<>(Arrays.asList(args)), label));
		return true;
	}

	@Override
	public void addSubCommand(FCommand subCommand) {
		super.addSubCommand(subCommand);
	}

	@Override
	public TL getUsageTranslation() {
		return TL.GENERIC_PLACEHOLDER;
	}

}
