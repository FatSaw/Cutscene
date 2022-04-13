package me.bomb.cutscene;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.java.JavaPlugin;

public class Cutscene extends JavaPlugin implements Listener {

	private CameraManager cameramanager;
	protected static FileConfiguration routedata;
	private YamlConfiguration lang;
	private boolean supported = false;
	protected static boolean api = false;
	private static File routefile;

	@Override
	public void onEnable() {
		routefile = new File(getDataFolder() + File.separator + "route.yml");
		switch (Bukkit.getServer().getClass().getPackage().getName().substring(23)) {
		case "v1_18_R2":
			cameramanager = new CameraManager_v1_18_R2();
			supported = true;
			break;
		case "v1_17_R1":
			cameramanager = new CameraManager_v1_17_R1();
			supported = true;
			break;
		case "v1_16_R3":
			cameramanager = new CameraManager_v1_16_R3();
			supported = true;
			break;
		case "v1_15_R1":
			cameramanager = new CameraManager_v1_15_R1();
			supported = true;
			break;
		case "v1_14_R1":
			cameramanager = new CameraManager_v1_14_R1();
			supported = true;
			break;
		case "v1_13_R2":
			cameramanager = new CameraManager_v1_13_R2();
			supported = true;
			break;
		case "v1_12_R1":
			cameramanager = new CameraManager_v1_12_R1();
			supported = true;
			break;
		case "v1_11_R1":
			cameramanager = new CameraManager_v1_11_R1();
			supported = true;
			break;
		case "v1_10_R1":
			cameramanager = new CameraManager_v1_10_R1();
			supported = true;
			break;
		case "v1_9_R2":
			cameramanager = new CameraManager_v1_9_R2();
			supported = true;
			break;
		case "v1_8_R3":
			cameramanager = new CameraManager_v1_8_R3();
			supported = true;
			break;
		}
		if (supported) {
			if (!routefile.exists()) {
				saveResource("route.yml", true);
			}
			routedata = YamlConfiguration.loadConfiguration(routefile);
			if (!new File(getDataFolder() + File.separator + "lang.yml").exists()) {
				saveResource("lang.yml", true);
			}
			lang = YamlConfiguration.loadConfiguration(new File(getDataFolder() + File.separator + "lang.yml"));

			Bukkit.getOnlinePlayers().forEach(player -> {
				cameramanager.registerHandler(player);
			});
			Bukkit.getPluginManager().registerEvents(this, this);
			getLogger().log(Level.INFO, "Plugin enabeled!");
			if (!new File(getDataFolder() + File.separator + "noapi").exists()) {
				new CutsceneAPI(cameramanager);
				Cutscene.api = true;
				getLogger().log(Level.INFO, "API enabled!");
			}
		} else {
			getLogger().log(Level.WARNING, "Unsupported version!");
			getLogger().log(Level.WARNING, "Supported versions: 1.8.4(+4),1.9.4,1.10(+2),1.11(+2),1.12(+2),1.13.2,1.14(+3),1.15(+2),1.16.4(+1),1.17(+1),1.18.2");
			getServer().getPluginManager().disablePlugin(this);
		}
	}

	public void onDisable() {
		if (supported) {
			Bukkit.getOnlinePlayers().forEach(player -> {
				cameramanager.unregisterHandler(player);
			});
		}
	}

