package me.bomb.cutscene;

import java.util.HashMap;
import java.util.UUID;

import org.bukkit.entity.Player;

public abstract class CameraManager {
	protected HashMap<UUID, RouteProvider> pos = new HashMap<UUID, RouteProvider>();
	protected HashMap<UUID, Object> eas = new HashMap<UUID, Object>();

	/**
	 * Returns true if contains a playing route for player uuid.
	 * 
	 * @param uuid
	 * @return true if contains a playing route for player uuid.
	 */
	public final boolean contains(UUID uuid) {
		if (eas.containsKey(uuid) && pos.containsKey(uuid)) {
			return true;
		}
		return false;
	}

	protected final void endroute(UUID uuid) {
		eas.remove(uuid);
		pos.remove(uuid);
	}

	/**
	 * Starts camera movement by route
	 * 
	 * @param player
	 * @param route
	 * @param type
	 */
	public abstract void startroute(Player player, RouteProvider route, CameraType type);

	protected abstract void registerHandler(Player player);

	protected abstract void unregisterHandler(Player player);
	
}