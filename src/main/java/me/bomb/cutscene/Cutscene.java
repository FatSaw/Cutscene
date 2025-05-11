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
	private volatile boolean supported = false;
	private static File routefile;
	
	private final CameraManager cameramanager;
	private RouteExecutor routeexecutor;
	private final int version;
	
	public Cutscene() {
		int version = 0;
		try {
			final String nmsversion = this.getServer().getClass().getPackage().getName().substring(23);
			switch(nmsversion) {
			case "v1_19_R3":
				version = 19;
			break;
			case "v1_18_R2":
				version = 18;
			break;
			case "v1_17_R1":
				version = 17;
			break;
			case "v1_16_R3":
				version = 16;
			break;
			case "v1_15_R1":
				version = 15;
			break;
			case "v1_14_R1":
				version = 14;
			break;
			case "v1_13_R2":
				version = 13;
			break;
			case "v1_12_R1":
				version = 12;
			break;
			case "v1_11_R1":
				version = 11;
			break;
			case "v1_10_R1":
				version = 10;
			break;
			case "v1_9_R2":
				version = 9;
			break;
			case "v1_8_R3":
				version = 8;
			break;
			default:
				version = 0;
			}
		} catch (Exception e) {
		}
		this.version = version;
		this.cameramanager = this.version == 0 ? null : CameraManager.initialize(this.version);
	}
	
	@Override
	public void onEnable() {
		if(this.cameramanager == null) {
			getLogger().log(Level.WARNING, "Unsupported server version!");
			getServer().getPluginManager().disablePlugin(this);
			return;
		}
		try {
			routefile = new File(getDataFolder(), "route.yml");
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
			if (!new File(getDataFolder(), "lang.yml").exists()) {
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
		this.routeexecutor = new RouteExecutor(cameramanager);
		try {
			PluginCommand playscenecommand = getCommand("playscene");
			playscenecommand.setExecutor(new PlaysceneCommand(this.cameramanager, this.routeexecutor, this.version));
			playscenecommand.setTabCompleter(new PlaysceneTabCompleter());
		} catch (Exception e) {
			getLogger().log(Level.WARNING, "Error on register playscene command!");
			getServer().getPluginManager().disablePlugin(this);
			return;
		}
		getServer().getOnlinePlayers().forEach(player -> {
			cameramanager.registerHandler(player);
		});
		routeexecutor.start();
		getLogger().log(Level.INFO, "Plugin enabeled!");
		supported = true;
	}
	
	public void onDisable() {
		if (supported) {
			routeexecutor.end();
			getServer().getOnlinePlayers().forEach(player -> {
				cameramanager.unregisterHandler(player);
				cameramanager.remove(player);
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
