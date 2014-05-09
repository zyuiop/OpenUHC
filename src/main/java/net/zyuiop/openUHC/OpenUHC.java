package net.zyuiop.openUHC;

import java.util.ArrayList;
import java.util.Random;

import net.zyuiop.openUHC.commands.CommandGamestart;
import net.zyuiop.openUHC.commands.CommandLimits;
import net.zyuiop.openUHC.commands.CommandPlayers;
import net.zyuiop.openUHC.commands.CommandShrink;
import net.zyuiop.openUHC.commands.CommandTeams;
import net.zyuiop.openUHC.events.BlockEvents;
import net.zyuiop.openUHC.events.CraftEvents;
import net.zyuiop.openUHC.events.EntityEvents;
import net.zyuiop.openUHC.events.MiscEvents;
import net.zyuiop.openUHC.events.NetworkEvents;
import net.zyuiop.openUHC.events.PlayerEvents;
import net.zyuiop.openUHC.teams.UHTeam;
import net.zyuiop.openUHC.teams.UHTeamManager;
import net.zyuiop.openUHC.timers.ChronoThread;
import net.zyuiop.openUHC.timers.Countdown;
import net.zyuiop.openUHC.timers.RetrecirCount;
import net.zyuiop.openUHC.utils.UHUtils;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapelessRecipe;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;


public class OpenUHC extends JavaPlugin {

	protected boolean gameStarted = false;
	
	protected UHTeamManager teams = new UHTeamManager();
	protected ArrayList<String> joueurs = new ArrayList<String>(); // Répertorie joueurs online
	
	protected boolean solo = true;
	protected boolean canJoin = true;
	
	protected boolean pvp = false;
	protected boolean degats = false;
	
	protected Scoreboard sb;
	protected Objective right;
	
	protected Game game = new Game(this);
	
	protected ArrayList<Integer> limits = new ArrayList<Integer>();
	public static Integer XLIMITN = 0;
	public static Integer XLIMITP = 1;
	public static Integer ZLIMITN = 2;
	public static Integer ZLIMITP = 3;
	
	public String sbname = "right";
	
	public ChronoThread c = null;
	
	public int mapSize;
	
	@Override
	public void onEnable() {
		sb = Bukkit.getScoreboardManager().getNewScoreboard();
		right = sb.registerNewObjective(sbname, "dummy");
		getCommand("teams").setExecutor(new CommandTeams(this));
		getCommand("players").setExecutor(new CommandPlayers(this));
		getCommand("gamestart").setExecutor(new CommandGamestart(this));
		getCommand("shrink").setExecutor(new CommandShrink(this));
		getCommand("limits").setExecutor(new CommandLimits(this));
		
		getServer().getPluginManager().registerEvents(new BlockEvents(this), this);
		getServer().getPluginManager().registerEvents(new CraftEvents(), this);
		getServer().getPluginManager().registerEvents(new EntityEvents(this), this);
		getServer().getPluginManager().registerEvents(new MiscEvents(this), this);
		getServer().getPluginManager().registerEvents(new NetworkEvents(this), this);
		getServer().getPluginManager().registerEvents(new PlayerEvents(this), this);
		
		this.saveDefaultConfig();
		
		
		
		// Set craft //
		ShapelessRecipe goldenMelon = new ShapelessRecipe(new ItemStack(Material.SPECKLED_MELON));
		goldenMelon.addIngredient(1, Material.GOLD_BLOCK);
		goldenMelon.addIngredient(1, Material.MELON);
		this.getServer().addRecipe(goldenMelon);
		if (getConfig().getBoolean("delete_world")) {
			try {
	            Runtime.getRuntime().addShutdownHook(new WorldDeleter(getWorld().getName()));
	        } catch (Exception e) {
	            e.printStackTrace();
	        }
		}
		
	}
	
	/**
	 * 
	 * @return The game representation object
	 */
	public Game getGame() {
		return game;
	}
	
	/**
	 * 
	 * @return The teams manager object
	 */
	public UHTeamManager teamManager() {
		return teams;
	}
	
