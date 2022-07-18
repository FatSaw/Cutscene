package me.bomb.cutscene;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class PlaysceneCommand implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if (sender instanceof Player) {
			Player player = (Player) sender;
			if (player.hasPermission("cutscene.playscene") || player.hasPermission("cutscene.playscene.other")) {
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
								String msgplayeroffline = Cutscene.lang.getString(getLocale(player) + ".playeroffline",Cutscene.lang.getString("default.playeroffline", "")).replace("%player%", args[2]);
								if (!msgplayeroffline.isEmpty()) player.sendMessage(msgplayeroffline);
								return true;
							}
						} else {
							String msgnopermissionother = Cutscene.lang.getString(getLocale(player) + ".nopermissionother",Cutscene.lang.getString("default.nopermissionother", ""));
							if (!msgnopermissionother.isEmpty())
								player.sendMessage(msgnopermissionother);
							return true;
						}
					}
					if (CutsceneAPI.cameramanager.contains(targetplayer.getUniqueId())) {
						String msgalreadyplayingscene = Cutscene.lang.getString(getLocale(player) + ".alreadyplayingscene",Cutscene.lang.getString("default.alreadyplayingscene", ""));
						if (!msgalreadyplayingscene.isEmpty())
							player.sendMessage(msgalreadyplayingscene);
					} else {
						boolean ok = false;
						if (Cutscene.routedata.getKeys(false).contains(routename)) {
							try {
								Route route = new Route(routename, targetplayer.getEyeLocation());
								if (route.isValid() && route.getWorld().equals(targetplayer.getWorld())) {
									if (type.equals(CameraType.NOT_SET)) {
										String msgunknowncameratype = Cutscene.lang.getString(getLocale(player) + ".unknowncameratype",Cutscene.lang.getString("default.unknowncameratype", ""));
										if (!msgunknowncameratype.isEmpty())
											player.sendMessage(msgunknowncameratype);
										ok = true;
									} else {
										CutsceneAPI.cameramanager.startroute(targetplayer, route, type);
										String msgplayingscene = Cutscene.lang.getString(getLocale(player) + ".playingscene",Cutscene.lang.getString("default.playingscene", ""));
										if (!msgplayingscene.isEmpty())
											player.sendMessage(msgplayingscene);
										ok = true;
									}
								} else {
									if (!player.getUniqueId().equals(targetplayer.getUniqueId())) {
										String msgplayerinanotherworld = Cutscene.lang.getString(getLocale(player) + ".playerinanotherworld",Cutscene.lang.getString("default.playerinanotherworld", ""));
										if (!msgplayerinanotherworld.isEmpty())
											player.sendMessage(msgplayerinanotherworld);
										ok = true;
									}
								}
							} catch (IllegalArgumentException e) {
							}
						}
						if (!ok) {
							String msgunknownroute = Cutscene.lang.getString(getLocale(player) + ".unknownroute",Cutscene.lang.getString("default.unknownroute", ""));
							if (!msgunknownroute.isEmpty())
								player.sendMessage(msgunknownroute);
						}
					}
				} else {
					String msgplayscenecommandhelp = Cutscene.lang.getString(getLocale(player) + ".playscenecommandhelp",Cutscene.lang.getString("default.playscenecommandhelp", ""));
					if (!msgplayscenecommandhelp.isEmpty())
						player.sendMessage(msgplayscenecommandhelp);
				}
			} else {
				String msgnopermission = Cutscene.lang.getString(getLocale(player) + ".nopermission",Cutscene.lang.getString("default.nopermission", ""));
				if (!msgnopermission.isEmpty())
					player.sendMessage(msgnopermission);
			}
		} else {
			if (args.length == 3) {
				Player targetplayer = Bukkit.getPlayerExact(args[2]);
				if (targetplayer != null) {
					if (!CutsceneAPI.cameramanager.contains(targetplayer.getUniqueId())) {
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
						if (Cutscene.routedata.getKeys(false).contains(routename)) {
							try {
								Route route = new Route(routename, targetplayer.getEyeLocation());
								if (route.isValid() && route.getWorld().equals(targetplayer.getWorld())) {
									if (!type.equals(CameraType.NOT_SET)) {
										CutsceneAPI.cameramanager.startroute(targetplayer, route, type);
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
	
	private static String getLocale(Player player) {
		switch (Bukkit.getServer().getClass().getPackage().getName().substring(23)) {
		case "v1_19_R1":
			return ((org.bukkit.craftbukkit.v1_19_R1.entity.CraftPlayer) player).getHandle().locale.toLowerCase();
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
	
}