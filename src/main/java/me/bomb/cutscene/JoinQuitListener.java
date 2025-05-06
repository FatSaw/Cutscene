package me.bomb.cutscene;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import me.bomb.camerautil.CameraManager;

public class JoinQuitListener implements Listener {
	
	private final CameraManager cameramanager;
	
	public JoinQuitListener(CameraManager cameramanager) {
		this.cameramanager = cameramanager;
	}
	
	@EventHandler
	public void onJoin(PlayerJoinEvent e) {
		cameramanager.registerHandler(e.getPlayer());
	}
	@EventHandler
	public void onQuit(PlayerQuitEvent e) {
		cameramanager.unregisterHandler(e.getPlayer());
		cameramanager.remove(e.getPlayer());
	}
}