	protected void generateWalls() {
		Bukkit.broadcastMessage(ChatColor.GRAY+""+ChatColor.ITALIC+"Génération des murs...");
		World w = getWorld();
		double work = (mapSize*4)*255;
		double done = 0;
		
		int x = limits.get(XLIMITN);
		while (x <= limits.get(XLIMITP)) {
			int y = 0;
			while (y <= 255) {
				w.getBlockAt(x,y,limits.get(ZLIMITN)).setType(Material.BEDROCK);
				w.getBlockAt(x,y,limits.get(ZLIMITP)).setType(Material.BEDROCK);
				
				y++;
				done++;
				UHUtils.showProgress(done,work);
				done++;
				UHUtils.showProgress(done,work);
			}
			x++;
		}
		
		int z = limits.get(ZLIMITN);
		while (z <= limits.get(ZLIMITP)) {
			int y = 0;
			while (y <= 255) {
				w.getBlockAt(limits.get(XLIMITN),y,z).setType(Material.BEDROCK);
				w.getBlockAt(limits.get(XLIMITP),y,z).setType(Material.BEDROCK);
				
				y++;
				done++;
				UHUtils.showProgress(done,work);
				done++;
				UHUtils.showProgress(done,work);
			}
			z++;
		}
		Bukkit.broadcastMessage(ChatColor.GRAY+""+ChatColor.ITALIC+"Génération des murs terminée !");

	}
	
	/**
	 * 
	 * @param pseudo
	 * @return true if "pseudo" is in game
	 */
	public boolean isIngame(String pseudo) {
		return joueurs.contains(pseudo);
	}

	/***
	 * This method returns the world
	 * @return The map the game is running on
	 */
	public World getWorld() {
		return Bukkit.getWorld(this.getConfig().getString("world","world"));
	}
	
	/***
	 * @author zyuiop
	 * Initialise le scoreboard au début de la partie, appelée par startGame
	 * 
	 */
	public void setupScoreboards() {
		sb.registerNewObjective("vie", "health").setDisplaySlot(DisplaySlot.PLAYER_LIST);
		if (solo == false) {
			int c = 0;
			for (String t : teams.getTeamsMap().keySet()) {
				Team te = sb.registerNewTeam(t);
				for (String pl : teams.getTeam(t).getPlayers()) {
					te.addPlayer(Bukkit.getOfflinePlayer(pl));
					this.teams.getTeam(t).setColor(UHUtils.getCol(c));
					try {
						Player p = Bukkit.getPlayer(pl);
						p.setDisplayName(UHUtils.getCol(c)+p.getName());
				        p.sendMessage("Vous avez rejoint l'équipe "+UHUtils.getCol(c)+t);
					} catch(Exception e) {
						
					}
					joueurs.add(pl);
				}
				te.setPrefix(UHUtils.getCol(c)+"");
				c++;
				if (c > 11)
					c=0;
			}
		}

		scoreboard();
		for (Player p : Bukkit.getOnlinePlayers()) {
			p.setScoreboard(sb);
		}
	}
	

	/***
	 * @author zyuiop
	 * Supprime un joueur du jeu
	 * 
	 */
	public void deletePlayer(String n) {
		
		if (getStarted() && joueurs.contains(n)) {
			joueurs.remove(n);
		}
		
		if (solo == true)
			return;
		
		UHTeam remove = null;
		
		for (UHTeam t : teams.getTeamsList()) {
			if (t.isContained(n))
			{
				t.deletePlayer(n);
				if (t.getPlayers().size() == 0)
					remove = t;
			}
		}
		if (remove != null) {
			Bukkit.broadcastMessage(ChatColor.GOLD+"L'équipe "+remove.getColorizedName()+ChatColor.GOLD+" a été éliminée.");
			teams.deleteTeam(remove.getName());
		}
	}
	
	public String phase = "Invincibilité";
	public int hours = 0;
	public int minutes = 0;
	public int seconds = 0;
	
	@SuppressWarnings("deprecation")
	public void scoreboard() {
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
		if (solo == false)
			right.getScore(Bukkit.getOfflinePlayer("Equipes : "+ChatColor.AQUA+teams.size())).setScore(2);
		right.getScore(Bukkit.getOfflinePlayer("Joueurs : "+ChatColor.AQUA+joueurs.size())).setScore(1);
		
		if (solo == false && teams.size() == 1)
		{
			if (c != null) {
				c.stop();
				Bukkit.getServer().broadcastMessage(ChatColor.GOLD+"L'équipe "+teams.getTeamsList().get(0).getColorizedName()+ChatColor.GOLD+" a gagné la partie !");
				game.finish(teams.getTeamsList().get(0).getName());
			}
		} else if (solo == true && joueurs.size() <= 1) {
			if (c != null) {
				c.stop();
				Bukkit.getServer().broadcastMessage(ChatColor.GOLD+"Le joueur "+joueurs.get(0)+" a gagné la partie !");
				game.finish(joueurs.get(0));
			}
		}
	}
	
