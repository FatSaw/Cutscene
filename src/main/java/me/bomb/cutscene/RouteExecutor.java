package me.bomb.cutscene;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import me.bomb.camerautil.CameraManager;
import me.bomb.camerautil.CameraType;

public class RouteExecutor extends Thread {
	
	private final CameraManager cameramanager;
	
	public RouteExecutor(CameraManager cameramanager) {
		this.cameramanager = cameramanager;
	}
	
	private volatile boolean run;
	
	private Map<UUID,RouteData> routes = new HashMap<>();
	
	@Override
	public void start() {
		run = true;
		super.start();
	}

	public void end() {
		run = false;
	}
	
	@Override
	public void run() {
		final Set<Entry<UUID,RouteData>> entrys = routes.entrySet();
		while(run) {
			try {
				sleep(50);
			} catch (InterruptedException e) {
			}
			Iterator<Entry<UUID, RouteData>> iterator = entrys.iterator();
			while(iterator.hasNext()) {
				final Entry<UUID, RouteData> entry = iterator.next();
				final UUID uuid = entry.getKey();
				Player player = Bukkit.getPlayer(uuid);
				if(player==null||!player.isOnline()) {
					iterator.remove();
					continue;
				}
				final RouteData data = entry.getValue();
				if(data.routeprovider.hasNext()) {
					if(cameramanager.contains(player)) {
						cameramanager.setLocationPoint(player, data.routeprovider.getNext());
					} else if(!new SceneStartEvent(player, data.routeprovider, data.camera).isCanceled()) {
						cameramanager.put(player, data.routeprovider.getNext(), data.camera, true, true);
					}
				} else {
					SceneEndEvent see = new SceneEndEvent(player, data.routeprovider, data.camera);
					if(see.getNextRoute()==null) {
						cameramanager.remove(player);
						iterator.remove();
						continue;
					} else if(see.getCameraType()==data.camera) continue;
					cameramanager.setCameraType(player, see.getCameraType());
				}
			}
		}
	}
	
	public void put(Player player,RouteProvider route,CameraType camera) {
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
