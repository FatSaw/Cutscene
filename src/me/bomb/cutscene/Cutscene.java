package me.bomb.cutscene;

import java.io.File;
import java.io.IOException;
import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.command.PluginCommand;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

public class Cutscene extends JavaPlugin {
	
	protected static FileConfiguration routedata;
	protected static YamlConfiguration lang;
	private boolean supported = false;
	protected static boolean api = false;
	private static File routefile;
	
	@Override
	public void onEnable() {
		switch (Bukkit.getServer().getClass().getPackage().getName().substring(23)) {
		case "v1_19_R1": case "v1_18_R2": case "v1_17_R1":case "v1_16_R3":case "v1_15_R1":case "v1_14_R1":case "v1_13_R2":case "v1_12_R1":case "v1_11_R1":case "v1_10_R1":case "v1_9_R2":case "v1_8_R3":
			if(CutsceneAPI.cameramanager == null) {
				getLogger().log(Level.WARNING, "Error on loading CameraManager!");
				getServer().getPluginManager().disablePlugin(this);
			} else {
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
					Bukkit.getPluginManager().registerEvents(new JoinQuitListener(), this);
				} catch (Exception e) {
					getLogger().log(Level.WARNING, "Error on register events!");
					getServer().getPluginManager().disablePlugin(this);
					return;
				}
				try {
					PluginCommand playscenecommand = getCommand("playscene");
					playscenecommand.setExecutor(new PlaysceneCommand());
					playscenecommand.setTabCompleter(new PlaysceneTabCompleter());
				} catch (Exception e) {
					getLogger().log(Level.WARNING, "Error on register playscene command!");
					getServer().getPluginManager().disablePlugin(this);
					return;
				}
				Bukkit.getOnlinePlayers().forEach(player -> {
					CutsceneAPI.cameramanager.registerHandler(player);
				});
				getLogger().log(Level.INFO, "Plugin enabeled!");
				if (!new File(getDataFolder() + File.separator + "noapi").exists()) {
					Cutscene.api = true;
					getLogger().log(Level.INFO, "API enabled!");
				}
				supported = true;
			}
			break;
		default:
			getLogger().log(Level.WARNING, "Unsupported version!");
			getLogger().log(Level.WARNING, "Supported versions: 1.8.4(+4),1.9.4,1.10(+2),1.11(+2),1.12(+2),1.13.2,1.14(+3),1.15(+2),1.16.4(+1),1.17(+1),1.18.2,1.19");
			getServer().getPluginManager().disablePlugin(this);
		}
	}
	
	public void onDisable() {
		if (supported) {
			Bukkit.getOnlinePlayers().forEach(player -> {
				CutsceneAPI.cameramanager.unregisterHandler(player);
			});
		}
	}
	
	protected static void saveRoutes() {
		try {
			Cutscene.routedata.save(routefile);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
}
