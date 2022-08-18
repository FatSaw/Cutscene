package me.bomb.cutscene.internal;

import org.bukkit.World;

public abstract class RouteProvider {
	public final String routename;
	private int stage = 0;
	public final World world;
	
	public RouteProvider(String routename,World world) {
		this.routename = routename;
		this.world = world;
	}
	
	/**
	 * Returns current LocationPoint.
	 * Should call nextStage() if hasNext() true;
	 * 
	 * @return current LocationPoint.
	 */
	public abstract LocationPoint getNext();
	
	/**
	 * Returns true if the cutscene continues.
	 * 
	 * @return true if the cutscene continues.
	 */
	public abstract boolean hasNext();
	
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
