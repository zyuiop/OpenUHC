package net.zyuiop.openUHC.listeners;

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
			e.setMotd(pl.localize(pl.localize("game_waiting_motd")));
		} else if (pl.getGame().isFinished()) {
			if (pl.getGame().getWinner() == "") {
				e.setMotd(pl.localize("game_over_equality"));
			}
			else if (pl.getGame().isSolo()) {
				e.setMotd(pl.localize("game_over_player_won").replace("{PLAYER}", pl.getGame().getWinner()));
			} else {
				e.setMotd(pl.localize("game_over_team_won").replace("{TEAM}", pl.teamManager().getTeam(pl.getGame().getWinner()).getColorizedName()));
			}
		} else {
			e.setMotd(pl.localize("game_in_progress_motd"));
		}
	}
	
}
