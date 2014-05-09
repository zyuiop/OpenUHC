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
import org.bukkit.scheduler.BukkitTask;

public class Game {
	private OpenUHC pl;
	public Game(OpenUHC pl) {
		this.pl = pl;
	}
	
	public void start() {
		Bukkit.broadcastMessage(ChatColor.GRAY+""+ChatColor.ITALIC+"Préparation du jeu...");
		pl.getWorld().setGameRuleValue("doDaylightCycle", pl.getConfig().getString("daylight-cycle", "true"));
		pl.getWorld().setGameRuleValue("naturalRegeneration", "false");
		pl.getWorld().setTime(pl.getConfig().getLong("begin-time", 6000L));
		pl.getWorld().setStorm(pl.getConfig().getBoolean("begin-storm", false));
		pl.getWorld().setDifficulty(Difficulty.HARD);
		pl.canJoin = false;
		if (pl.teams.size() >= 2)
			pl.solo = false;
		else {	
			pl.solo = true;
			for (Player p : Bukkit.getOnlinePlayers())
				pl.joueurs.add(p.getName());
		}
		pl.setupScoreboards();
		pl.mapSize = pl.getConfig().getInt("map-size");
		pl.setLimits();
		pl.generateWalls();
		World w = pl.getWorld();
		Bukkit.broadcastMessage(ChatColor.GRAY+""+ChatColor.ITALIC+"Génération des chunks de spawn... ");
		HashMap<String, Location> posTp = new HashMap<String, Location>();
		if (pl.solo) {
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
		pl.gameStarted = true;
		Bukkit.broadcastMessage(ChatColor.GRAY+""+ChatColor.ITALIC+"Début du jeu !");
		BukkitTask task = new Countdown(pl, pl.getConfig().getInt("damage-disable", 30), "degats").runTaskTimer(pl, 0, 20);

	}
}
