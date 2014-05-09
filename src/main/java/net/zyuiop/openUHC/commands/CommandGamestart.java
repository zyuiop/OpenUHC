package net.zyuiop.openUHC.commands;

import net.zyuiop.openUHC.Game;
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
		if (pl.canJoin() == false) {
			sender.sendMessage(ChatColor.RED+"La partie est dèjà en cours.");
			return true;
		}
		if(pl.getServer().getOnlinePlayers().length < 2) {
			sender.sendMessage(ChatColor.RED + "Il doit y avoir au moins 2 joueurs !");
			return true;
		}
		pl.getGame().start();
		return true;
	}

}
