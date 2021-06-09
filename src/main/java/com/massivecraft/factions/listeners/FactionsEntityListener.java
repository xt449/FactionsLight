package com.massivecraft.factions.listeners;

import com.massivecraft.factions.*;
import com.massivecraft.factions.combat.Setting;
import com.massivecraft.factions.configuration.MainConfiguration;
import com.massivecraft.factions.perms.PermissibleAction;
import com.massivecraft.factions.perms.Relation;
import com.massivecraft.factions.util.TL;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityCombustByEntityEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.PotionSplashEvent;
import org.bukkit.event.hanging.HangingBreakByEntityEvent;
import org.bukkit.event.hanging.HangingBreakEvent;
import org.bukkit.event.hanging.HangingPlaceEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.projectiles.ProjectileSource;

import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.UUID;

public class FactionsEntityListener extends AbstractListener {

	public final FactionsPlugin plugin;

	public FactionsEntityListener(FactionsPlugin plugin) {
		this.plugin = plugin;
	}

	/**
	 * Who can I hurt? I can never hurt members or allies. I can always hurt enemies. I can hurt neutrals as long as
	 * they are outside their own territory.
	 */
	@EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
	public void onEntityDamage(EntityDamageEvent event) {
		if(!plugin.configMain.restrictWorlds().isEnabled(event.getEntity().getWorld())) {
			return;
		}

		if(event instanceof EntityDamageByEntityEvent) {
			EntityDamageByEntityEvent sub = (EntityDamageByEntityEvent) event;
			if(!this.canDamagerHurtDamagee(sub, true)) {
				event.setCancelled(true);
			}
		} else if(event.getCause() == EntityDamageEvent.DamageCause.FALL && event.getEntity() instanceof Player) {
			Player player = (Player) event.getEntity();
			FPlayer fPlayer = FPlayers.getInstance().getByPlayer(player);
			if(fPlayer != null && !fPlayer.shouldTakeFallDamage()) {
				event.setCancelled(true); // Falling after /f fly
			}
		}
	}

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onEntityDamageMonitor(EntityDamageEvent event) {
		if(!plugin.configMain.restrictWorlds().isEnabled(event.getEntity().getWorld())) {
			return;
		}

		Entity damagee = event.getEntity();
		boolean playerHurt = damagee instanceof Player;

		// entity took generic damage?
		if(playerHurt) {
			Player player = (Player) damagee;
			cancelFStuckTeleport(player);
		}
	}

	public void cancelFStuckTeleport(Player player) {
		if(player == null) {
			return;
		}
		UUID uuid = player.getUniqueId();
		if(FactionsPlugin.getInstance().stuckMap.containsKey(uuid)) {
			FPlayers.getInstance().getByPlayer(player).msg(TL.COMMAND_STUCK_CANCELLED);
			FactionsPlugin.getInstance().stuckMap.remove(uuid);
		}
	}

	// mainly for flaming arrows; don't want allies or people in safe zones to be ignited even after damage event is cancelled
	@EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
	public void onEntityCombustByEntity(EntityCombustByEntityEvent event) {
		if(!plugin.configMain.restrictWorlds().isEnabled(event.getEntity().getWorld())) {
			return;
		}

		EntityDamageByEntityEvent sub = new EntityDamageByEntityEvent(event.getCombuster(), event.getEntity(), EntityDamageEvent.DamageCause.FIRE, 0d);
		if(!this.canDamagerHurtDamagee(sub, false)) {
			event.setCancelled(true);
		}
	}

	private static final Set<PotionEffectType> badPotionEffects = new LinkedHashSet<>(Arrays.asList(PotionEffectType.BLINDNESS, PotionEffectType.CONFUSION, PotionEffectType.HARM, PotionEffectType.HUNGER, PotionEffectType.POISON, PotionEffectType.SLOW, PotionEffectType.SLOW_DIGGING, PotionEffectType.WEAKNESS, PotionEffectType.WITHER));

	@EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
	public void onPotionSplashEvent(PotionSplashEvent event) {
		if(!plugin.configMain.restrictWorlds().isEnabled(event.getEntity().getWorld())) {
			return;
		}

		// see if the potion has a harmful effect
		boolean badjuju = false;
		for(PotionEffect effect : event.getPotion().getEffects()) {
			if(badPotionEffects.contains(effect.getType())) {
				badjuju = true;
				break;
			}
		}
		if(!badjuju) {
			return;
		}

		ProjectileSource thrower = event.getPotion().getShooter();
		if(!(thrower instanceof Entity)) {
			return;
		}

		if(thrower instanceof Player) {
			Player player = (Player) thrower;
			FPlayer fPlayer = FPlayers.getInstance().getByPlayer(player);
			if(fPlayer.getFaction().getCombatSetting() == Setting.PREVENT_ALL) {
				event.setCancelled(true);
				return;
			}
		}

		// scan through affected entities to make sure they're all valid targets
		for(LivingEntity target : event.getAffectedEntities()) {
			EntityDamageByEntityEvent sub = new EntityDamageByEntityEvent((Entity) thrower, target, EntityDamageEvent.DamageCause.CUSTOM, 0);
			if(!this.canDamagerHurtDamagee(sub, true)) {
				event.setIntensity(target, 0.0);  // affected entity list doesn't accept modification (so no iter.remove()), but this works
			}
		}
	}

	public boolean canDamagerHurtDamagee(EntityDamageByEntityEvent sub, boolean notify) {
		return canDamage(sub.getDamager(), sub.getEntity(), notify);
	}

