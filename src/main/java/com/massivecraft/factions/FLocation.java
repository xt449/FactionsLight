package com.massivecraft.factions;

import com.massivecraft.factions.util.MiscUtil;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

import java.io.Serializable;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Set;

public class FLocation implements Serializable {
	private static final long serialVersionUID = -8292915234027387983L;
	private static final boolean worldBorderSupport;
	private String worldName = "world";
	private int x = 0;
	private int z = 0;

	static {
		boolean worldBorderClassPresent = false;
		try {
			Class.forName("org.bukkit.WorldBorder");
			worldBorderClassPresent = true;
		} catch(ClassNotFoundException ignored) {
		}

		worldBorderSupport = worldBorderClassPresent;
	}

	//----------------------------------------------//
	// Constructors
	//----------------------------------------------//

	public FLocation() {

	}

	public FLocation(String worldName, int x, int z) {
		this.worldName = worldName;
		this.x = x;
		this.z = z;
	}

	public FLocation(Location location) {
		this(location.getWorld().getName(), blockToChunk(location.getBlockX()), blockToChunk(location.getBlockZ()));
	}

	public FLocation(Chunk chunk) {
		this(chunk.getWorld().getName(), chunk.getX(), chunk.getZ());
	}

	public FLocation(Player player) {
		this(player.getLocation());
	}

	public FLocation(FPlayer fplayer) {
		this(fplayer.getPlayer());
	}

	public FLocation(Block block) {
		this(block.getLocation());
	}

	//----------------------------------------------//
	// Getters and Setters
	//----------------------------------------------//

	public String getWorldName() {
		return worldName;
	}

	public World getWorld() {
		return Bukkit.getWorld(worldName);
	}

	public void setWorldName(String worldName) {
		this.worldName = worldName;
	}

	public long getX() {
		return x;
	}

	public long getZ() {
		return z;
	}

	public String getCoordString() {
		return "" + x + "," + z;
	}

	public Chunk getChunk() {
		return new Location(getWorld(), chunkToBlock(x), 0, chunkToBlock(z)).getChunk();
	}

	@Override
	public String toString() {
		return "[" + this.getWorldName() + "," + this.getCoordString() + "]";
	}

	public static FLocation fromString(String string) {
		int index = string.indexOf(',');
		int start = 1;
		String worldName = string.substring(start, index);
		start = index + 1;
		index = string.indexOf(',', start);
		int x = Integer.parseInt(string.substring(start, index));
		int y = Integer.parseInt(string.substring(index + 1, string.length() - 1));
		return new FLocation(worldName, x, y);
	}

	//----------------------------------------------//
	// Block/Chunk/Region Value Transformation
	//----------------------------------------------//

	// bit-shifting is used because it's much faster than standard division and multiplication
	public static int blockToChunk(int blockVal) {    // 1 chunk is 16x16 blocks
		return blockVal >> 4;   // ">> 4" == "/ 16"
	}

	public static int chunkToBlock(int chunkVal) {
		return chunkVal << 4;   // "<< 4" == "* 16"
	}

	//----------------------------------------------//
	// Misc Geometry
	//----------------------------------------------//

	public FLocation getRelative(int dx, int dz) {
		return new FLocation(this.worldName, this.x + dx, this.z + dz);
	}

	public double getDistanceTo(FLocation that) {
		double dx = that.x - this.x;
		double dz = that.z - this.z;
		return Math.sqrt(dx * dx + dz * dz);
	}

	public double getDistanceSquaredTo(FLocation that) {
		double dx = that.x - this.x;
		double dz = that.z - this.z;
		return dx * dx + dz * dz;
	}

	public boolean isInChunk(Location loc) {
		if(loc == null) {
			return false;
		}
		Chunk chunk = loc.getChunk();
		return loc.getWorld().getName().equalsIgnoreCase(getWorldName()) && chunk.getX() == x && chunk.getZ() == z;
	}

	//----------------------------------------------//
	// Some Geometry
	//----------------------------------------------//
	public Set<FLocation> getCircle(double radius) {
		double radiusSquared = radius * radius;

		Set<FLocation> ret = new LinkedHashSet<>();
		if(radius <= 0) {
			return ret;
		}

		int xfrom = (int) Math.floor(this.x - radius);
		int xto = (int) Math.ceil(this.x + radius);
		int zfrom = (int) Math.floor(this.z - radius);
		int zto = (int) Math.ceil(this.z + radius);

		for(int x = xfrom; x <= xto; x++) {
			for(int z = zfrom; z <= zto; z++) {
				FLocation potential = new FLocation(this.worldName, x, z);
				if(this.getDistanceSquaredTo(potential) <= radiusSquared) {
					ret.add(potential);
				}
			}
		}

		return ret;
	}

	public static HashSet<FLocation> getArea(FLocation from, FLocation to) {
		HashSet<FLocation> ret = new HashSet<>();

		for(long x : MiscUtil.range(from.getX(), to.getX())) {
			for(long z : MiscUtil.range(from.getZ(), to.getZ())) {
				ret.add(new FLocation(from.getWorldName(), (int) x, (int) z));
			}
		}

		return ret;
	}

	//----------------------------------------------//
	// Comparison
	//----------------------------------------------//

	@Override
	public int hashCode() {
		// should be fast, with good range and few hash collisions: (x * 512) + z + worldName.hashCode
		return (this.x << 9) + this.z + (this.worldName != null ? this.worldName.hashCode() : 0);
	}

	@Override
	public boolean equals(Object obj) {
		if(obj == this) {
			return true;
		}
		if(!(obj instanceof FLocation)) {
			return false;
		}

		FLocation that = (FLocation) obj;
		return this.x == that.x && this.z == that.z && (Objects.equals(this.worldName, that.worldName));
	}
}
