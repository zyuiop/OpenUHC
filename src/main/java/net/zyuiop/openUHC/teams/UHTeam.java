package net.zyuiop.openUHC.teams;

import java.util.ArrayList;

import org.bukkit.ChatColor;

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
		if (getColor() == null)
			return getName();
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
