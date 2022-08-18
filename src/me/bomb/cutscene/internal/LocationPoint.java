package me.bomb.cutscene.internal;

import org.bukkit.Location;
import org.bukkit.World;

public class LocationPoint {
	public final double x;
	public final double y;
	public final double z;
	public final float yaw;
	public final float pitch;
	
	public LocationPoint(double x,double y,double z,float yaw,float pitch) {
		this.x = x;
		this.y = y;
		this.z = z;
		this.yaw = yaw;
		this.pitch = pitch;
	}
	
	public final boolean hasMove(double x,double y,double z) {
		return x - this.x >= 0.1 || this.x - x >= 0.1 || y - this.y >= 0.1 || this.y - y >= 0.1 || z - this.z >= 0.1 || this.z - z >= 0.1;
	}
	
	public Location toLocation(World world) {
		return new Location(world, x, y, z, yaw, pitch);
	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("#");
		sb.append(x);
		sb.append("#");
		sb.append(y);
		sb.append("#");
		sb.append(z);
		sb.append("#");
		sb.append(yaw);
		sb.append("#");
		sb.append(pitch);
		sb.append("#");
		return sb.toString();
	}
	
	public double getX() {
		return x;
	}
	
	public double getY() {
		return y;
	}
	
	public double getZ() {
		return z;
	}
	
	public float getYaw() {
		return yaw;
	}
	
	public float getPitch() {
		return pitch;
	}
	
}