	public static boolean canDamage(Entity damager, Entity damagee, boolean notify) {
		Faction defLocFaction = Board.getInstance().getFactionAt(new FLocation(damagee.getLocation()));

		// for damage caused by projectiles, getDamager() returns the projectile... what we need to know is the source
		if(damager instanceof Projectile) {
			Projectile projectile = (Projectile) damager;

			if(!(projectile.getShooter() instanceof Entity)) {
				return true;
			}

			damager = (Entity) projectile.getShooter();
		}

		if(damager instanceof Player) {
			Player player = (Player) damager;
			Material material = null;
			switch(damagee.getType()) {
				case ITEM_FRAME:
					material = Material.ITEM_FRAME;
					break;
				case ARMOR_STAND:
					material = Material.ARMOR_STAND;
					break;
			}
			if(material != null && !canUseBlock(player, material, damagee.getLocation())) {
				return false;
			}
		}

		if(!(damagee instanceof Player)) {
			if(defLocFaction.getCombatSetting() == Setting.PREVENT_ALL) {
				if(damager instanceof Player && notify) {
					FPlayers.getInstance().getByPlayer((Player) damager).msg(TL.PERM_DENIED_TERRITORY.format(TL.GENERIC_ATTACK.toString(), defLocFaction.getTag(FPlayers.getInstance().getByPlayer((Player) damager))));
				}
				return false;
			}
			if(damager instanceof Player && defLocFaction.isNormal()) {// TODO
				FPlayer fPlayer = FPlayers.getInstance().getByPlayer((Player) damager);
				if(!defLocFaction.hasAccess(fPlayer, PermissibleAction.DESTROY)) {
					if(notify) {
						fPlayer.msg(TL.PERM_DENIED_TERRITORY.format(TL.GENERIC_ATTACK.toString(), defLocFaction.getTag(FPlayers.getInstance().getByPlayer((Player) damager))));
					}
					return false;
				}
			}
			return true;
		}

		FPlayer defender = FPlayers.getInstance().getByPlayer((Player) damagee);

		if(defender == null || defender.getPlayer() == null) {
			return true;
		}

		if(damager == damagee) {  // ender pearl usage and other self-inflicted damage
			return true;
		}

		// Players can not take attack damage in a SafeZone, or possibly peaceful territory

		if(!(damager instanceof Player)) {
			return true;
		}

		FPlayer attacker = FPlayers.getInstance().getByPlayer((Player) damager);

		if(attacker == null || attacker.getPlayer() == null) {
			return true;
		}

		MainConfiguration.Factions facConf = FactionsPlugin.getInstance().configMain.factions();

		if(attacker.inGracePeriod()) {
			if(notify) {
				attacker.msg(TL.PLAYER_PVP_LOGIN, facConf.combat().gracePeriodOnLogin());
			}
			return false;
		}

		Faction defendFaction = defender.getFaction();
		Faction attackFaction = attacker.getFaction();

		if(defendFaction.isWilderness()) {
			if(defLocFaction == attackFaction && facConf.combat().allowByDefault()) {
				// Allow PVP vs. Factionless in attacker's faction territory
				return true;
			}
		}

		if(defendFaction.getCombatSetting() == Setting.PREVENT_ALL) {
			if(notify) {
				attacker.msg(TL.PLAYER_PVP_PEACEFUL);
			}
			return false;
		} else if(attackFaction.getCombatSetting() == Setting.PREVENT_ALL) {
			if(notify) {
				attacker.msg(TL.PLAYER_PVP_PEACEFUL);
			}
			return false;
		}

		Relation relation = defendFaction.getRelationTo(attackFaction);

		// Players without faction may be hurt anywhere
		if(!defender.hasFaction()) {
			return true;
		}

		// You can never hurt faction members or allies
		if(relation.isMember() || relation.isAlly() || relation.isTruce()) {
			if(notify) {
				attacker.msg(TL.PLAYER_PVP_CANTHURT, defender.describeTo(attacker));
			}
			return false;
		}

		boolean ownTerritory = Board.getInstance().getFactionAt(new FLocation(defender)) == defender.getFaction();

		// You can not hurt neutrals in their own territory.
		if(ownTerritory && relation.isNeutral()) {
			if(notify) {
				attacker.msg(TL.PLAYER_PVP_NEUTRALFAIL, defender.describeTo(attacker));
				defender.msg(TL.PLAYER_PVP_TRIED, attacker.describeTo(defender, true));
			}
			return false;
		}

		return true;
	}

	@EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
	public void onPaintingBreak(HangingBreakEvent event) {
		if(!plugin.configMain.restrictWorlds().isEnabled(event.getEntity().getWorld())) {
			return;
		}

		if(!(event instanceof HangingBreakByEntityEvent)) {
			return;
		}

		Entity breaker = ((HangingBreakByEntityEvent) event).getRemover();
		if(!(breaker instanceof Player)) {
			return;
		}

		if(!FactionsBlockListener.playerCanBuildDestroyBlock((Player) breaker, event.getEntity().getLocation(), PermissibleAction.DESTROY, false)) {
			event.setCancelled(true);
		}
	}

	@EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
	public void onPaintingPlace(HangingPlaceEvent event) {
		if(!plugin.configMain.restrictWorlds().isEnabled(event.getEntity().getWorld())) {
			return;
		}

		if(!FactionsBlockListener.playerCanBuildDestroyBlock(event.getPlayer(), event.getBlock().getRelative(event.getBlockFace()).getLocation(), PermissibleAction.BUILD, false)) {
			event.setCancelled(true);
		}
	}

}
