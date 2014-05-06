package me.zyuiop.uhplugin;

import java.util.ArrayList;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class UHCommands implements CommandExecutor {

	private UHPlugin pl = null;
	
	public UHCommands(UHPlugin p) {
		pl = p;
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		
		if (sender instanceof Player) {
			Player p = (Player) sender;
			if (command.getName().equalsIgnoreCase("teams")) {
				if (pl.canJoin() == false) {
					p.sendMessage(ChatColor.RED+"La partie est dèjà en cours.");
					return true;
				}
				if (args.length < 1) {
					return false;
				}
				if (args[0].equalsIgnoreCase("ajouter") && args.length == 2) {
					if (!pl.addTeam(args[1]))
						p.sendMessage(ChatColor.RED+"Cette équipe existe déjà.");
					else
						p.sendMessage(ChatColor.GREEN+"L'équipe a bien été créée.");
					return true;
				}
			} else if (command.getName().equalsIgnoreCase("players")) {
				if (pl.canJoin() == false) {
					p.sendMessage(ChatColor.RED+"La partie est dèjà en cours.");
					return true;
				}
				if (args.length < 1) {
					return false;
				}
				if (args[0].equalsIgnoreCase("ajouter") && args.length == 3) {
					if (!pl.addPlayer(args[1], args[2]))
						p.sendMessage(ChatColor.RED+"Le joueur n'a pas été ajouté : il est déjà ajouté a une équipe ou la team n'existe pas.");
					else
						p.sendMessage(ChatColor.GREEN+"Le joueur a bien été ajouté.");
					return true;
				}
				if (args[0].equalsIgnoreCase("supprimer") && args.length == 3) {
					if (!pl.delFromTeam(args[2], args[1]))
						p.sendMessage(ChatColor.RED+"Le joueur n'a pas été supprimé de l'équipe car il n'en fait pas partie");
					else
						p.sendMessage(ChatColor.GREEN+"Le joueur a bien été supprimé de l'équipe.");
					return true;
				}
			} else if (command.getName().equalsIgnoreCase("gamestart")) {
				if (pl.canJoin() == false) {
					p.sendMessage(ChatColor.RED+"La partie est dèjà en cours.");
					return true;
				}
				if(pl.getServer().getOnlinePlayers().length < 2) {
					p.sendMessage(ChatColor.RED + "Il doit y avoir au moins 2 joueurs !");
					return true;
				}
				pl.startGame();
				return true;
			} else if (command.getName().equalsIgnoreCase("retrecir")) {
				if (args.length < 1) {
					return false;
				}
				Integer size = null;
				try {
					size = Integer.parseInt(args[0]);
				} catch (Exception e) {
					p.sendMessage(ChatColor.RED+"La taille n'est pas valide.");
					return true;
				}
				pl.retrecirCount(size);
				return true;
			} else if (command.getName().equalsIgnoreCase("limites")) {
				if (!pl.getStarted()) {
					p.sendMessage(ChatColor.RED+"La partie n'a pas démarré.");
					return true;
				}
				ArrayList<Integer> nlimits = pl.getLimits();
				p.sendMessage(ChatColor.GOLD+"Limites de la map : x("+nlimits.get(0)+","+nlimits.get(1)+") z("+nlimits.get(2)+","+nlimits.get(3)+")");
				return true;
			}

			
		} else {
			sender.sendMessage(ChatColor.RED + "Erreur : seuls les joueurs peuvent utiliser les commandes.");
			return true;
		}
		
		return false;
	}

}
