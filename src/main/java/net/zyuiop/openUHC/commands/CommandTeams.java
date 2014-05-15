package net.zyuiop.openUHC.commands;

import net.zyuiop.openUHC.OpenUHC;
import net.zyuiop.openUHC.teams.UHTeam;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

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
				sender.sendMessage(ChatColor.RED+"La partie est dèjà en cours.");
				return true;
			}
			if (!pl.teamManager().registerTeam(args[1]))
				sender.sendMessage(ChatColor.RED+"Cette équipe existe déjà.");
			else
				sender.sendMessage(ChatColor.GREEN+"L'équipe a bien été créée.");
			return true;
		} else if (args[0].equalsIgnoreCase("delete") && args.length == 2) {
			if (pl.getGame().canJoin() == false) {
				sender.sendMessage(ChatColor.RED+"La partie est dèjà en cours.");
				return true;
			}
			if (!pl.teamManager().deleteTeam(args[1]))
				sender.sendMessage(ChatColor.RED+"L'équipe n'a pas pu être supprimée, elle n'existe probablement pas.");
			else
				sender.sendMessage(ChatColor.GREEN+"L'équipe a bien été suppriméée.");
			return true;
		} else if (args[0].equalsIgnoreCase("list")) {
			if (args.length == 2) {
				if (!pl.teamManager().teamExists(args[1]))
					sender.sendMessage(ChatColor.RED+"L'équipe n'existe pas.");
				
				String joueurs = "";
				boolean f = true;
				for (Player j : pl.teamManager().getTeam(args[1]).getPlayers()) {
					if (f == true) {
						joueurs=ChatColor.AQUA+j.getDisplayName();
						f = false;
					} else
						joueurs+=ChatColor.WHITE+", "+ChatColor.AQUA+j.getDisplayName();
				}
				sender.sendMessage(ChatColor.GOLD+"Voici les joueurs de l'équipe "+pl.teamManager().getTeam(args[1]).getColorizedName()+ChatColor.GOLD+" : "+joueurs);
				return true;
			} else {
				sender.sendMessage(ChatColor.GOLD+"Voici les équipes :");
				for (UHTeam t : pl.teamManager().getTeamsList()) {
					String joueurs = "";
					boolean f = true;
					for (Player j : t.getPlayers()) {
						if (f == true) {
							joueurs=ChatColor.AQUA+j.getDisplayName();
							f = false;
						} else
							joueurs+=ChatColor.WHITE+", "+ChatColor.AQUA+j.getDisplayName();
					}
					sender.sendMessage(ChatColor.GOLD+"- "+t.getColorizedName()+ChatColor.WHITE+", contenant les joueurs suivants : "+joueurs);
				}
				return true;
			}
		} else {
			return false;
		}
	}

}