	public boolean isSolo() {
		return solo;
	}
	
	public String getWinner() {
		return winner;
	}
	
	public boolean isFinished() {
		return isWon;
	}
	
	
	protected boolean isWon = false;
	protected String winner = null;
	
	public UHTeam getTeam(String player) {
		for (UHTeam t : teams.getTeamsList()) {
			if (t.isContained(player))
			{
				return t;
			}
		}
		return null;
	}
	
	public boolean delFromTeam(String player, String team) {
		if (teams.getTeam(team) == null)
			return false;
		if (!teams.getTeam(team).isContained(player))
			return false;
		teams.getTeam(team).deletePlayer(player);
		return true;
	}
	
	public void reduceSize(ArrayList<Integer> l) {
		setLimits(l);
		for (String j : joueurs) {
			Player p = Bukkit.getOfflinePlayer(j).getPlayer();
			if (p != null) {
				if (!isInLimits(p.getLocation().getBlockX(), p.getLocation().getBlockZ()))
					p.teleport(getRandLoc());
			}
		}
		generateWalls();
	}
	
	public boolean isInLimits(int x, int z) {
		if (x > limits.get(XLIMITN) && x < limits.get(XLIMITP) && z > limits.get(ZLIMITN) && z < limits.get(ZLIMITP))
			return true;
		return false;
	}
	
	public ArrayList<Integer> getLimits() {
		return limits;
	}
	
	public void setLimits(Integer size) {
		limits = getLimits(size);
	}
	
	public void setLimits(ArrayList<Integer> l) {
		limits = l;
	}
	
	public ArrayList<Integer> getLimits(Integer size) {
		ArrayList<Integer> l = new ArrayList<Integer>();
		Location spawn = getWorld().getSpawnLocation();
		size = (int) Math.floor(size/2);
		l.add(spawn.getBlockX()-size);
		l.add(spawn.getBlockX()+size);
		l.add(spawn.getBlockZ()-size);
		l.add(spawn.getBlockZ()+size);
		return l;
	}
	
	public void setLimits() {
		Integer size = getConfig().getInt("map-size");
		setLimits(size);
	}
	
	public boolean canJoin() {
		return canJoin;
	}
	
	
	 
	
	public void enablePVP() {
		pvp = true;
		runGame();
	}
	
	public void runGame() {
		phase = "Partie en cours";
		c = new ChronoThread(this);
		c.start();
	}
	
	public void enableDegats() {
		degats = true;
		new Countdown(this, this.getConfig().getInt("pvp-disable", 120), "pvp").runTaskTimer(this, 0, 20);
	}

	public void retrecirCount(int amount) {
		ArrayList<Integer> nc = getLimits(mapSize-amount);
		mapSize = mapSize-amount;

		new RetrecirCount(this, 120, nc).runTaskTimer(this, 0, 20);
	}
	
	public boolean canTakeDamage() {
		return degats;
	}
	
	public boolean canPvP() {
		return pvp;
	}
	
	
	public boolean addPlayer(String teamName, String playerName) {
		if (getTeam(playerName) != null)
			return false;
		
		if (!teams.teamExists(teamName))
			return false;
		
		teams.getTeam(teamName).addPlayer(playerName);
		return true;
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
	
	//////////////
	// GETTERS  //
	//////////////
	
	public Scoreboard getSb() {
		return sb;
	}
	
	public Objective getRight() {
		return right;
	}
	
	public boolean getStarted() {
		return gameStarted;
	}
	
	//////////////
	// UTILS    //
	//////////////
	
	public Location getRandLoc() {
		World w = getWorld();
		while (true) {
			int tpx = UHUtils.randomInt(limits.get(XLIMITN),limits.get(XLIMITP));
			int tpz = UHUtils.randomInt(limits.get(ZLIMITN),limits.get(ZLIMITP));
			int tpy = 250;
			while (w.getBlockAt(tpx, tpy, tpz).getType().equals(Material.AIR))
				tpy--;
			if (!w.getBlockAt(tpx, tpy, tpz).isLiquid())
				return new Location(w, tpx, tpy+15, tpz);
		}
	}
	
	
	
}

