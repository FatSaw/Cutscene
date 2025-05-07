package me.bomb.camerautil;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;

import org.bukkit.entity.Player;

public abstract class CameraManager {
	
	public static final CameraManager initialize(final int mcversion) {
		switch(mcversion) {
		case 19:
			return new CameraManager_v1_19_R1();
		case 18:
			return new CameraManager_v1_18_R2();
		case 17:
			return new CameraManager_v1_17_R1();
		case 16:
			return new CameraManager_v1_16_R3();
		case 15:
			return new CameraManager_v1_15_R1();
		case 14:
			return new CameraManager_v1_14_R1();
		case 13:
			return new CameraManager_v1_13_R2();
		case 12:
			return new CameraManager_v1_12_R1();
		case 11:
			return new CameraManager_v1_11_R1();
		case 10:
			return new CameraManager_v1_10_R1();
		case 9:
			return new CameraManager_v1_9_R2();
		case 8:
			return new CameraManager_v1_8_R3();
		default:
			return null;
		}
	}
	
	protected ConcurrentHashMap<UUID, AtomicBoolean> filters = new ConcurrentHashMap<>();
	protected Map<UUID, CameraData> cameradata = new HashMap<UUID, CameraData>();
	
	public final void put(Player player,LocationPoint currentlocation,CameraType cameratype) {
		final AtomicBoolean filter;
		final UUID playeruuid;
		if(player==null||currentlocation==null||cameratype==null||(filter=filters.get(playeruuid = player.getUniqueId()))==null) {
			return;
		}
		CameraData data = cameradata.get(playeruuid);
		if(data!=null) {
			data.currentlocation = currentlocation;
			data.cameratype = cameratype;
			this.updateCameraType(player);
			return;
		}
		filter.set(true);
		cameradata.put(playeruuid,new CameraData(currentlocation,null,cameratype));
		this.spawnCamera(player);
	}
	
	public final LocationPoint getFirstLocationPoint(Player player) {
		return player!=null && cameradata.containsKey(player.getUniqueId()) ? cameradata.get(player.getUniqueId()).firstlocation : null;
	}
	
	public final LocationPoint getPreviousLocationPoint(Player player) {
		return player!=null && cameradata.containsKey(player.getUniqueId()) ? cameradata.get(player.getUniqueId()).previouslocation : null;
	}
	
	public final LocationPoint getCurrentLocationPoint(Player player) {
		return player!=null && cameradata.containsKey(player.getUniqueId()) ? cameradata.get(player.getUniqueId()).currentlocation : null;
	}
	
	public final CameraType getCameraType(Player player) {
		return player!=null && cameradata.containsKey(player.getUniqueId()) ? cameradata.get(player.getUniqueId()).cameratype : null;
	}
	
	public final void setLocationPoint(Player player,LocationPoint currentlocation) {
		if(player==null||currentlocation==null||!cameradata.containsKey(player.getUniqueId())) return;
		CameraData data = cameradata.get(player.getUniqueId());
		if(data.currentlocation.hasMove(currentlocation)) {
			data.previouslocation = data.currentlocation;
			data.currentlocation = currentlocation;
		}
		this.updateCameraLocation(player);
	}
	
	public final void setCameraType(Player player,CameraType cameratype) {
		if(player==null||cameratype==null||!cameradata.containsKey(player.getUniqueId())) return;
		cameradata.get(player.getUniqueId()).cameratype = cameratype;
		this.updateCameraType(player);
	}
	
	public final Set<UUID> keySet() {
		return cameradata.keySet();
	}
	
	public final boolean contains(Player player) {
		return player!=null && cameradata.containsKey(player.getUniqueId());
	}
	
	public final void remove(Player player) {
		if(player==null) return;

		AtomicBoolean filter = filters.get(player.getUniqueId());
		final boolean online = filter != null;
		if(online) {
			this.despawnCamera(player);
			filter.set(false);
		}
		cameradata.remove(player.getUniqueId());
		if(online) this.restore(player);
	}
	
	public void registerHandler(Player player) {
		if(player==null) return;
		AtomicBoolean filter = new AtomicBoolean(false);
		filters.put(player.getUniqueId(), filter);
		this.register(player, filter);
	}
	
	public final void unregisterHandler(Player player) {
		if(player==null) return;
		this.unregister(player);
		filters.remove(player.getUniqueId());
	}
	
	protected abstract void register(Player player, AtomicBoolean filter);
	protected abstract void unregister(Player player);
	protected abstract void spawnCamera(Player player);
	protected abstract void updateCameraType(Player player);
	protected abstract void updateCameraLocation(Player player);
	protected abstract void despawnCamera(Player player);
	protected abstract void restore(Player player);
	
	static final class CameraData {
		protected LocationPoint firstlocation, previouslocation, currentlocation;
		protected Object cameraentity;
		protected CameraType cameratype;
		private CameraData(LocationPoint currentlocation,Object cameraentity,CameraType cameratype) {
			this.firstlocation = currentlocation;
			this.previouslocation = currentlocation;
			this.currentlocation = currentlocation;
			this.cameraentity = cameraentity;
			this.cameratype = cameratype;
		}
	}
	
}