package me.bomb.cutscene;

import java.io.File;
import java.io.IOException;
import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.command.PluginCommand;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import me.bomb.camerautil.CameraManager;
import me.bomb.cutscene.Route.RouteLocationPoint;

public final class Cutscene extends JavaPlugin {
	
	protected static FileConfiguration routedata;
	protected static YamlConfiguration lang;
	private boolean supported = false;
	private static File routefile;
	
	private final CameraManager cameramanager;
	private final RouteExecutor routeexecutor;
	
	public Cutscene() {
		CameraManager cameramanager = null;
		try {
			final String nmsversion = this.getServer().getClass().getPackage().getName().substring(23);
			switch(nmsversion) {
			case "v1_19_R1":
				cameramanager = CameraManager.initialize(19);
			break;
			case "v1_18_R2":
				cameramanager = CameraManager.initialize(18);
			break;
			case "v1_17_R1":
				cameramanager = CameraManager.initialize(17);
			break;
			case "v1_16_R3":
				cameramanager = CameraManager.initialize(16);
			break;
			case "v1_15_R1":
				cameramanager = CameraManager.initialize(15);
			break;
			case "v1_14_R1":
				cameramanager = CameraManager.initialize(14);
			break;
			case "v1_13_R2":
				cameramanager = CameraManager.initialize(13);
			break;
			case "v1_12_R1":
				cameramanager = CameraManager.initialize(12);
			break;
			case "v1_11_R1":
				cameramanager = CameraManager.initialize(11);
			break;
			case "v1_10_R1":
				cameramanager = CameraManager.initialize(10);
			break;
			case "v1_9_R2":
				cameramanager = CameraManager.initialize(9);
			break;
			case "v1_8_R3":
				cameramanager = CameraManager.initialize(8);
			break;
			}
		} catch (Exception e) {
		}
		this.cameramanager = cameramanager;
		this.routeexecutor = new RouteExecutor(cameramanager);
	}
	
	@Override
	public void onEnable() {
		switch (Bukkit.getServer().getClass().getPackage().getName().substring(23)) {
		case "v1_19_R1":case "v1_18_R2":case "v1_17_R1":case "v1_16_R3":case "v1_15_R1":case "v1_14_R1":case "v1_13_R2":case "v1_12_R1":case "v1_11_R1":case "v1_10_R1":case "v1_9_R2":case "v1_8_R3":
			try {
				routefile = new File(getDataFolder() + File.separator + "route.yml");
				if (!routefile.exists()) {
					saveResource("route.yml", true);
				}
				routedata = YamlConfiguration.loadConfiguration(routefile);
			} catch (Exception e) {
				getLogger().log(Level.WARNING, "Error on loading route file!");
				getServer().getPluginManager().disablePlugin(this);
				return;
			}
			try {
				if (!new File(getDataFolder() + File.separator + "lang.yml").exists()) {
					saveResource("lang.yml", true);
				}
				lang = YamlConfiguration.loadConfiguration(new File(getDataFolder() + File.separator + "lang.yml"));
			} catch (Exception e) {
				getLogger().log(Level.WARNING, "Error on loading language file!");
				getServer().getPluginManager().disablePlugin(this);
				return;
			}
			try {
				Bukkit.getPluginManager().registerEvents(new JoinQuitListener(this.cameramanager), this);
			} catch (Exception e) {
				getLogger().log(Level.WARNING, "Error on register events!");
				getServer().getPluginManager().disablePlugin(this);
				return;
			}
			try {
				PluginCommand playscenecommand = getCommand("playscene");
				playscenecommand.setExecutor(new PlaysceneCommand(this.cameramanager, this.routeexecutor));
				playscenecommand.setTabCompleter(new PlaysceneTabCompleter());
			} catch (Exception e) {
				getLogger().log(Level.WARNING, "Error on register playscene command!");
				getServer().getPluginManager().disablePlugin(this);
				return;
			}
			Bukkit.getOnlinePlayers().forEach(player -> {
				cameramanager.registerHandler(player);
			});
			routeexecutor.start();
			getLogger().log(Level.INFO, "Plugin enabeled!");
			supported = true;
			break;
		default:
			getLogger().log(Level.WARNING, "Unsupported server version!");
			getServer().getPluginManager().disablePlugin(this);
		}
	}
	
	public void onDisable() {
		if (supported) {
			routeexecutor.end();
			Bukkit.getOnlinePlayers().forEach(player -> {
				cameramanager.unregisterHandler(player);
			});
		}
	}
	
	public static Route readroute(String routename,RouteLocationPoint previouslocation) {
		return Route.readRoute(routedata, routename, previouslocation);
	}
	
	public static void saveroute(Route route) {
		route.writeRoute(routedata);
	}
	
	public static void saveRoutes() {
		try {
			Cutscene.routedata.save(routefile);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
}
