package net.zyuiop.openUHC.spectators;

import java.util.ArrayList;
import java.util.List;

import net.zyuiop.openUHC.OpenUHC;

import org.bukkit.GameMode;
import org.bukkit.entity.Player;

public class SpectatorManager {
	protected final OpenUHC pl;
	protected List<Player> spectators = new ArrayList<Player>();
	
	/**
	 * 
	 * @param pl OpenUHC plugin
	 */
	public SpectatorManager(OpenUHC pl) {
		this.pl = pl;
	}
	/**
	 * Add a player to spectators
	 * @param p Player to be added
	 */
	public void addPlayer(Player p) {
		if (!isSpectator(p))
			spectators.add(p);
		p.setGameMode(GameMode.ADVENTURE);
		p.setAllowFlight(true);
		p.setHealth(p.getMaxHealth());
		p.setFoodLevel(20);
		for (Player player : pl.getServer().getOnlinePlayers()) {
			if (!isSpectator(player)) {
				player.hidePlayer(p);
			}
		}
	}
	
	/**
	 * Check whether the player is a spectator
	 * @param p The player to check
	 * @return 
	 */
	public boolean isSpectator(Player p) {
		return spectators.contains(p);
	}
}
