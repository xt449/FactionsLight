package com.massivecraft.factions.struct;

import com.massivecraft.factions.FactionsPlugin;
import org.bukkit.command.CommandSender;

public enum Permission {
	MANAGE_SAFE_ZONE("managesafezone"),
	MANAGE_WAR_ZONE("managewarzone"),
	OWNERSHIP_BYPASS("ownershipbypass"),
	ADMIN("admin"),
	ADMIN_ANY("admin.any"),
	AHOME("ahome"),
	ANNOUNCE("announce"),
	AUTOCLAIM("autoclaim"),
	AUTO_LEAVE_BYPASS("autoleavebypass"),
	BAN("ban"),
	BYPASS("bypass"),
	CHAT("chat"),
	CHATSPY("chatspy"),
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
	DELHOME("delhome"),
	DESCRIPTION("description"),
	DISBAND("disband"),
	DISBAND_ANY("disband.any"),
	DTR("dtr"),
	DTR_ANY("dtr.any"),
	HELP("help"),
	HOME("home"),
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
	MODIFY_DTR("modifydtr"),
	MODIFY_POWER("modifypower"),
	MONITOR_LOGINS("monitorlogins"),
	NO_BOOM("noboom"),
	OPEN("open"),
	OWNER("owner"),
	OWNERLIST("ownerlist"),
	SET_PEACEFUL("setpeaceful"),
	SET_PERMANENT("setpermanent"),
	SET_PERMANENTPOWER("setpermanentpower"),
	SHOW_INVITES("showinvites"),
	PERMISSIONS("permissions"),
	POWERBOOST("powerboost"),
	POWER("power"),
	POWER_ANY("power.any"),
	PROMOTE("promote"),
	RELATION("relation"),
	RELOAD("reload"),
	SAVE("save"),
	SETHOME("sethome"),
	SETHOME_ANY("sethome.any"),
	SHOW("show"),
	SHOW_BYPASS_EXEMPT("show.bypassexempt"),
	STATUS("status"),
	STUCK("stuck"),
	TAG("tag"),
	TITLE("title"),
	TITLE_COLOR("title.color"),
	TOGGLE_ALLIANCE_CHAT("togglealliancechat"),
	UNCLAIM("unclaim"),
	UNCLAIM_ALL("unclaimall"),
	VERSION("version"),
	SCOREBOARD("scoreboard"),
	SEECHUNK("seechunk"),
	SETWARP("setwarp"),
	TOP("top"),
	WARP("warp"),
	UPDATES("updates"),
	DEBUG("debug");

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
