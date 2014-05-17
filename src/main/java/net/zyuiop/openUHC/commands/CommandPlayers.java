package net.zyuiop.openUHC.commands;

import net.zyuiop.openUHC.OpenUHC;

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
		
		if (sender instanceof Player && args[0].equals("list")) {
			Player p = (Player) sender;
			if (!pl.isIngame(p))
				pl.getSpectatorManager().playersInventory((Player) sender);
			else
				sender.sendMessage(pl.localize("not_a_spectator"));
			return true;
		}
		
		if (pl.getGame().canJoin() == false) {
			sender.sendMessage(pl.localize("game_already_started"));
			return true;
		}
		
		if (args[0].equalsIgnoreCase("add") && args.length == 3) {
			if (!pl.addPlayer(args[1], pl.getServer().getPlayer(args[2])))
				sender.sendMessage(pl.localize("already_in_team"));
			else
				sender.sendMessage(pl.localize("added_in_team"));
			return true;
		}
		if (args[0].equalsIgnoreCase("delete") && args.length == 3) {
			if (!pl.delFromTeam(pl.getServer().getPlayer(args[2]), args[1]))
				sender.sendMessage(pl.localize("not_in_team"));
			else
				sender.sendMessage(pl.localize("deleted_from_team"));
			return true;
		}
		return false;
	}

}
