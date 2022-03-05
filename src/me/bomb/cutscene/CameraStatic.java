package me.bomb.cutscene;

import org.bukkit.Location;

public class CameraStatic extends RouteProvider {
	private LocationPoint locationpoint;
	private int time;
	
	CameraStatic(String routename,Location location,int time) {
		super(routename, location.getWorld());
		this.locationpoint = new LocationPoint(location.getX(), location.getY(), location.getZ(), location.getYaw(), location.getPitch());
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
