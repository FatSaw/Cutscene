package me.bomb.cutscene.event;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import me.bomb.camerautil.CameraType;
import me.bomb.cutscene.route.AbstractRoute;

public final class SceneEndEvent extends Event {
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
	private AbstractRoute nextroute;

	public SceneEndEvent(Player player, AbstractRoute route, CameraType cameratype) {
		super(true);
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

	public AbstractRoute getNextRoute() {
		return nextroute;
	}

	public void setCameraType(CameraType cameratype) {
		this.cameratype = cameratype;
	}

	public void setNextRoute(AbstractRoute nextroute) {
		this.nextroute = nextroute;
	}
}
