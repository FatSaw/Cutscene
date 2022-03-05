package me.bomb.cutscene;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public final class SceneEndEvent extends Event {
	private static final HandlerList handlers = new HandlerList();

	public HandlerList getHandlers() {
		return handlers;
	}

	public static HandlerList getHandlerList() {
		return handlers;
	}

	private Player player;
	private RouteProvider route;
	private CameraType cameratype;
	private RouteProvider nextroute;

	protected SceneEndEvent(Player player, RouteProvider route, CameraType cameratype) {
		super(true);
		this.player = player;
		this.route = route;
		this.cameratype = cameratype;
	}

	public Player getPlayer() {
		return player;
	}

	public RouteProvider getRoute() {
		return route;
	}

	public CameraType getCameraType() {
		return cameratype;
	}

	protected RouteProvider getNextRoute() {
		return nextroute;
	}

	public void setNextRoute(RouteProvider nextroute) {
		this.nextroute = nextroute;
	}
}
