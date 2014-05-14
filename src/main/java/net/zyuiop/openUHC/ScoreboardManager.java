package net.zyuiop.openUHC;

import java.util.Random;

import net.zyuiop.openUHC.utils.UHUtils;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

public class ScoreboardManager implements org.bukkit.scoreboard.ScoreboardManager {
	
	protected Scoreboard sb;
	protected Objective right;	
	private OpenUHC plugin;
	public String sbname = "right";
	
	public ScoreboardManager(OpenUHC pl) {
		this.plugin = pl;
		this.sb = Bukkit.getScoreboardManager().getNewScoreboard();
	}
	
	
	public String phase = "Invincibilité";
	public int hours = 0;
	public int minutes = 0;
	public int seconds = 0;
	
	@SuppressWarnings("deprecation")
	public void refresh() {
		Objective obj = null;
		try {
			obj = sb.getObjective(sbname);
			obj.setDisplaySlot(null);
			obj.unregister();
		} catch (Exception e) {

		}
		Random r = new Random();
		sbname = "right"+r.nextInt(10000000);
		obj = sb.registerNewObjective(sbname, "dummy");
		obj = sb.getObjective(sbname);
		this.right = obj;

		right.setDisplayName(ChatColor.DARK_AQUA+"== UHC Games ==");
		right.setDisplaySlot(DisplaySlot.SIDEBAR);
		right.getScore(Bukkit.getOfflinePlayer(" ")).setScore(6);
		right.getScore(Bukkit.getOfflinePlayer(this.phase)).setScore(5);
		right.getScore(Bukkit.getOfflinePlayer(((hours > 0) ? hours+"h " : "")+((minutes > 0) ? minutes+"m " : "")+seconds+"s")).setScore(4);
		right.getScore(Bukkit.getOfflinePlayer(" ")).setScore(3);
		if (plugin.getGame().solo == false)
			right.getScore(Bukkit.getOfflinePlayer("Equipes : "+ChatColor.AQUA+plugin.teams.size())).setScore(2);
		right.getScore(Bukkit.getOfflinePlayer("Joueurs : "+ChatColor.AQUA+plugin.joueurs.size())).setScore(1);
		
		if (plugin.getGame().solo == false && plugin.teams.size() == 1)
		{
			if (plugin.c != null) {
				plugin.c.stop();
				Bukkit.getServer().broadcastMessage(ChatColor.GOLD+"L'équipe "+plugin.teams.getTeamsList().get(0).getColorizedName()+ChatColor.GOLD+" a gagné la partie !");
				plugin.game.finish(plugin.teams.getTeamsList().get(0).getName());
			}
		} else if (plugin.getGame().solo == true && plugin.joueurs.size() <= 1) {
			if (plugin.c != null) {
				plugin.c.stop();
			}
			Bukkit.getServer().broadcastMessage(ChatColor.GOLD+"Le joueur "+plugin.joueurs.get(0)+" a gagné la partie !");
			plugin.game.finish(plugin.joueurs.get(0).getDisplayName());
		}
	}
	
	public void init() {
		sb.registerNewObjective("vie", "health").setDisplaySlot(DisplaySlot.PLAYER_LIST);
		if (plugin.getGame().solo == false) {
			int c = 0;
			for (String t : plugin.teams.getTeamsMap().keySet()) {
				Team te = sb.registerNewTeam(t);
				for (Player pl : plugin.teams.getTeam(t).getPlayers()) {
					te.addPlayer(Bukkit.getOfflinePlayer(pl.getName()));
					plugin.teams.getTeam(t).setColor(UHUtils.getCol(c));
					try {
						pl.setDisplayName(UHUtils.getCol(c)+pl.getName());
				        pl.sendMessage("Vous avez rejoint l'équipe "+UHUtils.getCol(c)+t);
					} catch(Exception e) {
						
					}
					plugin.joueurs.add(pl);
				}
				te.setPrefix(UHUtils.getCol(c)+"");
				c++;
				if (c > 11)
					c=0;
			}
		}

		refresh();
		for (Player p : Bukkit.getOnlinePlayers()) {
			p.setScoreboard(sb);
		}
	}

	public Objective getRight() {
		return right;
	}
	
	public void formatTime(long seconds) {
		int hours = (int) seconds / 3600;
	    int remainder = (int) seconds - hours * 3600;
	    int mins = remainder / 60;
	    remainder = remainder - mins * 60;
	    int secs = remainder;

	    this.hours = hours;
	    this.minutes = mins;
	    this.seconds = secs;
	}
	
	public void setPhase(String p) {
		this.phase = p;
	}

	public Scoreboard getMainScoreboard() {
		return sb;
	}

	public Scoreboard getNewScoreboard() {
		return Bukkit.getScoreboardManager().getNewScoreboard();
	}
}
