package me.bomb.cutscene.event;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import me.bomb.camerautil.CameraType;
import me.bomb.cutscene.route.AbstractRoute;

public final class SceneStartEvent extends Event {
	private static final HandlerList handlers = new HandlerList();

	public HandlerList getHandlers() {
		return handlers;
	}

	public static HandlerList getHandlerList() {
		return handlers;
	}

	private Player player;
	private AbstractRoute route;
	private CameraType cameratype;
	private boolean cancel = false;

	public SceneStartEvent(Player player, AbstractRoute route, CameraType cameratype) {
		this.player = player;
		this.route = route;
		this.cameratype = cameratype;
	}

	public Player getPlayer() {
		return player;
	}

	public AbstractRoute getRoute() {
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
