package net.zyuiop.openUHC.events;

import net.zyuiop.openUHC.teams.UHTeam;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

/**
 * Event fired when the games end
 * @author zyuiop
 *
 */
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
	private static final HandlerList handlers = new HandlerList();
	@Override
	public HandlerList getHandlers() {
		return handlers;
	}
	
	/**
	 * Return the winner object
	 * @return winner, can be an UHTeam or a String which is the name of the player
	 */
	public Object getWinner() {
		return winner;
	}
	
	public boolean isInTeams() {
		return team;
	}

}
