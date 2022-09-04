package me.bomb.cutscene;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import me.bomb.camerautil.CameraManager;
import me.bomb.camerautil.CameraType;

public class RouteExecutor {
	private static Map<UUID,RouteData> routes = new HashMap<UUID,RouteData>();
	
	public static void init(JavaPlugin plugin) {
		new BukkitRunnable() {
			@Override
			public void run() {
				Set<UUID> toremove = new HashSet<UUID>();
				for(UUID uuid : routes.keySet()) {
					Player player = Bukkit.getPlayer(uuid);
					if(player==null||!player.isOnline()) {
						toremove.add(uuid);
						continue;
					}
					RouteData data = routes.get(uuid);
					if(data.routeprovider.hasNext()) {
						if(CameraManager.contains(player)) {
							CameraManager.setLocationPoint(player, data.routeprovider.getNext());
						} else if(!new SceneStartEvent(player, data.routeprovider, data.camera).isCanceled()) {
							CameraManager.put(player, data.routeprovider.getNext(), data.camera, true, true);
						}
					} else {
						SceneEndEvent see = new SceneEndEvent(player, data.routeprovider, data.camera);
						if(see.getNextRoute()==null) {
							CameraManager.remove(player);
							toremove.add(uuid);
							continue;
						} else if(see.getCameraType()==data.camera) continue;
						CameraManager.setCameraType(player, see.getCameraType());
					}
				}
				for(UUID uuid : toremove) {
					routes.remove(uuid);
				}
			}
		}.runTaskTimerAsynchronously(plugin, 0L, 1);
	}
	
	public static void put(Player player,RouteProvider route,CameraType camera) {
		routes.put(player.getUniqueId(), new RouteData(route, camera));
	}
	
	private static final class RouteData {
		private RouteProvider routeprovider;
		private CameraType camera;
		private RouteData(RouteProvider routeprovider,CameraType camera) {
			this.routeprovider = routeprovider;
			this.camera = camera;
		}
	}
}
