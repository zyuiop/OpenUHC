package net.zyuiop.openUHC.commands;

import java.util.ArrayList;

import net.zyuiop.openUHC.OpenUHC;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class CommandLimits implements CommandExecutor {
	
	private OpenUHC pl;
	public CommandLimits(OpenUHC pl) {
		this.pl = pl;
	}

	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if (!pl.getGame().getStarted()) {
			sender.sendMessage(ChatColor.RED+"La partie n'a pas démarré.");
			return true;
		}
		ArrayList<Integer> nlimits = pl.getLimits();
		sender.sendMessage(ChatColor.GOLD+"Limites de la map : x("+nlimits.get(0)+","+nlimits.get(1)+") z("+nlimits.get(2)+","+nlimits.get(3)+")");
		return true;
	}

}
