package me.bomb.cutscene.route;

import me.bomb.camerautil.LocationPoint;

public class CameraStatic extends AbstractRoute {
	private LocationPoint locationpoint;
	private final int time;
	
	public CameraStatic(String routename, double x, double y, double z, float yaw, float pitch,int time) {
		super(routename);
		this.locationpoint = new LocationPoint(x, y, z, yaw, pitch);
		this.time = time;
	}
	
	@Override
	public LocationPoint getNext() {
		nextStage();
		return locationpoint;
	}
	
	@Override
	public boolean hasNext() {
		return getStage()<time;
	}
	
}