	@Override
	public ArrayList<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
		ArrayList<String> tabcomplete = new ArrayList<String>();
		if (command.getName().equalsIgnoreCase("playscene")) {
			if (sender instanceof Player) {
				Player player = (Player) sender;
				if (player.hasPermission("cutscene.playscene")) {
					if (args.length == 1) {
						for (String routename : routedata.getKeys(false)) {
							if (routename.startsWith(args[0]) && ((routedata.contains(routename + ".world") && routedata.isString(routename + ".world") && player.getWorld().getName().equals(routedata.getString(routename + ".world"))) || player.hasPermission("cutscene.playscene.other"))) {
								tabcomplete.add(routename);
							}
						}
					} else if (args.length == 2) {
						if ("normal".startsWith(args[1].toLowerCase()))
							tabcomplete.add("normal");
						if ("green".startsWith(args[1].toLowerCase()))
							tabcomplete.add("green");
						if ("negative".startsWith(args[1].toLowerCase()))
							tabcomplete.add("negative");
						if ("split".startsWith(args[1].toLowerCase()))
							tabcomplete.add("split");
					} else if (args.length == 3) {
						if (player.hasPermission("cutscene.playscene.other")) {
							for (Player tcplayer : Bukkit.getOnlinePlayers()) {
								if (!player.getUniqueId().equals(tcplayer.getUniqueId()) && tcplayer.getName().toLowerCase().startsWith(args[2].toLowerCase())) tabcomplete.add(tcplayer.getName());
							}
						}
					}
				}
			} else {
				if (args.length == 1) {
					for (String routename : routedata.getKeys(false)) {
						if (routename.startsWith(args[0])) tabcomplete.add(routename);
					}
				} else if (args.length == 2) {
					if ("normal".startsWith(args[1].toLowerCase()))
						tabcomplete.add("normal");
					if ("green".startsWith(args[1].toLowerCase()))
						tabcomplete.add("green");
					if ("negative".startsWith(args[1].toLowerCase()))
						tabcomplete.add("negative");
					if ("split".startsWith(args[1].toLowerCase()))
						tabcomplete.add("split");
				} else if (args.length == 3) {
					for (Player tcplayer : Bukkit.getOnlinePlayers()) {
						if (tcplayer.getName().toLowerCase().startsWith(args[2].toLowerCase())) tabcomplete.add(tcplayer.getName());
					}
				}
			}

		}
		return tabcomplete;
	};

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if (command.getName().equalsIgnoreCase("playscene")) {
			if (sender instanceof Player) {
				Player player = (Player) sender;
				if (player.hasPermission("cutscene.playscene")) {
					Player targetplayer = player;
					if (args.length > 1) {
						String routename = args[0];
						String cameratype = args[1].toLowerCase();
						CameraType type = CameraType.NOT_SET;
						switch (cameratype) {
						case "normal":
							type = CameraType.NORMAL;
							break;
						case "green":
							type = CameraType.GREEN;
							break;
						case "negative":
							type = CameraType.NEGATIVE;
							break;
						case "split":
							type = CameraType.SPLIT;
							break;
						}
						if (args.length == 3) {
							if (player.hasPermission("cutscene.playscene.other")) {
								targetplayer = Bukkit.getPlayerExact(args[2]);
								if (targetplayer == null) {
									String msgplayeroffline = lang.getString(getLocale(player) + ".playeroffline", lang.getString("default.playeroffline", "")).replace("%player%", args[2]);
									if (!msgplayeroffline.isEmpty()) player.sendMessage(msgplayeroffline);
									return true;
								}
							} else {
								String msgnopermissionother = lang.getString(getLocale(player) + ".nopermissionother", lang.getString("default.nopermissionother", ""));
								if (!msgnopermissionother.isEmpty()) player.sendMessage(msgnopermissionother);
								return true;
							}
						}
						if (cameramanager.contains(targetplayer.getUniqueId())) {
							String msgalreadyplayingscene = lang.getString(getLocale(player) + ".alreadyplayingscene", lang.getString("default.alreadyplayingscene", ""));
							if (!msgalreadyplayingscene.isEmpty()) player.sendMessage(msgalreadyplayingscene);
						} else {
							boolean ok = false;
							if (routedata.getKeys(false).contains(routename)) {
								try {
									Route route = new Route(routename,targetplayer.getEyeLocation());
									if(route.isValid() && route.getWorld().equals(targetplayer.getWorld())) {
										if (type.equals(CameraType.NOT_SET)) {
											String msgunknowncameratype = lang.getString(getLocale(player) + ".unknowncameratype", lang.getString("default.unknowncameratype", ""));
											if (!msgunknowncameratype.isEmpty()) player.sendMessage(msgunknowncameratype);
											ok = true;
										} else {
											cameramanager.startroute(targetplayer, route, type);
											String msgplayingscene = lang.getString(getLocale(player) + ".playingscene", lang.getString("default.playingscene", ""));
											if (!msgplayingscene.isEmpty()) player.sendMessage(msgplayingscene);
											ok = true;
										}
									} else {
										if(!player.getUniqueId().equals(targetplayer.getUniqueId())) {
											String msgplayerinanotherworld = lang.getString(getLocale(player) + ".playerinanotherworld", lang.getString("default.playerinanotherworld", ""));
											if (!msgplayerinanotherworld.isEmpty()) player.sendMessage(msgplayerinanotherworld);
											ok = true;
										}
									}
								} catch (IllegalArgumentException e) {
								}
							}
							if (!ok) {
								String msgunknownroute = lang.getString(getLocale(player) + ".unknownroute", lang.getString("default.unknownroute", ""));
								if (!msgunknownroute.isEmpty()) player.sendMessage(msgunknownroute);
							}
						}
					} else {
						String msgplayscenecommandhelp = lang.getString(getLocale(player) + ".playscenecommandhelp", lang.getString("default.playscenecommandhelp", ""));
						if (!msgplayscenecommandhelp.isEmpty()) player.sendMessage(msgplayscenecommandhelp);
					}
				} else {
					String msgnopermission = lang.getString(getLocale(player) + ".nopermission", lang.getString("default.nopermission", ""));
					if (!msgnopermission.isEmpty()) player.sendMessage(msgnopermission);
				}
			} else {
				if (args.length == 3) {
					Player targetplayer = Bukkit.getPlayerExact(args[2]);
					if (targetplayer != null) {
						if (!cameramanager.contains(targetplayer.getUniqueId())) {
							String routename = args[0];
							String cameratype = args[1].toLowerCase();
							CameraType type = CameraType.NOT_SET;
							switch (cameratype) {
							case "normal":
								type = CameraType.NORMAL;
								break;
							case "green":
								type = CameraType.GREEN;
								break;
							case "negative":
								type = CameraType.NEGATIVE;
								break;
							case "split":
								type = CameraType.SPLIT;
								break;
							}
							boolean ok = false;
							if (routedata.getKeys(false).contains(routename)) {
								try {
									Route route = new Route(routename,targetplayer.getEyeLocation());
									if(route.isValid() && route.getWorld().equals(targetplayer.getWorld())) {
										if (!type.equals(CameraType.NOT_SET)) {
											cameramanager.startroute(targetplayer, route, type);
											sender.sendMessage("Start cutscene");
											ok = true;
										} else {
											sender.sendMessage("Unknown camera type");
											ok = true;
										}
									} else {
										sender.sendMessage("Target player in another world");
										ok = true;
									}
								} catch (IllegalArgumentException e) {
								}
							}
							if (!ok) sender.sendMessage("Unknown route");

						} else sender.sendMessage("Scene already playing");
					} else sender.sendMessage("Player '" + args[2] + "' cannot be found");
				} else sender.sendMessage("Usage: /playscene <route> <camera> <player>");
			}
		}
		return true;
	};

	protected static void saveRoutes() {
		try {
			Cutscene.routedata.save(routefile);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	protected String getLocale(Player player) {
		switch (Bukkit.getServer().getClass().getPackage().getName().substring(23)) {
		case "v1_18_R2":
			return ((org.bukkit.craftbukkit.v1_18_R2.entity.CraftPlayer) player).getHandle().locale.toLowerCase();
		case "v1_17_R1":
			return ((org.bukkit.craftbukkit.v1_17_R1.entity.CraftPlayer) player).getHandle().locale.toLowerCase();
		case "v1_16_R3":
			return ((org.bukkit.craftbukkit.v1_16_R3.entity.CraftPlayer) player).getHandle().locale.toLowerCase();
		case "v1_15_R1":
			return ((org.bukkit.craftbukkit.v1_15_R1.entity.CraftPlayer) player).getHandle().locale.toLowerCase();
		case "v1_14_R1":
			return ((org.bukkit.craftbukkit.v1_14_R1.entity.CraftPlayer) player).getHandle().locale.toLowerCase();
		case "v1_13_R2":
			return ((org.bukkit.craftbukkit.v1_13_R2.entity.CraftPlayer) player).getHandle().locale.toLowerCase();
		case "v1_12_R1":
			return ((org.bukkit.craftbukkit.v1_12_R1.entity.CraftPlayer) player).getHandle().locale.toLowerCase();
		case "v1_11_R1":
			return ((org.bukkit.craftbukkit.v1_11_R1.entity.CraftPlayer) player).getHandle().locale.toLowerCase();
		case "v1_10_R1":
			return ((org.bukkit.craftbukkit.v1_10_R1.entity.CraftPlayer) player).getHandle().locale.toLowerCase();
		case "v1_9_R2":
			return ((org.bukkit.craftbukkit.v1_9_R2.entity.CraftPlayer) player).getHandle().locale.toLowerCase();
		case "v1_8_R3":
			return ((org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer) player).getHandle().locale.toLowerCase();
		default:
			return "";
		}
	}

	@EventHandler
	public void onJoin(PlayerJoinEvent e) {
		cameramanager.registerHandler(e.getPlayer());
	}

	@EventHandler
	public void onQuit(PlayerQuitEvent e) {
		cameramanager.unregisterHandler(e.getPlayer());
		cameramanager.endroute(e.getPlayer().getUniqueId());
	}

}
