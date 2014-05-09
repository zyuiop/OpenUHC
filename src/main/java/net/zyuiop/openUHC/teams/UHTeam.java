package net.zyuiop.openUHC.teams;

import java.util.ArrayList;

import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

public class UHTeam {
	private ArrayList<String> players;
	private ChatColor color = null;
	private String name = null;
	
	public UHTeam(String name, ChatColor color) {
		this.color = color;
		this.name = name;
		this.players = new ArrayList<String>();
	}
	
	public UHTeam(String name) {
		this.name = name;
		this.players = new ArrayList<String>();
	}
	
	public void setColor(ChatColor color) {
		this.color = color;
	}
	
	public String getName() {
		return name;
	}
	
	public ChatColor getColor() {
		return color;
	}
	
	public String getColorizedName() {
		return getColor()+getName();
	}
	
	public void addPlayer(String p) {
		this.players.add(p);
	}
	
	public boolean deletePlayer(String p) {
		if (isContained(p)) {
			this.players.remove(p);
			return true;
		}
		return false;
	}
	
	public boolean isContained(String player) {
		return this.players.contains(player);
	}
	
	public ArrayList<String> getPlayers() {
		return players;
	}
}
