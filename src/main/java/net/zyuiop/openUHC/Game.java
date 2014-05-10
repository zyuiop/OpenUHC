package net.zyuiop.openUHC;

import java.util.HashMap;

import net.zyuiop.openUHC.teams.UHTeam;
import net.zyuiop.openUHC.timers.Countdown;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Difficulty;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;

public class Game {
	private OpenUHC pl;
	protected boolean isWon = false;
	protected String winner = null;
	protected boolean solo = true;	
	protected boolean canJoin = true;
	protected boolean pvp = false;
	protected boolean degats = false;
	protected boolean gameStarted = false;
	
	public Game(OpenUHC pl) {
		this.pl = pl;
	}
	
	/**
	 * Use this method to start the game. This method shall only be used ONCE.
	 */
	public void start() {
		Bukkit.broadcastMessage(ChatColor.GRAY+""+ChatColor.ITALIC+"Préparation du jeu...");
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
			for (Player p : Bukkit.getOnlinePlayers())
				pl.joueurs.add(p.getName());
		}
		pl.sbmanager.init();
		pl.mapSize = pl.getConfig().getInt("map-size");
		pl.setLimits();
		pl.generateWalls();
		World w = pl.getWorld();
		Bukkit.broadcastMessage(ChatColor.GRAY+""+ChatColor.ITALIC+"Génération des chunks de spawn... ");
		HashMap<String, Location> posTp = new HashMap<String, Location>();
		if (this.solo) {
			for (String p : pl.joueurs) {
				Location l = pl.getRandLoc();
				posTp.put(p, l);
				w.getChunkAt(l).load(true);
			}
		}
		else {
			for (UHTeam t : pl.teams.getTeamsList()) {
				Location l = pl.getRandLoc();
				w.getChunkAt(l).load(true);
				for (String p : t.getPlayers()) {
					posTp.put(p, l);
				}
			}
		}
		Bukkit.broadcastMessage(ChatColor.GRAY+""+ChatColor.ITALIC+"Génération des chunks de spawn terminée.");
		for (String p : posTp.keySet()) {
			Player pl = Bukkit.getPlayer(p);
			pl.setGameMode(GameMode.SURVIVAL);
			pl.getInventory().clear();
			pl.setHealth(20);
			pl.setFoodLevel(20);
			pl.setFlying(false);
			pl.teleport(posTp.get(p));
		}
		
		// gen tp chunks
		
		// start
		gameStarted = true;
		Bukkit.broadcastMessage(ChatColor.GRAY+""+ChatColor.ITALIC+"Début du jeu !");
		new Countdown(pl, pl.getConfig().getInt("damage-disable", 30), "degats").runTaskTimer(pl, 0, 20);

	}
	
	/**
	 * Finish the game
	 * This method doesn't need to be called as it's automatically called when a team or a player wins
	 * @param winner The name of the winner (Player or team)
	 */
	public void finish(String winner) {
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
		}
		else {
			for(String wcmd : pl.getConfig().getStringList("commands.winner")) {
				for (String player : pl.teams.getTeam(winner).getPlayers()) {
					pl.getServer().dispatchCommand(pl.getServer().getConsoleSender(), wcmd.replace("{PLAYER}", player));
				}
			}
		}
		for (String fcmd : pl.getConfig().getStringList("commands.final")) {
			pl.getServer().getScheduler().runTaskLater(pl, new RunCommandTask(pl, fcmd), pl.getConfig().getLong("delay_before_final") * 20);
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
	 * Enable PvP (automatically called)
	 */
	public void enablePVP() {
		pvp = true;
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
		new Countdown(pl, pl.getConfig().getInt("pvp-disable", 120), "pvp").runTaskTimer(pl, 0, 20);
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
	public boolean canPvP() {
		return pvp;
	}
	
}
