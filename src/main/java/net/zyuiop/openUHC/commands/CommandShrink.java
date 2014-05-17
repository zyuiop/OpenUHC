package net.zyuiop.openUHC.commands;

import net.zyuiop.openUHC.OpenUHC;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class CommandShrink implements CommandExecutor {
	
	private OpenUHC pl;
	public CommandShrink(OpenUHC pl) {
		this.pl = pl;
	}

	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if (args.length < 1) {
			return false;
		}
		Integer size = null;
		try {
			size = Integer.parseInt(args[0]);
		} catch (Exception e) {
			sender.sendMessage(pl.localize("size_not_valid"));
			return true;
		}
		pl.retrecirCount(size);
		return true;
	}

}
