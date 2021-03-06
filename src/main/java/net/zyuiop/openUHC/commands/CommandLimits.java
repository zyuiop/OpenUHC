package net.zyuiop.openUHC.commands;

import java.util.ArrayList;

import net.zyuiop.openUHC.OpenUHC;

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
			sender.sendMessage(pl.localize("game_already_started"));
			return true;
		}
		ArrayList<Integer> nlimits = pl.getLimits();
		sender.sendMessage(pl.localize("limits_message").replace("{LIMITS}", "x("+nlimits.get(0)+","+nlimits.get(1)+") z("+nlimits.get(2)+","+nlimits.get(3)+")"));
		
		return true;
	}

}
