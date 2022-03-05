package me.bomb.cutscene;

import org.bukkit.Location;
import org.bukkit.World;

public class LocationPoint {
	protected double x;
	protected double y;
	protected double z;
	protected float yaw;
	protected float pitch;
	
	public LocationPoint(double x,double y,double z,float yaw,float pitch) {
		this.x = x;
		this.y = y;
		this.z = z;
		this.yaw = yaw;
		this.pitch = pitch;
	}
	
	public Location toLocation(World world) {
		return new Location(world, x, y, z, yaw, pitch);
	}
	
	protected String toPointString() {
		return "#" + x + "#" + y + "#" + z + "#" + yaw + "#" + pitch + "#";
	}
	
	protected double getX() {
		return x;
	}
	
	protected double getY() {
		return y;
	}
	
	protected double getZ() {
		return z;
	}
	
	protected float getYaw() {
		return yaw;
	}

	protected float getPitch() {
		return pitch;
	}
}