package me.bomb.cutscene;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import me.bomb.camerautil.CameraManager;

public class JoinQuitListener implements Listener {
	@EventHandler
	public void onJoin(PlayerJoinEvent e) {
		CameraManager.registerHandler(e.getPlayer());
	}
	@EventHandler
	public void onQuit(PlayerQuitEvent e) {
		CameraManager.unregisterHandler(e.getPlayer());
		CameraManager.remove(e.getPlayer());
	}
}
