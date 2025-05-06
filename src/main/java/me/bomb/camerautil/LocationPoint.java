package me.bomb.camerautil;

public class LocationPoint {
	private final double x;
	private final double y;
	private final double z;
	private final float yaw;
	private final float pitch;
	
	public LocationPoint(double x,double y,double z,float yaw,float pitch) {
		this.x = x;
		this.y = y;
		this.z = z;
		this.yaw = yaw;
		this.pitch = pitch;
	}
	
	/*protected final boolean hasMove(double x,double y,double z) {
		return x - this.x >= 0.1 || this.x - x >= 0.1 || y - this.y >= 0.1 || this.y - y >= 0.1 || z - this.z >= 0.1 || this.z - z >= 0.1;
	}*/
	
	protected final boolean hasMove(LocationPoint location) {
		return location.x - this.x >= 0.1 || this.x - location.x >= 0.1 || location.y - this.y >= 0.1 || this.y - location.y >= 0.1 || location.z - this.z >= 0.1 || this.z - location.z >= 0.1;
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