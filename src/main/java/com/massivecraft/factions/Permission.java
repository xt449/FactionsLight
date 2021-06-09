package com.massivecraft.factions;

import org.bukkit.command.CommandSender;

public enum Permission {
	ADMIN("admin"),
	ADMIN_ANY("admin.any"),
	AUTOCLAIM("autoclaim"),
	BAN("ban"),
	BYPASS("bypass"),
	CLAIM("claim"),
	CLAIMAT("claimat"),
	CLAIM_FILL("claim.fill"),
	CLAIM_LINE("claim.line"),
	CLAIM_RADIUS("claim.radius"),
	COLEADER("coleader"),
	COLEADER_ANY("coleader.any"),
	COORDS("coords"),
	CREATE("create"),
	DEFAULTRANK("defaultrank"),
	DEINVITE("deinvite"),
	DESCRIPTION("description"),
	DISBAND("disband"),
	DISBAND_ANY("disband.any"),
	HELP("help"),
	INVITE("invite"),
	JOIN("join"),
	JOIN_ANY("join.any"),
	JOIN_OTHERS("join.others"),
	KICK("kick"),
	KICK_ANY("kick.any"),
	LEAVE("leave"),
	LIST("list"),
	LISTCLAIMS("listclaims"),
	LISTCLAIMS_OTHER("listclaims.other"),
	LOCK("lock"),
	MAP("map"),
	MOD("mod"),
	MOD_ANY("mod.any"),
	OPEN("open"),
	SET_PERMANENT("setpermanent"),
	SHOW_INVITES("showinvites"),
	PERMISSIONS("permissions"),
	PROMOTE("promote"),
	RELATION("relation"),
	RELOAD("reload"),
	SAVE("save"),
	SHOW("show"),
	STATUS("status"),
	STUCK("stuck"),
	TAG("tag"),
	TITLE("title"),
	TITLE_COLOR("title.color"),
	UNCLAIM("unclaim"),
	UNCLAIM_ALL("unclaimall"),
	VERSION("version"),
	TOP("top");

	public final String node;

	Permission(final String node) {
		this.node = "factions." + node;
	}

	@Override
	public String toString() {
		return this.node;
	}

	public boolean has(CommandSender sender, boolean informSenderIfNot) {
		return FactionsPlugin.getInstance().getPermUtil().has(sender, this.node, informSenderIfNot);
	}

	public boolean has(CommandSender sender) {
		return has(sender, false);
	}
}
