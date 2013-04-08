package com.bukkthat.disablecommands;

import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

public class DisableCommands extends JavaPlugin implements Listener {

	int height;

	public void onEnable() {
		PluginManager pm = getServer().getPluginManager();
		pm.registerEvents(this, this);
		saveDefaultConfig();
		height = getConfig().getInt("height", 30);
	}

	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if(args.length == 0) {
			return false;
		} else if(args.length == 1) {
			if(args[0].equalsIgnoreCase("list")) {
				if(sender.hasPermission("disablecommands.list")) {
					sender.sendMessage(ChatColor.GOLD + "Forbidden Commands");
					for(String command:getConfig().getStringList("forbidden-commands")) {
						sender.sendMessage(ChatColor.GOLD + " - " + command);
					}
					return true;
				} else {
					sender.sendMessage(ChatColor.RED + "You do not have permission to do that!");
					return true;
				}
			} else {
				return false;
			}
		} else if(args.length == 2) {
			if(args[0].equalsIgnoreCase("add")) {
				if(sender.hasPermission("disablecommands.add")) {
					List<String> forbiddenCommands = getConfig().getStringList("forbidden-commands");
					if(forbiddenCommands.contains(args[1].toLowerCase())) {
						sender.sendMessage(ChatColor.RED + "That command is already forbidden!");
						return true;
					}
					forbiddenCommands.add(args[1].toLowerCase());
					getConfig().set("forbidden-commands", forbiddenCommands);
					saveConfig();
					sender.sendMessage(ChatColor.GREEN + "Added " + ChatColor.AQUA + args[1].toLowerCase() + ChatColor.GREEN + " to the forbidden commands list!");
					return true;
				} else {
					sender.sendMessage(ChatColor.RED + "You do not have permission to do that!");
					return true;
				}
			} else if(args[0].equalsIgnoreCase("remove")) {
				if(sender.hasPermission("disablecommands.remove")) {
					List<String> forbiddenCommands = getConfig().getStringList("forbidden-commands");
					if(!forbiddenCommands.contains(args[1].toLowerCase())) {
						sender.sendMessage(ChatColor.RED + "That command is not forbidden!");
						return true;
					}
					forbiddenCommands.remove(args[1].toLowerCase());
					getConfig().set("forbidden-commands", forbiddenCommands);
					saveConfig();
					sender.sendMessage(ChatColor.GREEN + "Removed " + ChatColor.AQUA + args[1].toLowerCase() + ChatColor.GREEN + " from the forbidden commands list!");
					return true;
				} else {
					sender.sendMessage(ChatColor.RED + "You do not have permission to do that!");
					return true;
				}
			} else {
				return false;
			}
		} else {
			return false;
		}
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onPreprocess(PlayerCommandPreprocessEvent event) {
		String command = event.getMessage().substring(1);
		if(event.getPlayer().getLocation().getBlockY() < height) {
			if(!event.getPlayer().hasPermission("disablecommands.bypass")) {
				List<String> forbiddenCommands = getConfig().getStringList("forbidden-commands");
				for(String forbiddenCommand:forbiddenCommands) {
					if(command.startsWith(forbiddenCommand)) {
						event.setCancelled(true);
						event.getPlayer().sendMessage(ChatColor.RED + "That command is not allowed below Y" + height + "!");
						break;
					}
				}
			}
		}
	}

}
