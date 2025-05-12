package me.bomb.cutscene.command;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import me.bomb.camerautil.CameraManager;
import me.bomb.camerautil.CameraType;
import me.bomb.camerautil.LocationPoint;
import me.bomb.cutscene.RouteExecutor;
import me.bomb.cutscene.route.Route;

public final class PlaysceneCommand implements CommandExecutor {
	
	private final CameraManager cameramanager;
	private final RouteExecutor routeexecutor;
	private final int version;
	private final YamlConfiguration lang, routedata;
	
	public PlaysceneCommand(CameraManager cameramanager, RouteExecutor routeexecutor, int version, YamlConfiguration lang, YamlConfiguration routedata) {
		this.cameramanager = cameramanager;
		this.routeexecutor = routeexecutor;
		this.version = version;
		this.lang = lang;
		this.routedata = routedata;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if (sender instanceof Player) {
			Player player = (Player) sender;
			if (player.hasPermission("cutscene.playscene") || player.hasPermission("cutscene.playscene.other")) {
				Player targetplayer = player;
				if (args.length > 1) {
					String routename = args[0];
					String cameratype = args[1].toLowerCase();
					CameraType type = null;
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
								String msgplayeroffline = this.lang.getString(getLocale(player) + ".playeroffline",this.lang.getString("default.playeroffline", "")).replace("%player%", args[2]);
								if (!msgplayeroffline.isEmpty()) player.sendMessage(msgplayeroffline);
								return true;
							}
						} else {
							String msgnopermissionother = this.lang.getString(getLocale(player) + ".nopermissionother",this.lang.getString("default.nopermissionother", ""));
							if (!msgnopermissionother.isEmpty())
								player.sendMessage(msgnopermissionother);
							return true;
						}
					}
					if (cameramanager.contains(targetplayer)) {
						String msgalreadyplayingscene = this.lang.getString(getLocale(player) + ".alreadyplayingscene",this.lang.getString("default.alreadyplayingscene", ""));
						if (!msgalreadyplayingscene.isEmpty())
							player.sendMessage(msgalreadyplayingscene);
					} else {
						boolean ok = false;
						if (this.routedata.getKeys(false).contains(routename)) {
							try {
								Location targeteyelocation = targetplayer.getEyeLocation();
								Route route = new Route(this.routedata, routename, new LocationPoint(targeteyelocation.getX(), targeteyelocation.getY(), targeteyelocation.getZ(), targeteyelocation.getYaw(), targeteyelocation.getPitch()));
								if (type==null) {
									String msgunknowncameratype = this.lang.getString(getLocale(player) + ".unknowncameratype",this.lang.getString("default.unknowncameratype", ""));
									if (!msgunknowncameratype.isEmpty())
										player.sendMessage(msgunknowncameratype);
									ok = true;
								} else {
									routeexecutor.put(targetplayer, route, type);
									String msgplayingscene = this.lang.getString(getLocale(player) + ".playingscene",this.lang.getString("default.playingscene", ""));
									if (!msgplayingscene.isEmpty())
										player.sendMessage(msgplayingscene);
									ok = true;
								}
							} catch (IllegalArgumentException e) {
							}
						}
						if (!ok) {
							String msgunknownroute = this.lang.getString(getLocale(player) + ".unknownroute",this.lang.getString("default.unknownroute", ""));
							if (!msgunknownroute.isEmpty())
								player.sendMessage(msgunknownroute);
						}
					}
				} else {
					String msgplayscenecommandhelp = this.lang.getString(getLocale(player) + ".playscenecommandhelp",this.lang.getString("default.playscenecommandhelp", ""));
					if (!msgplayscenecommandhelp.isEmpty())
						player.sendMessage(msgplayscenecommandhelp);
				}
			} else {
				String msgnopermission = this.lang.getString(getLocale(player) + ".nopermission",this.lang.getString("default.nopermission", ""));
				if (!msgnopermission.isEmpty())
					player.sendMessage(msgnopermission);
			}
		} else {
			if (args.length == 3) {
				Player targetplayer = Bukkit.getPlayerExact(args[2]);
				if (targetplayer != null) {
					if (!cameramanager.contains(targetplayer)) {
						String routename = args[0];
						String cameratype = args[1].toLowerCase();
						CameraType type = null;
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
						if (this.routedata.getKeys(false).contains(routename)) {
							try {
								Location targeteyelocation = targetplayer.getEyeLocation();
								Route route = new Route(this.routedata, routename, new LocationPoint(targeteyelocation.getX(), targeteyelocation.getY(), targeteyelocation.getZ(), targeteyelocation.getYaw(), targeteyelocation.getPitch()));
								if (type==null) {
									sender.sendMessage("Unknown camera type");
									ok = true;
								} else {
									routeexecutor.put(targetplayer, route, type);
									sender.sendMessage("Start cutscene");
									ok = true;
								}
							} catch (IllegalArgumentException e) {
							}
						}
						if (!ok)
							sender.sendMessage("Unknown route");

					} else
						sender.sendMessage("Scene already playing");
				} else
					sender.sendMessage("Player '" + args[2] + "' cannot be found");
			} else
				sender.sendMessage("Usage: /playscene <route> <camera> <player>");
		}
		return true;
	};
	
	private String getLocale(Player player) {
		switch (this.version) {
		case 19:
			return ((org.bukkit.craftbukkit.v1_19_R3.entity.CraftPlayer) player).getHandle().locale.toLowerCase();
		case 18:
			return ((org.bukkit.craftbukkit.v1_18_R2.entity.CraftPlayer) player).getHandle().locale.toLowerCase();
		case 17:
			return ((org.bukkit.craftbukkit.v1_17_R1.entity.CraftPlayer) player).getHandle().locale.toLowerCase();
		case 16:
			return ((org.bukkit.craftbukkit.v1_16_R3.entity.CraftPlayer) player).getHandle().locale.toLowerCase();
		case 15:
			return ((org.bukkit.craftbukkit.v1_15_R1.entity.CraftPlayer) player).getHandle().locale.toLowerCase();
		case 14:
			return ((org.bukkit.craftbukkit.v1_14_R1.entity.CraftPlayer) player).getHandle().locale.toLowerCase();
		case 13:
			return ((org.bukkit.craftbukkit.v1_13_R2.entity.CraftPlayer) player).getHandle().locale.toLowerCase();
		case 12:
			return ((org.bukkit.craftbukkit.v1_12_R1.entity.CraftPlayer) player).getHandle().locale.toLowerCase();
		case 11:
			return ((org.bukkit.craftbukkit.v1_11_R1.entity.CraftPlayer) player).getHandle().locale.toLowerCase();
		case 10:
			return ((org.bukkit.craftbukkit.v1_10_R1.entity.CraftPlayer) player).getHandle().locale.toLowerCase();
		case 9:
			return ((org.bukkit.craftbukkit.v1_9_R2.entity.CraftPlayer) player).getHandle().locale.toLowerCase();
		case 8:
			return ((org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer) player).getHandle().locale.toLowerCase();
		default:
			return "";
		}
	}
	
}