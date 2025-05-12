package me.bomb.cutscene.command;

import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

public final class PlaysceneTabCompleter implements TabCompleter {
	
	private final YamlConfiguration routedata;
	
	public PlaysceneTabCompleter(YamlConfiguration routedata) {
		this.routedata = routedata;
	}
	
	@Override
	public ArrayList<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
		ArrayList<String> tabcomplete = new ArrayList<String>();
		if (sender instanceof Player) {
			Player player = (Player) sender;
			if (player.hasPermission("cutscene.playscene") || player.hasPermission("cutscene.playscene.other")) {
				if (args.length == 1) {
					for (String routename : this.routedata.getKeys(false)) {
						if(routename.startsWith(args[0]) && (player.hasPermission("cutscene.playscene.other") || !this.routedata.isString(routename + ".world") || player.getWorld().getName().equals(routedata.getString(routename + ".world")))) {
							tabcomplete.add(routename);
						}
					}
				} else if (args.length == 2) {
					if ("normal".startsWith(args[1].toLowerCase())) tabcomplete.add("normal");
					if ("green".startsWith(args[1].toLowerCase())) tabcomplete.add("green");
					if ("negative".startsWith(args[1].toLowerCase())) tabcomplete.add("negative");
					if ("split".startsWith(args[1].toLowerCase())) tabcomplete.add("split");
				} else if (args.length == 3) {
					if (player.hasPermission("cutscene.playscene.other")) {
						for (Player tcplayer : Bukkit.getOnlinePlayers()) {
							if (!player.getUniqueId().equals(tcplayer.getUniqueId()) && tcplayer.getName().toLowerCase().startsWith(args[2].toLowerCase())) {
								tabcomplete.add(tcplayer.getName());
							}	
						}
					}
				}
			}
		} else {
			if (args.length == 1) {
				for (String routename : this.routedata.getKeys(false)) {
					if (routename.startsWith(args[0]))
						tabcomplete.add(routename);
				}
			} else if (args.length == 2) {
				if ("normal".startsWith(args[1].toLowerCase())) tabcomplete.add("normal");
				if ("green".startsWith(args[1].toLowerCase())) tabcomplete.add("green");
				if ("negative".startsWith(args[1].toLowerCase())) tabcomplete.add("negative");
				if ("split".startsWith(args[1].toLowerCase())) tabcomplete.add("split");
			} else if (args.length == 3) {
				for (Player tcplayer : Bukkit.getOnlinePlayers()) {
					if (tcplayer.getName().toLowerCase().startsWith(args[2].toLowerCase())) tabcomplete.add(tcplayer.getName());
				}
			}
		}
		return tabcomplete;
	};
	
}