package com.massivecraft.factions.util;

import mkremins.fanciful.FancyMessage;
import org.bukkit.ChatColor;
import org.bukkit.Material;

import java.util.ArrayList;
import java.util.List;

public abstract class TextUtil {

	// -------------------------------------------- //
	// Top-level parsing functions.
	// -------------------------------------------- //

	public static String parse(String str, Object... args) {
		return String.format(parse(str), args);
	}

	public static String parse(String str) {
		return parseColor(str);
	}

	// -------------------------------------------- //
	// Fancy parsing
	// -------------------------------------------- //

	public static FancyMessage parseFancy(String prefix) {
		return toFancy(parse(prefix));
	}

	public static FancyMessage toFancy(String first) {
		String text = "";
		FancyMessage message = new FancyMessage(text);
		ChatColor color = null;
		char[] chars = first.toCharArray();

		for(int i = 0; i < chars.length; i++) {
			if(chars[i] == '§') {
				if(color != null) {
					if(color.isColor()) {
						message.then(text).color(color);
					} else {
						message.then(text).style(color);
					}
					text = "";
				}
				color = ChatColor.getByChar(chars[i + 1]);
				i++; // skip color char
			} else {
				text += chars[i];
			}
		}
		if(text.length() > 0) {
			if(color != null) {
				if(color.isColor()) {
					message.then(text).color(color);
				} else {
					message.then(text).style(color);
				}
			} else {
				message.text(text);
			}
		}
		return message;
	}

	public static String parseColor(String string) {
		return string.replaceAll("(&([0-9a-fklmnorxA-FKLMNORX]))", "\u00A7$2").replace("&&", "&");
	}

	public static String upperCaseFirst(String string) {
		return string.substring(0, 1).toUpperCase() + string.substring(1);
	}

	// -------------------------------------------- //
	// Material name tools
	// -------------------------------------------- //

	public static String getMaterialName(Material material) {
		return material.toString().replace('_', ' ').toLowerCase();
	}

	// -------------------------------------------- //
	// Paging and chrome-tools like titleize
	// -------------------------------------------- //

	private final static String titleizeLine = "____________________________________________________"; // 52 underscores
	private final static int titleizeBalance = -1;

	public static String titleize(String str) {
		String center = ".[ " + ChatColor.GREEN + str + ChatColor.GOLD + " ].";
		int centerlen = ChatColor.stripColor(center).length();
		int pivot = titleizeLine.length() / 2;
		int eatLeft = (centerlen / 2) - titleizeBalance;
		int eatRight = (centerlen - eatLeft) + titleizeBalance;

		if(eatLeft < pivot) {
			return ChatColor.GOLD + titleizeLine.substring(0, pivot - eatLeft) + center + titleizeLine.substring(pivot + eatRight);
		} else {
			return ChatColor.GOLD + center;
		}
	}

	public static ArrayList<String> getPage(List<String> lines, int pageHumanBased, String title) {
		ArrayList<String> ret = new ArrayList<>();
		int pageZeroBased = pageHumanBased - 1;
		int pageheight = 9;
		int pagecount = (lines.size() / pageheight) + 1;

		ret.add(titleize(title + " " + pageHumanBased + "/" + pagecount));

		if(pageZeroBased < 0 || pageHumanBased > pagecount) {
			ret.add(TL.INVALIDPAGE.format(pagecount));
			return ret;
		}

		int from = pageZeroBased * pageheight;
		int to = from + pageheight;
		if(to > lines.size()) {
			to = lines.size();
		}

		ret.addAll(lines.subList(from, to));

		return ret;
	}
}
