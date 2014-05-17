package net.zyuiop.openUHC.commands;

import net.zyuiop.openUHC.OpenUHC;
import net.zyuiop.openUHC.teams.UHTeam;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class CommandTeams implements CommandExecutor {
	
	private OpenUHC pl;
	public CommandTeams(OpenUHC pl) {
		this.pl = pl;
	}

	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if (args.length < 1) {
			return false;
		}
		if (args[0].equalsIgnoreCase("add") && args.length == 2) {
			if (pl.getGame().canJoin() == false) {
				sender.sendMessage(pl.localize("game_already_started"));
				return true;
			}
			if (!pl.teamManager().registerTeam(args[1]))
				sender.sendMessage(pl.localize("team_already_exist"));
			else
				sender.sendMessage(pl.localize("team_created"));
			return true;
		} else if (args[0].equalsIgnoreCase("delete") && args.length == 2) {
			if (pl.getGame().canJoin() == false) {
				sender.sendMessage(pl.localize("game_already_started"));
				return true;
			}
			if (!pl.teamManager().deleteTeam(args[1]))
				sender.sendMessage(pl.localize("team_not_exist"));
			else
				sender.sendMessage(pl.localize("team_deleted"));
			return true;
		} else if (args[0].equalsIgnoreCase("list")) {
			if (args.length == 2) {
				if (!pl.teamManager().teamExists(args[1]))
					sender.sendMessage(pl.localize("team_not_exist"));
				
				String joueurs = "";
				boolean f = true;
				for (String j : pl.teamManager().getTeam(args[1]).getPlayers()) {
					if (f == true) {
						joueurs=ChatColor.AQUA+j;
						f = false;
					} else
						joueurs+=ChatColor.WHITE+", "+ChatColor.AQUA+j;
				}
				sender.sendMessage(pl.localize("team_players_message").replace("{TEAM}", pl.teamManager().getTeam(args[1]).getColorizedName()+ChatColor.GOLD)+" : "+joueurs);
				return true;
			} else {
				sender.sendMessage(pl.localize("teams_listing_message"));
				for (UHTeam t : pl.teamManager().getTeamsList()) {
					String joueurs = "";
					boolean f = true;
					for (String j : t.getPlayers()) {
						if (f == true) {
							joueurs=ChatColor.AQUA+j;
							f = false;
						} else
							joueurs+=ChatColor.WHITE+", "+ChatColor.AQUA+j;
					}
					sender.sendMessage(ChatColor.GOLD+"- "+t.getColorizedName()+ChatColor.WHITE+", "+pl.localize("teams_listing_players")+" : "+joueurs);
				}
				return true;
			}
		} else {
			return false;
		}
	}

}
