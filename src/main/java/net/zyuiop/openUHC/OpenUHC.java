package net.zyuiop.openUHC;

import java.util.ArrayList;

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
import net.zyuiop.openUHC.timers.RetrecirCount;
import net.zyuiop.openUHC.utils.UHUtils;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.ShapelessRecipe;
import org.bukkit.plugin.java.JavaPlugin;

public class OpenUHC extends JavaPlugin {

	
	
	protected UHTeamManager teams = new UHTeamManager();
	protected ArrayList<String> joueurs = new ArrayList<String>(); // Répertorie joueurs online
	
	protected Game game = new Game(this);
	protected ScoreboardManager sbmanager;
	
	protected ArrayList<Integer> limits = new ArrayList<Integer>();
	public static Integer XLIMITN = 0;
	public static Integer XLIMITP = 1;
	public static Integer ZLIMITN = 2;
	public static Integer ZLIMITP = 3;
	

	public ChronoThread c = null;
	
	public int mapSize;
	
	@Override
	public void onEnable() {
		sbmanager = new ScoreboardManager(this);
		getCommand("teams").setExecutor(new CommandTeams(this));
		getCommand("players").setExecutor(new CommandPlayers(this));
		getCommand("gamestart").setExecutor(new CommandGamestart(this));
		getCommand("shrink").setExecutor(new CommandShrink(this));
		getCommand("limits").setExecutor(new CommandLimits(this));
		
		getServer().getPluginManager().registerEvents(new BlockEvents(this), this);
		getServer().getPluginManager().registerEvents(new CraftEvents(this), this);
		getServer().getPluginManager().registerEvents(new EntityEvents(this), this);
		getServer().getPluginManager().registerEvents(new MiscEvents(this), this);
		getServer().getPluginManager().registerEvents(new NetworkEvents(this), this);
		getServer().getPluginManager().registerEvents(new PlayerEvents(this), this);
		
		this.saveDefaultConfig();
		if (getConfig().getBoolean("delete_world")) {
			try {
	            Runtime.getRuntime().addShutdownHook(new WorldDeleter(getWorld().getName()));
	        } catch (Exception e) {
	            e.printStackTrace();
	        }
		}
		setCustomCrafts();
	}
	
	private void setCustomCrafts() {
		/* Melon */
		this.getServer().addRecipe(getMelonRecipe());
		
		/* Compass */
		if (isCompassEnabled()) {
			this.getServer().addRecipe(getCompassRecipe());
		}
	}
	
	public boolean isCompassEnabled() {
		return this.getConfig().getBoolean("compass.enabled", true);
	}
	
	public boolean isCompassHarder() {
		if (!isCompassEnabled())
			return false;
		return this.getConfig().getBoolean("compass.harder", false);
	}
	
	
	public ShapedRecipe getCompassRecipe() {
		if (!isCompassEnabled()) return null;
		
		ShapedRecipe compass = new ShapedRecipe(new ItemStack(Material.COMPASS));
		compass.shape(new String[] {"CIE", "IRI", "BIF"});
		compass.setIngredient('I', Material.IRON_INGOT);
		compass.setIngredient('R', Material.REDSTONE);
		if (isCompassHarder()) {
			compass.setIngredient('C', Material.SLIME_BALL);
			compass.setIngredient('E', Material.SADDLE);
			compass.setIngredient('B', Material.BLAZE_ROD);
			compass.setIngredient('F', Material.ENDER_PEARL);
		} else {
			compass.setIngredient('C', Material.SULPHUR);
			compass.setIngredient('E', Material.SPIDER_EYE);
			compass.setIngredient('B', Material.BONE);
			compass.setIngredient('F', Material.ROTTEN_FLESH);
		}
		return compass;
	}
	
	public ShapelessRecipe getMelonRecipe() {
		ShapelessRecipe goldenMelon = new ShapelessRecipe(new ItemStack(Material.SPECKLED_MELON));
		goldenMelon.addIngredient(1, Material.GOLD_BLOCK);
		goldenMelon.addIngredient(1, Material.MELON);
		return goldenMelon;
	}
	
	/**
	 * @return The game representation object
	 */
	public Game getGame() {
		return game;
	}
	
	/**
	 * @return The scoreboard manager
	 */
	public ScoreboardManager getSbManager() {
		return sbmanager;
	}
	
	/**
	 * @return The teams manager object
	 */
	public UHTeamManager teamManager() {
		return teams;
	}
	
	/**
	 * Protected method, called automatically when you shrink the map or when you start the game.
	 */
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
	 * @param player the name of the player
	 * @return true if the player is in game
	 */
	public boolean isIngame(String player) {
		return joueurs.contains(player);
	}

	/***
	 * This method returns the world
	 * @return The map the game is running on
	 */
	public World getWorld() {
		return Bukkit.getWorld(this.getConfig().getString("world","world"));
	}

	/***
	 * Delete a player from the game
	 * @param n The name of the player
	 */
	public void deletePlayer(String n) {
		
		if (game.getStarted() && joueurs.contains(n)) {
			joueurs.remove(n);
		}
		
		if (getGame().solo == true)
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
	
	/**
	 * Get the team of a player		
	 * @param player The nickname of the target player
	 * @return the team of the player, represented by an UHTeam object
	 */
	public UHTeam getTeam(String player) {
		for (UHTeam t : teams.getTeamsList()) {
			if (t.isContained(player))
			{
				return t;
			}
		}
		return null;
	}
	
	/**
	 * Deletes a player from a team
	 * @param player The nickname of the player
	 * @param team The name of the team
	 * @return true if the player has been successfully removed, false if the team doesn't exist or if the player isn't in the team
	 */
	public boolean delFromTeam(String player, String team) {
		if (teams.getTeam(team) == null)
			return false;
		if (!teams.getTeam(team).isContained(player))
			return false;
		teams.getTeam(team).deletePlayer(player);
		return true;
	}
	
	/**
	 * Reduces the size of the map and generates a new wall
	 * @param l The new limits of the map
	 */
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
	
	/**
	 * Allow to know if (x,z) is into the limits of the map
	 * @param x
	 * @param z
	 * @return true if (x,z) is in limits, false else
	 */
	public boolean isInLimits(int x, int z) {
		if (x > limits.get(XLIMITN) && x < limits.get(XLIMITP) && z > limits.get(ZLIMITN) && z < limits.get(ZLIMITP))
			return true;
		return false;
	}
	
	/**
	 * Gets an array representing the world limits
	 * @return
	 */
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
	
	

	public void retrecirCount(int amount) {
		ArrayList<Integer> nc = getLimits(mapSize-amount);
		mapSize = mapSize-amount;

		new RetrecirCount(this, 120, nc).runTaskTimer(this, 0, 20);
	}
	
	
	
	public boolean addPlayer(String teamName, String playerName) {
		if (getTeam(playerName) != null)
			return false;
		
		if (!teams.teamExists(teamName))
			return false;
		
		teams.getTeam(teamName).addPlayer(playerName);
		return true;
	}
	
	
	
	//////////////
	// GETTERS  //
	//////////////
	
	
	
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
	
	public void startChrono() {
		c = new ChronoThread(this, this.getConfig().getInt("damage-disable")+this.getConfig().getInt("pvp-disable"));
		c.start();
	}
	
}

