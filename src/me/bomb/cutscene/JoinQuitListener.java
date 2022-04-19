package me.bomb.cutscene;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class JoinQuitListener implements Listener {
	@EventHandler
	public void onJoin(PlayerJoinEvent e) {
		CutsceneAPI.cameramanager.registerHandler(e.getPlayer());
	}
	@EventHandler
	public void onQuit(PlayerQuitEvent e) {
		CutsceneAPI.cameramanager.unregisterHandler(e.getPlayer());
		CutsceneAPI.cameramanager.endroute(e.getPlayer().getUniqueId());
	}
}
