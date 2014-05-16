package net.zyuiop.openUHC.events;

import net.zyuiop.openUHC.teams.UHTeam;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class UHCGameEnded extends Event {

	private Object winner;
	private boolean team = false;
	
	public UHCGameEnded(String winner) {
		this.winner = winner;
	}
	
	public UHCGameEnded(UHTeam winner) {
		this.winner = winner;
		this.team = true;
	}
	
	@Override
	public HandlerList getHandlers() {
		// TODO Auto-generated method stub
		return null;
	}
	
	public Object getWinner() {
		return winner;
	}
	
	public boolean isInTeams() {
		return team;
	}

}
