package net.zyuiop.openUHC.events;

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
		if (pl.canJoin()) {
			e.setMotd(ChatColor.GREEN+"En attente de joueurs.");
		} else if (pl.isFinished()) {
			if (pl.isSolo()) {
				e.setMotd(ChatColor.GREEN+"Partie gagnée par "+ChatColor.AQUA+pl.getWinner());
			} else {
				e.setMotd(ChatColor.GREEN+"Partie gagnée par l'équipe "+pl.teamManager().getTeam(pl.getWinner()).getColorizedName());
			}
		} else {
			e.setMotd(ChatColor.RED+"Partie en cours");
		}
	}
	
}
