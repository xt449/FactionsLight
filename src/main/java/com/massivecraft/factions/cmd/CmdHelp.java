package com.massivecraft.factions.cmd;

import com.massivecraft.factions.Permission;
import com.massivecraft.factions.util.TL;
import com.massivecraft.factions.util.TextUtil;
import org.bukkit.ChatColor;

import java.util.ArrayList;


public class CmdHelp extends FCommand {

	public CmdHelp() {
		super();
		this.aliases.add("help");
		this.aliases.add("h");

		//this.requiredArgs.add("");
		this.optionalArgs.put("page", "1");

		this.requirements = new CommandRequirements.Builder(Permission.HELP).noDisableOnLock().build();
	}

	@Override
	public void perform(CommandContext context) {
		// TODO - redo help
	}

	//----------------------------------------------//
	// Build the help pages
	//----------------------------------------------//

	public ArrayList<ArrayList<String>> helpPages;

	public void updateHelp(CommandContext context) {
		helpPages = new ArrayList<>();
		ArrayList<String> pageLines;

		pageLines = new ArrayList<>();
		pageLines.add(FCmdRoot.getInstance().cmdHelp.getUsageTemplate(context, true));
		pageLines.add(FCmdRoot.getInstance().cmdList.getUsageTemplate(context, true));
		pageLines.add(FCmdRoot.getInstance().cmdShow.getUsageTemplate(context, true));
//		pageLines.add(FCmdRoot.getInstance().cmdPower.getUsageTemplate(context, true));
		pageLines.add(FCmdRoot.getInstance().cmdJoin.getUsageTemplate(context, true));
		pageLines.add(FCmdRoot.getInstance().cmdLeave.getUsageTemplate(context, true));
		pageLines.add(TextUtil.parse(TL.COMMAND_HELP_NEXTCREATE.toString()));
		helpPages.add(pageLines);

		pageLines = new ArrayList<>();
		pageLines.add(FCmdRoot.getInstance().cmdCreate.getUsageTemplate(context, true));
		pageLines.add(FCmdRoot.getInstance().cmdDescription.getUsageTemplate(context, true));
		pageLines.add(FCmdRoot.getInstance().cmdRename.getUsageTemplate(context, true));
		pageLines.add(TextUtil.parse(TL.COMMAND_HELP_INVITATIONS.toString()));
		pageLines.add(FCmdRoot.getInstance().cmdOpen.getUsageTemplate(context, true));
		pageLines.add(FCmdRoot.getInstance().cmdInvite.getUsageTemplate(context, true));
		pageLines.add(FCmdRoot.getInstance().cmdDeinvite.getUsageTemplate(context, true));
		helpPages.add(pageLines);

		pageLines = new ArrayList<>();
		pageLines.add(FCmdRoot.getInstance().cmdClaim.getUsageTemplate(context, true));
		pageLines.add(FCmdRoot.getInstance().cmdAutoClaim.getUsageTemplate(context, true));
		pageLines.add(FCmdRoot.getInstance().cmdUnclaim.getUsageTemplate(context, true));
		pageLines.add(FCmdRoot.getInstance().cmdUnclaimall.getUsageTemplate(context, true));
		pageLines.add(FCmdRoot.getInstance().cmdKick.getUsageTemplate(context, true));
		pageLines.add(FCmdRoot.getInstance().cmdMod.getUsageTemplate(context, true));
		pageLines.add(FCmdRoot.getInstance().cmdAdmin.getUsageTemplate(context, true));
		pageLines.add(FCmdRoot.getInstance().cmdTitle.getUsageTemplate(context, true));
		pageLines.add(FCmdRoot.getInstance().cmdSB.getUsageTemplate(context, true));
		pageLines.add(FCmdRoot.getInstance().cmdStatus.getUsageTemplate(context, true));
		pageLines.add(TextUtil.parse(TL.COMMAND_HELP_PLAYERTITLES.toString()));
		helpPages.add(pageLines);

		pageLines = new ArrayList<>();
		pageLines.add(FCmdRoot.getInstance().cmdMap.getUsageTemplate(context, true));
		helpPages.add(pageLines);

		pageLines = new ArrayList<>();
		pageLines.add(FCmdRoot.getInstance().cmdDisband.getUsageTemplate(context, true));
		pageLines.add("");
		pageLines.add(FCmdRoot.getInstance().cmdRelationAlly.getUsageTemplate(context, true));
		pageLines.add(FCmdRoot.getInstance().cmdRelationNeutral.getUsageTemplate(context, true));
		pageLines.add(FCmdRoot.getInstance().cmdRelationEnemy.getUsageTemplate(context, true));
		pageLines.add(TextUtil.parse(TL.COMMAND_HELP_RELATIONS_1.toString()));
		pageLines.add(TextUtil.parse(TL.COMMAND_HELP_RELATIONS_2.toString()));
		pageLines.add(TextUtil.parse(TL.COMMAND_HELP_RELATIONS_3.toString()));
		pageLines.add(TextUtil.parse(TL.COMMAND_HELP_RELATIONS_4.toString()));
		helpPages.add(pageLines);

		pageLines = new ArrayList<>();
		pageLines.add(TextUtil.parse(TL.COMMAND_HELP_RELATIONS_5.toString()));
		pageLines.add(TextUtil.parse(TL.COMMAND_HELP_RELATIONS_6.toString()));
		pageLines.add(TextUtil.parse(TL.COMMAND_HELP_RELATIONS_7.toString()));
		pageLines.add(TL.COMMAND_HELP_RELATIONS_8.toString());
		pageLines.add(TextUtil.parse(TL.COMMAND_HELP_RELATIONS_9.toString()));
		helpPages.add(pageLines);

		pageLines = new ArrayList<>();
		pageLines.add(TextUtil.parse(TL.COMMAND_HELP_PERMISSIONS_1.toString()));
		pageLines.add(TextUtil.parse(TL.COMMAND_HELP_PERMISSIONS_2.toString()));
		pageLines.add(TextUtil.parse(TL.COMMAND_HELP_PERMISSIONS_3.toString()));
		pageLines.add(TL.COMMAND_HELP_PERMISSIONS_4.toString());
		pageLines.add(TextUtil.parse(TL.COMMAND_HELP_PERMISSIONS_5.toString()));
		pageLines.add(TextUtil.parse(TL.COMMAND_HELP_PERMISSIONS_6.toString()));
		pageLines.add(TextUtil.parse(TL.COMMAND_HELP_PERMISSIONS_7.toString()));
		pageLines.add(TextUtil.parse(TL.COMMAND_HELP_PERMISSIONS_8.toString()));
		pageLines.add(TextUtil.parse(TL.COMMAND_HELP_PERMISSIONS_9.toString()));
		helpPages.add(pageLines);

		pageLines = new ArrayList<>();
		pageLines.add(TL.COMMAND_HELP_MOAR_1.toString());
		pageLines.add(FCmdRoot.getInstance().cmdBypass.getUsageTemplate(context, true));
		//TODO:TL
		pageLines.add(TextUtil.parse(ChatColor.YELLOW + "Note: " + FCmdRoot.getInstance().cmdUnclaim.getUsageTemplate(context, false) + ChatColor.YELLOW + " works on safe/war zones as well."));
		helpPages.add(pageLines);

		pageLines = new ArrayList<>();
		pageLines.add(TextUtil.parse(TL.COMMAND_HELP_MOAR_2.toString()));
		pageLines.add(FCmdRoot.getInstance().cmdPermanent.getUsageTemplate(context, true));
//		pageLines.add(FCmdRoot.getInstance().cmdPermanentPower.getUsageTemplate(context, true));
//		pageLines.add(FCmdRoot.getInstance().cmdPowerBoost.getUsageTemplate(context, true));
		helpPages.add(pageLines);

		pageLines = new ArrayList<>();
		pageLines.add(TextUtil.parse(TL.COMMAND_HELP_MOAR_3.toString()));
		pageLines.add(FCmdRoot.getInstance().cmdLock.getUsageTemplate(context, true));
		pageLines.add(FCmdRoot.getInstance().cmdReload.getUsageTemplate(context, true));
		pageLines.add(FCmdRoot.getInstance().cmdSaveAll.getUsageTemplate(context, true));
		pageLines.add(FCmdRoot.getInstance().cmdVersion.getUsageTemplate(context, true));
		helpPages.add(pageLines);
	}

	@Override
	public TL getUsageTranslation() {
		return TL.COMMAND_HELP_DESCRIPTION;
	}
}

