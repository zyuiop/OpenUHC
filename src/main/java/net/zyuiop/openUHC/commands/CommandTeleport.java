package net.zyuiop.openUHC.commands;

import net.zyuiop.openUHC.OpenUHC;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CommandTeleport implements CommandExecutor {
	private final OpenUHC pl;
	public CommandTeleport(OpenUHC pl) {
		this.pl = pl;
	}
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if (!(sender instanceof Player)) {
			sender.sendMessage(ChatColor.RED + "You aren't a player !");
			return true;
		}
		Player p = (Player) sender;
		if (!pl.getSpectatorManager().isSpectator(p) && !p.hasPermission("uhpl.cheat.teleport")) {
			sender.sendMessage(pl.localize("not_a_spectator"));
			return true;
		}
		if (args.length >= 1) {
			Player target = Bukkit.getServer().getPlayer(args[0]);
			if (target == null) {
				sender.sendMessage(ChatColor.RED + "Could not found specified player.");
				return true;
			}
			p.teleport(target.getLocation());
			return true;
		}
		return false;
	}
}
