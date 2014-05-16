package net.zyuiop.openUHC.listeners;

import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.server.ServerListPingEvent;

import net.zyuiop.openUHC.OpenUHC;

public class NetworkEvents implements Listener {
	private OpenUHC pl;
	public NetworkEvents(OpenUHC plugin) {
		this.pl = plugin;
	}
	
	@EventHandler 
	public void onPing(ServerListPingEvent e) {
		if (pl.getGame().canJoin()) {
			e.setMotd(ChatColor.GREEN+"En attente de joueurs.");
		} else if (pl.getGame().isFinished()) {
			if (pl.getGame().isSolo()) {
				e.setMotd(ChatColor.GREEN+"Partie gagnée par "+ChatColor.AQUA+pl.getGame().getWinner());
			} else {
				e.setMotd(ChatColor.GREEN+"Partie gagnée par l'équipe "+pl.teamManager().getTeam(pl.getGame().getWinner()).getColorizedName());
			}
		} else {
			e.setMotd(ChatColor.RED+"Partie en cours");
		}
	}
	
}
