package net.zyuiop.openUHC.teams;

import java.util.ArrayList;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class UHTeam {
	private ArrayList<Player> players;
	private ChatColor color = null;
	private String name = null;
	
	public UHTeam(String name, ChatColor color) {
		this.color = color;
		this.name = name;
		this.players = new ArrayList<Player>();
	}
	
	public UHTeam(String name) {
		this.name = name;
		this.players = new ArrayList<Player>();
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
	
	public void addPlayer(Player p) {
		this.players.add(p);
	}
	
	public boolean deletePlayer(Player p) {
		if (isContained(p)) {
			this.players.remove(p);
			return true;
		}
		return false;
	}
	
	public boolean isContained(Player player) {
		return this.players.contains(player);
	}
	
	public ArrayList<Player> getPlayers() {
		return players;
	}
}
