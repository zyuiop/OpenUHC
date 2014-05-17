package net.zyuiop.openUHC.commands;

import net.zyuiop.openUHC.OpenUHC;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class CommandGamestart implements CommandExecutor {
	
	private OpenUHC pl;
	public CommandGamestart(OpenUHC pl) {
		this.pl = pl;
	}

	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if (pl.getGame().canJoin() == false) {
			sender.sendMessage(pl.localize("game_already_started"));
			return true;
		}
		if(pl.getServer().getOnlinePlayers().length < 2) {
			sender.sendMessage(pl.localize("not_enough_players"));
			return true;
		}
		pl.getGame().start();
		return true;
	}

}
