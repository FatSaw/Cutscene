package me.bomb.camerautil;

public class LocationPoint {
	private final double x, y, z;
	private final float yaw, pitch;
	
	public LocationPoint(double x,double y,double z,float yaw,float pitch) {
		this.x = x;
		this.y = y;
		this.z = z;
		this.yaw = yaw;
		this.pitch = pitch;
	}
	
	protected final boolean hasMove(LocationPoint location) {
		return location.x - this.x >= 0.1 || this.x - location.x >= 0.1 || location.y - this.y >= 0.1 || this.y - location.y >= 0.1 || location.z - this.z >= 0.1 || this.z - location.z >= 0.1;
	}
	
	public final double getX() {
		return x;
	}
	
	public final double getY() {
		return y;
	}
	
	public final double getZ() {
		return z;
	}
	
	public final float getYaw() {
		return yaw;
	}
	
	public final float getPitch() {
		return pitch;
	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append('#');
		sb.append(this.x);
		sb.append('#');
		sb.append(this.y);
		sb.append('#');
		sb.append(this.z);
		sb.append('#');
		sb.append(this.yaw);
		sb.append('#');
		sb.append(this.pitch);
		sb.append('#');
		return sb.toString();
	}
	
}