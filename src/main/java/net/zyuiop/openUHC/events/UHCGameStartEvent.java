package net.zyuiop.openUHC.events;

import net.zyuiop.openUHC.Game;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

/**
 * Event fired when the game starts
 * @author gnomino
 *
 */
public class UHCGameStartEvent extends Event {
	private final Game game;
	public UHCGameStartEvent(Game game) {
		this.game = game;
	}
	private static final HandlerList handlers = new HandlerList();
	@Override
	public HandlerList getHandlers() {
		return handlers;
	}
	public static HandlerList getHandlerList() {
        return handlers;
    }
	public Game getGame() {
		return game;
	}
}
