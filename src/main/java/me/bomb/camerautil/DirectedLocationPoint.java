package me.bomb.camerautil;

public class DirectedLocationPoint extends LocationPoint {
	
	private final float dyaw, dpitch;
	
	public DirectedLocationPoint(double x,double y,double z,float yaw,float pitch,float dyaw,float dpitch) {
		super(x, y, z, yaw, pitch);
		this.dyaw = dyaw;
		this.dpitch = dpitch;
	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder(super.toString());
		sb.append(dyaw);
		sb.append('#');
		sb.append(dpitch);
		sb.append('#');
		return sb.toString();
	}
	
	public float getDirectionYaw() {
		return dyaw;
	}
	
	public float getDirectionPitch() {
		return dpitch;
	}

}
