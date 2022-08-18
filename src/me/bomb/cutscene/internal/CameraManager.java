package me.bomb.cutscene.internal;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import me.bomb.cutscene.CameraType;
import me.bomb.cutscene.Cutscene;

public abstract class CameraManager {
	
	private static final CameraManager cameramanager;
	
	static {
		switch (Bukkit.getServer().getClass().getPackage().getName().substring(23)) {
		case "v1_17_R1":
			cameramanager = new CameraManager_v1_17_R1();
			break;
		case "v1_16_R3":
			cameramanager = new CameraManager_v1_16_R3();
			break;
		case "v1_15_R1":
			cameramanager = new CameraManager_v1_15_R1();
			break;
		case "v1_14_R1":
			cameramanager = new CameraManager_v1_14_R1();
			break;
		case "v1_13_R2":
			cameramanager = new CameraManager_v1_13_R2();
			break;
		case "v1_12_R1":
			cameramanager = new CameraManager_v1_12_R1();
			break;
		case "v1_11_R1":
			cameramanager = new CameraManager_v1_11_R1();
			break;
		case "v1_10_R1":
			cameramanager = new CameraManager_v1_10_R1();
			break;
		case "v1_9_R2":
			cameramanager = new CameraManager_v1_9_R2();
			break;
		case "v1_8_R3":
			cameramanager = new CameraManager_v1_8_R3();
			break;
		default:
			cameramanager = null;
		}
	}
	
	protected CameraManager() {
		new BukkitRunnable() {
			@Override
			public void run() {
				for(UUID uuid : cameradata.keySet()) {
					Player player = Bukkit.getPlayer(uuid);
					if(player==null||!player.isOnline()) continue;
					if(cameradata.get(uuid).route.hasNext()) {
						updateCameraLocation(player);
					} else {
						CameraData data = cameradata.get(player.getUniqueId());
						SceneEndEvent event = new SceneEndEvent(player, data.route, data.cameratype);
						if(event.nextroute!=null&&event.nextroute.hasNext()) {
							data.route = event.nextroute;
							if(event.newcameratype!=null&&data.cameratype!=event.newcameratype) {
								data.cameratype = event.newcameratype;
								updateCameraType(player);
							}
							continue;
						}
						remove(player);
					}
				}
			}
		}.runTaskTimerAsynchronously(Cutscene.getPlugin(Cutscene.class), 0L, 1);
	}
	
	protected static Map<UUID, CameraData> cameradata = new HashMap<UUID, CameraData>();

	public static final void put(Player player,RouteProvider route,CameraType cameratype) {
		if(player==null||route==null||cameratype==null||new SceneStartEvent(player, route, cameratype).cancel) return;
		if(cameradata.containsKey(player.getUniqueId())) {
			CameraData data = cameradata.get(player.getUniqueId());
			data.route = route;
			data.cameratype = cameratype;
			cameramanager.updateCameraType(player);
			return;
		}
		cameradata.put(player.getUniqueId(),new CameraData(route,null,cameratype));
		cameramanager.spawnCamera(player);
	}
	
	public static final RouteProvider getRoute(Player player) {
		return player!=null && cameradata.containsKey(player.getUniqueId()) ? cameradata.get(player.getUniqueId()).route : null;
	}
	
	public static final CameraType getCameraType(Player player) {
		return player!=null && cameradata.containsKey(player.getUniqueId()) ? cameradata.get(player.getUniqueId()).cameratype : null;
	}
	
	public static final void setRoute(Player player,RouteProvider route) {
		if(player==null||route==null||!cameradata.containsKey(player.getUniqueId())) return;
		cameradata.get(player.getUniqueId()).route = route;
	}
	
	public static final void setCameraType(Player player,CameraType cameratype) {
		if(player==null||cameratype==null||!cameradata.containsKey(player.getUniqueId())) return;
		cameradata.get(player.getUniqueId()).cameratype = cameratype;
		cameramanager.updateCameraType(player);
	}
	
	public static final boolean contains(Player player) {
		return player!=null && cameradata.containsKey(player.getUniqueId());
	}
	
	public static final void remove(Player player) {
		if(player==null) return;
		boolean online = player.isOnline();
		if(online) cameramanager.despawnCamera(player);
		cameradata.remove(player.getUniqueId());
		if(online) cameramanager.restore(player);
	}
	
	public static final void registerHandler(Player player) {
		if(player==null) return;
		cameramanager.register(player);
	}
	
	public static final void unregisterHandler(Player player) {
		if(player==null) return;
		cameramanager.unregister(player);
	}
	
	protected abstract void register(Player player);
	protected abstract void unregister(Player player);
	protected abstract void spawnCamera(Player player);
	protected abstract void updateCameraType(Player player);
	protected abstract void updateCameraLocation(Player player);
	protected abstract void despawnCamera(Player player);
	protected abstract void restore(Player player);
	
	static class CameraData {
		protected RouteProvider route;
		protected Object cameraentity;
		protected CameraType cameratype;
		CameraData(RouteProvider route,Object cameraentity,CameraType cameratype) {
			this.route = route;
			this.cameraentity = cameraentity;
			this.cameratype = cameratype;
		}
	}
	
}