package me.bomb.cutscene.internal;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import me.bomb.cutscene.CameraType;

public final class SceneStartEvent extends Event {
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
	protected boolean cancel = false;

	protected SceneStartEvent(Player player, RouteProvider route, CameraType cameratype) {
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

	public void setCanceled(boolean cancel) {
		this.cancel = cancel;
	}

	public boolean isCanceled() {
		return cancel;
	}
}
