package net.zyuiop.openUHC.commands;

import net.zyuiop.openUHC.OpenUHC;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CommandPlayers implements CommandExecutor {
	
	private OpenUHC pl;
	public CommandPlayers(OpenUHC pl) {
		this.pl = pl;
	}

	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if (args.length < 1) {
			return false;
		}
		
		if (args[0].equals("inv")) {
			pl.getSpectatorManager().playersInventory((Player) sender);
			return true;
		}
		
		if (pl.getGame().canJoin() == false) {
			sender.sendMessage(ChatColor.RED+"La partie est dèjà en cours.");
			return true;
		}
		
		if (args[0].equalsIgnoreCase("add") && args.length == 3) {
			if (!pl.addPlayer(args[1], pl.getServer().getPlayer(args[2])))
				sender.sendMessage(ChatColor.RED+"Le joueur n'a pas été ajouté : il est déjà ajouté a une équipe ou la team n'existe pas.");
			else
				sender.sendMessage(ChatColor.GREEN+"Le joueur a bien été ajouté.");
			return true;
		}
		if (args[0].equalsIgnoreCase("delete") && args.length == 3) {
			if (!pl.delFromTeam(pl.getServer().getPlayer(args[2]), args[1]))
				sender.sendMessage(ChatColor.RED+"Le joueur n'a pas été supprimé de l'équipe car il n'en fait pas partie");
			else
				sender.sendMessage(ChatColor.GREEN+"Le joueur a bien été supprimé de l'équipe.");
			return true;
		}
		return false;
	}

}
