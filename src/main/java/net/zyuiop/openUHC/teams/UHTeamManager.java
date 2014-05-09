package net.zyuiop.openUHC.teams;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Hashtable;
import java.util.List;

import org.bukkit.ChatColor;

public class UHTeamManager {
	private Hashtable<String, UHTeam> teams;
	
	public UHTeamManager() {
		teams = new Hashtable<String, UHTeam>();
	}
	
	public boolean teamExists(String name) {
		return teams.keySet().contains(name);
	}
	
	public boolean registerTeam(UHTeam team) {
		if (teamExists(team.getName()))
			return false;
		teams.put(team.getName(), team);
		return true;
	}
	
	public boolean registerTeam(String name, ChatColor color) {
		return registerTeam(new UHTeam(name, color));
	}
	
	public boolean registerTeam(String name) {
		return registerTeam(new UHTeam(name));
	}
	
	public boolean deleteTeam(String name) {
		if (!teamExists(name))
			return false;
		teams.remove(name);
		return true;
	}
	
	public UHTeam getTeam(String name) {
		if (!teamExists(name))
			return null;
		return teams.get(name);
	}
	
	public Hashtable<String, UHTeam> getTeamsMap() {
		return teams;
	}
	
	public ArrayList<UHTeam> getTeamsList() {
		ArrayList<UHTeam> ret = new ArrayList<UHTeam>();
		for (String t : teams.keySet()) {
			ret.add(teams.get(t));
		}
		return ret;
	}
	
	public int size() {
		return teams.size();
	}
}
