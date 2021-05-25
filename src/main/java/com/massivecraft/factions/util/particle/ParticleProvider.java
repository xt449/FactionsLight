package com.massivecraft.factions.util.particle;

import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.Player;

public class ParticleProvider {

	public void spawn(Particle effect, Location location, int count) {
		location.getWorld().spawnParticle(effect, location, count);
	}

	public void playerSpawn(Player player, Particle effect, Location location, int count) {
		player.spawnParticle(effect, location, count);
	}

	public void spawn(Particle particle, Location location, int count, double speed, double offsetX, double offsetY, double offsetZ) {
		location.getWorld().spawnParticle(particle, location, count, offsetX, offsetY, offsetZ, speed);
	}

	public void playerSpawn(Player player, Particle particle, Location location, int count, double speed, double offsetX, double offsetY, double offsetZ) {
		player.spawnParticle(particle, location, count, offsetX, offsetY, offsetZ, speed);
	}

	public void spawn(Particle particle, Location location, ParticleColor color) {
		location.getWorld().spawnParticle(particle, location, 1, new Particle.DustOptions(color.getColor(), 1));
	}

	public void playerSpawn(Player player, Particle particle, Location location, ParticleColor color) {
		player.spawnParticle(particle, location, 1, new Particle.DustOptions(color.getColor(), 1.5f));
	}

	public Particle effectFromString(String string) {
		for(Particle particle : Particle.values()) {
			if(particle.name().equalsIgnoreCase(string)) {
				return particle;
			}
		}
		return null;
	}

	public String effectName(Particle particle) {
		return particle.name();
	}
}
