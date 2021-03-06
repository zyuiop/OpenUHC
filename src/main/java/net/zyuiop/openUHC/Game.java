package net.zyuiop.openUHC;

import java.util.HashMap;

import net.zyuiop.openUHC.events.UHCGameEnded;
import net.zyuiop.openUHC.events.UHCGameStartEvent;
import net.zyuiop.openUHC.teams.UHTeam;
import net.zyuiop.openUHC.timers.Countdown;

import org.bukkit.Bukkit;
import org.bukkit.Difficulty;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public class Game {
	private OpenUHC pl;
	protected boolean isWon = false;
	protected String winner = null;
	protected boolean solo = true;	
	protected boolean canJoin = true;
	protected boolean degats = false;
	protected boolean gameStarted = false;
	
	public Game(OpenUHC pl) {
		this.pl = pl;
	}
	
	/**
	 * Use this method to start the game. This method shall only be used ONCE.
	 */
	public void start() {
		pl.getWorld().setGameRuleValue("doDaylightCycle", pl.getConfig().getString("daylight-cycle", "true"));
		pl.getWorld().setGameRuleValue("naturalRegeneration", "false");
		pl.getWorld().setTime(pl.getConfig().getLong("begin-time", 6000L));
		pl.getWorld().setStorm(pl.getConfig().getBoolean("begin-storm", false));
		pl.getWorld().setDifficulty(Difficulty.HARD);
		this.canJoin = false;
		if (pl.teams.size() >= 2)
			this.solo = false;
		else {	
			this.solo = true;
			for (Player p : Bukkit.getOnlinePlayers()) {
				pl.joueurs.add(p.getName());
			}
		}
		pl.sbmanager.init();
		pl.mapSize = pl.getConfig().getInt("map-size");
		pl.setLimits();
		pl.generateWalls();
		HashMap<Player, Location> posTp = new HashMap<Player, Location>();
		Bukkit.broadcastMessage(pl.localize("generating_chunks"));
		if (this.solo) {
			for (String p : pl.joueurs) {
				Location l = pl.getRandLoc();
				pl.generateChunk(l.getChunk());
				Player play = (Player) Bukkit.getOfflinePlayer(p);
				posTp.put(play, l);
			}
		}
		else {
			for (UHTeam t : pl.teams.getTeamsList()) {
				Location l = pl.getRandLoc();
				pl.generateChunk(l.getChunk());
				
				for (String p : t.getPlayers()) {
					Player play = (Player) Bukkit.getOfflinePlayer(p);
					posTp.put(play, l);
				}
			}
		}
		Bukkit.broadcastMessage(pl.localize("chunks_ended"));
		for (Player pl : posTp.keySet()) {
			pl.setGameMode(GameMode.SURVIVAL);
			pl.getInventory().clear();
			pl.closeInventory();
			pl.setLevel(0);
			pl.setExp(0);
			pl.setHealth(20);
			pl.setFoodLevel(20);
			pl.setFlying(false);
			pl.teleport(posTp.get(pl));
			for (Player p : Bukkit.getServer().getOnlinePlayers()) {
				pl.showPlayer(p);
			}
		}
		
		// gen tp chunks
		
		// start
		gameStarted = true;
		Bukkit.broadcastMessage(pl.localize("game_begin"));
		new Countdown(pl, pl.getConfig().getInt("damage-disable", 60)).runTaskTimer(pl, 0, 20);
		Bukkit.getServer().getPluginManager().callEvent(new UHCGameStartEvent(this));
	}
	
	/**
	 * Finish the game
	 * This method doesn't need to be called as it's automatically called when a team or a player wins
	 * @param winner The name of the winner (Player or team)
	 */
	@SuppressWarnings("deprecation")
	public void finish(String winner) {
		if (!this.isWon) {
			this.winner = winner;
			this.isWon = true;
			for (Player p : Bukkit.getOnlinePlayers()) {
				for (String cmd : pl.getConfig().getStringList("commands.everyone")) {
					pl.getServer().dispatchCommand(pl.getServer().getConsoleSender(), cmd.replace("{PLAYER}", p.getName()));
				}
			}
			if (this.solo == true) {
				for (String wcmd : pl.getConfig().getStringList("commands.winner")) {
					pl.getServer().dispatchCommand(pl.getServer().getConsoleSender(), wcmd.replace("{PLAYER}", winner));
				}
				Bukkit.getServer().getPluginManager().callEvent(new UHCGameEnded(winner));
			}
			else {
				for(String wcmd : pl.getConfig().getStringList("commands.winner")) {
					for (String player : pl.teams.getTeam(winner).getPlayers()) {
						pl.getServer().dispatchCommand(pl.getServer().getConsoleSender(), wcmd.replace("{PLAYER}", player));
					}
				}
				Bukkit.getServer().getPluginManager().callEvent(new UHCGameEnded(pl.teams.getTeam(winner)));
			}
			pl.c.stop();
		}
	}
	
	
	/**
	 * Get the type of the game
	 * @return true if it's a single player game, false if it's a teams game
	 */
	public boolean isSolo() {
		return this.solo;
	}
	
	/**
	 * Get the winner of the game
	 * @return the name of the winner or null if the game isn't finished
	 */
	public String getWinner() {
		return winner;
	}
	
	/**
	 * Get the state of the game
	 * @return true if the game is finished, false if the game is still running OR if the game isn't started yet
	 */
	public boolean isFinished() {
		return isWon;
	}
	
	/**
	 * Allow to know if players can join the game
	 * @return true if it's allowed to join the game, false else
	 */
	public boolean canJoin() {
		return canJoin;
	}
	
	/**
	 * Returns the state of the game
	 * @return true if the game is started, false else
	 */
	public boolean getStarted() {
		return gameStarted;
	}
	
	/**
	 * Called automatically when the pvp is enabled, it finishes the init phase of the game.
	 */
	public void runGame() {
		pl.sbmanager.setPhase("Partie en cours");
		pl.startChrono();
	}

	/**
	 * Enable damages, called automatically after a countdown
	 */
	public void enableDegats() {
		degats = true;
		
	}
	
	/**
	 * @return true if damages are enabled, false else
	 */
	public boolean canTakeDamage() {
		return degats;
	}
	
	/**
	 * @return true if PvP is enabled, false else
	 */
	@Deprecated
	public boolean canPvP() {
		return degats;
	}
	
}
