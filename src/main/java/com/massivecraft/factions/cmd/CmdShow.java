package com.massivecraft.factions.cmd;

import com.massivecraft.factions.*;
import com.massivecraft.factions.perms.Relation;
import com.massivecraft.factions.tag.FactionTag;
import com.massivecraft.factions.tag.FancyTag;
import com.massivecraft.factions.tag.Tag;
import com.massivecraft.factions.util.MiscUtil;
import com.massivecraft.factions.util.TL;
import com.massivecraft.factions.util.TextUtil;
import mkremins.fanciful.FancyMessage;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class CmdShow extends FCommand {

	public CmdShow() {
		this.aliases.add("show");
		this.aliases.add("info");
		this.aliases.add("who");

		this.optionalArgs.put("faction tag", "yours");

		this.requirements = new CommandRequirements.Builder(Permission.SHOW).noDisableOnLock().build();
	}

	@Override
	public void perform(CommandContext context) {
		Faction faction = context.faction;
		if(context.argIsSet(0)) {
			faction = context.argAsFaction(0);
		}
		if(faction == null || faction.isWilderness()) {
			context.msg(TL.COMMAND_SHOW_NOFACTION_SELF);
			return;
		}

		List<String> show = Collections.singletonList(FactionsPlugin.getInstance().configMain.commands().show().format());

		List<String> messageList = new ArrayList<>();
		for(String raw : show) {
			String parsed = Tag.parsePlain(faction, context.fPlayer, raw); // use relations
			if(parsed == null) {
				continue; // Due to minimal f show.
			}

			if(context.fPlayer != null) {
				parsed = Tag.parsePlaceholders(context.fPlayer.getPlayer(), parsed);
			}

			parsed = parsed.replace("%", ""); // Just in case it got in there before we disallowed it.
			messageList.add(parsed);
		}

//		List<String> messageList = Collections.singletonList(
//				TextUtil.parse(
//						Tag.parsePlaceholders(
//								context.player, Tag.parsePlain(
//										faction, context.fPlayer, FactionsPlugin.getInstance().configMain.commands().show().format()
//								)
//						)
//				)
//		);

		this.sendMessages(messageList, context.sender, faction, context.fPlayer);
	}

	private void sendMessages(List<String> messageList, CommandSender recipient, Faction faction, FPlayer player) {
		FancyTag tag;
		for(String parsed : messageList) {
			if((tag = FancyTag.getMatch(parsed)) != null) {
				if(player != null) {
					List<FancyMessage> fancy = FancyTag.parse(parsed, faction, player);
					if(fancy != null) {
						for(FancyMessage fancyMessage : fancy) {
							fancyMessage.send(recipient);
						}
					}
				} else {
					StringBuilder builder = new StringBuilder();
					builder.append(parsed.replace(tag.toString(), ""));
					switch(tag) {
						case ONLINE_LIST:
							this.onOffLineMessage(builder, recipient, faction, true);
							break;
						case OFFLINE_LIST:
							this.onOffLineMessage(builder, recipient, faction, false);
							break;
						case ALLIES_LIST:
							this.relationMessage(builder, recipient, faction, Relation.ALLY);
							break;
						case ENEMIES_LIST:
							this.relationMessage(builder, recipient, faction, Relation.ENEMY);
							break;
						case TRUCES_LIST:
							this.relationMessage(builder, recipient, faction, Relation.TRUCE);
							break;
						default:
							// NO
					}
				}
			} else {
				recipient.sendMessage(TextUtil.parse(parsed));
			}
		}
	}

	private void onOffLineMessage(StringBuilder builder, CommandSender recipient, Faction faction, boolean online) {
		boolean first = true;
		for(FPlayer p : MiscUtil.rankOrder(faction.getFPlayersWhereOnline(online))) {
			String name = p.getNameAndTitle();
			builder.append(first ? name : ", " + name);
			first = false;
		}
		recipient.sendMessage(TextUtil.parse(builder.toString()));
	}

	private void relationMessage(StringBuilder builder, CommandSender recipient, Faction faction, Relation relation) {
		boolean first = true;
		for(Faction otherFaction : Factions.getInstance().getAllFactions()) {
			if(otherFaction != faction && otherFaction.getRelationTo(faction) == relation) {
				String s = otherFaction.getTag();
				builder.append(first ? s : ", " + s);
				first = false;
			}
		}
		recipient.sendMessage(TextUtil.parse(builder.toString()));
	}

	@Override
	public TL getUsageTranslation() {
		return TL.COMMAND_SHOW_COMMANDDESCRIPTION;
	}

}