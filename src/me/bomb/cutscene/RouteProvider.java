package me.bomb.cutscene;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;

public abstract class RouteProvider {
	private String routename;
	private int stage = 0;
	private World world;
	
	protected RouteProvider(String routename) {
		this.routename = routename;
		FileConfiguration routedata = Cutscene.routedata;
		if (routedata!=null && routedata.contains(routename + ".world") && routedata.isString(routename + ".world")) {
			this.world = Bukkit.getWorld(routedata.getString(routename + ".world"));
		}
	}
	
	public RouteProvider(String routename,World world) {
		this.routename = routename;
		this.world = world;
	}
	
	/**
	 * Returns last LocationPoint.
	 * 
	 * @return last LocationPoint.
	 */
	public abstract LocationPoint getNext();
	
	/**
	 * Returns true if the cutscene continues.
	 * 
	 * @return true if the cutscene continues.
	 */
	public abstract boolean hasNext();
	
	protected final boolean isValid() {
		return world!=null;
	}
	
	public final World getWorld() {
		return world;
	}
	
	public final String getRouteName() {
		return routename;
	}
	
	protected final void resetStage() {
		this.stage = 0;
	}
	
	public final void nextStage() {
		if(this.stage<Integer.MAX_VALUE) ++this.stage;
	}
	
	public final int getStage() {
		return this.stage;
	}
	
}
