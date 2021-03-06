package net.zyuiop.openUHC;

import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.UUID;

import net.zyuiop.openUHC.commands.CommandGamestart;
import net.zyuiop.openUHC.commands.CommandLimits;
import net.zyuiop.openUHC.commands.CommandPlayers;
import net.zyuiop.openUHC.commands.CommandShrink;
import net.zyuiop.openUHC.commands.CommandTeams;
import net.zyuiop.openUHC.commands.CommandTeleport;
import net.zyuiop.openUHC.listeners.BlockEvents;
import net.zyuiop.openUHC.listeners.EntityEvents;
import net.zyuiop.openUHC.listeners.InventoryEvents;
import net.zyuiop.openUHC.listeners.MiscEvents;
import net.zyuiop.openUHC.listeners.NetworkEvents;
import net.zyuiop.openUHC.listeners.PlayerEvents;
import net.zyuiop.openUHC.spectators.SpectatorManager;
import net.zyuiop.openUHC.teams.UHTeam;
import net.zyuiop.openUHC.teams.UHTeamManager;
import net.zyuiop.openUHC.timers.ChronoThread;
import net.zyuiop.openUHC.timers.RetrecirCount;
import net.zyuiop.openUHC.utils.UHUtils;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.ShapelessRecipe;
import org.bukkit.plugin.java.JavaPlugin;

public class OpenUHC extends JavaPlugin {

	
	
	protected UHTeamManager teams = new UHTeamManager();
	protected ArrayList<String> joueurs = new ArrayList<String>(); // Répertorie joueurs online
	protected SpectatorManager spectatorManager = null;
	protected Game game = new Game(this);
	protected ScoreboardManager sbmanager;
	protected ArrayList<Integer> limits = new ArrayList<Integer>();
	public static Integer XLIMITN = 0;
	public static Integer XLIMITP = 1;
	public static Integer ZLIMITN = 2;
	public static Integer ZLIMITP = 3;
	public HashMap<UUID, Date> logout_times = new HashMap<UUID, Date>();

	public ChronoThread c = null;
	
	public int mapSize;
	
	@Override
	public void onEnable() {
		this.language = getConfig().getString("language", "en");
		loadTranslations();
		
		sbmanager = new ScoreboardManager(this);
		spectatorManager = new SpectatorManager(this);
		getCommand("tp").setExecutor(new CommandTeleport(this));
		getCommand("teams").setExecutor(new CommandTeams(this));
		getCommand("players").setExecutor(new CommandPlayers(this));
		getCommand("gamestart").setExecutor(new CommandGamestart(this));
		getCommand("shrink").setExecutor(new CommandShrink(this));
		getCommand("limits").setExecutor(new CommandLimits(this));
		
		getServer().getPluginManager().registerEvents(new BlockEvents(this), this);
		getServer().getPluginManager().registerEvents(new InventoryEvents(this), this);
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
		Bukkit.broadcastMessage(this.localize("generating_walls").replace("{PERCENT}", "0"));
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
				int res = 0;
				res = UHUtils.showProgress(done,work);
				if (res != 0) Bukkit.broadcastMessage(this.localize("generating_walls").replace("{PERCENT}", res+""));
				done++;
				res = UHUtils.showProgress(done,work);
				if (res != 0) Bukkit.broadcastMessage(this.localize("generating_walls").replace("{PERCENT}", res+""));
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
				int res = 0;
				res = UHUtils.showProgress(done,work);
				if (res != 0) Bukkit.broadcastMessage(this.localize("generating_walls").replace("{PERCENT}", res+""));
				done++;
				res = UHUtils.showProgress(done,work);
				if (res != 0) Bukkit.broadcastMessage(this.localize("generating_walls").replace("{PERCENT}", res+""));
			}
			z++;
		}
		Bukkit.broadcastMessage(this.localize("generation_ended"));

	}
	
	/**
	 * @param player The Player object representing the player
	 * @return true if the player is in game
	 */
	public boolean isIngame(Player player) {
		return isIngame(player.getName());
	}
	
	/**
	 * @param player the name of the player
	 * @return true if the player is in game
	 */
	public boolean isIngame(String player) {
		return this.joueurs.contains(player);
	}

	/***
	 * This method returns the world
	 * @return The map the game is running on
	 */
	public World getWorld() {
		return Bukkit.getWorld(this.getConfig().getString("world","world"));
	}

	/**
	 * This method generates a chunk
	 * @param Chunk to generate
	 */
	public void generateChunk(Chunk c) {
		int x = c.getX()-Bukkit.getViewDistance();
		int toX = x + (Bukkit.getViewDistance() * 2);
		int toZ = x + (Bukkit.getViewDistance() * 2);
		
		while (x < toX) {
			int z = c.getZ()-Bukkit.getViewDistance();
			while (z < toZ) {
				getWorld().loadChunk(x,z);
				z++;
			}
			x++;
		}
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
			Bukkit.broadcastMessage(this.localize("team_stumped").replace("{TEAM}", remove.getColorizedName()));
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
	public boolean delFromTeam(Player player, String team) {
		if (teams.getTeam(team) == null)
			return false;
		if (!teams.getTeam(team).isContained(player.getName()))
			return false;
		teams.getTeam(team).deletePlayer(player.getName());
		return true;
	}
	
	/**
	 * Reduces the size of the map and generates a new wall
	 * @param l The new limits of the map
	 */
	public void reduceSize(ArrayList<Integer> l) {
		setLimits(l);
		for (String pname : joueurs) {
			Player p = Bukkit.getPlayer(pname);
			if (p != null) {
				if (!isInLimits(p.getLocation().getBlockX(), p.getLocation().getBlockZ()))
					p.teleport(getSafeRandLoc());
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
	
	public boolean isInLimits(int x, int z, ArrayList<Integer> limits) {
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
	
	
	
	public boolean addPlayer(String teamName, Player player) {
		if (getTeam(player.getName()) != null)
			return false;
		
		if (!teams.teamExists(teamName))
			return false;
		
		teams.getTeam(teamName).addPlayer(player.getName());
		return true;
	}
	
	
	
	//////////////
	// GETTERS  //
	//////////////
	
	public SpectatorManager getSpectatorManager() {
		return spectatorManager;
	}
	
	//////////////
	// UTILS    //
	//////////////
	
	public Location getRandLoc() {
		World w = getWorld();
	
		int tpx = UHUtils.randomInt(limits.get(XLIMITN),limits.get(XLIMITP));
		int tpz = UHUtils.randomInt(limits.get(ZLIMITN),limits.get(ZLIMITP));
		int tpy = 250;
		while (w.getBlockAt(tpx, tpy, tpz).getType().equals(Material.AIR))
			tpy--;
		if (!w.getBlockAt(tpx, tpy, tpz).isLiquid())
			return new Location(w, tpx, 250, tpz);
		else
			return getRandLoc();
	}
	
	public Location getSafeRandLoc() {
		World w = getWorld();
	
		int tpx = UHUtils.randomInt(limits.get(XLIMITN),limits.get(XLIMITP));
		int tpz = UHUtils.randomInt(limits.get(ZLIMITN),limits.get(ZLIMITP));
		int tpy = 250;
		while (w.getBlockAt(tpx, tpy, tpz).getType().equals(Material.AIR))
			tpy--;
		if (!w.getBlockAt(tpx, tpy, tpz).isLiquid())
			return new Location(w, tpx+0.5, tpy+2, tpz+0.5);
		else
			return getSafeRandLoc();
	}
	
	public void startChrono() {
		c = new ChronoThread(this, this.getConfig().getInt("damage-disable"));
		c.start();
	}
	
	/**
	 * Get ingame players
	 * @return a list of ingame players
	 */
	public ArrayList<String> getPlayers() {
		return this.joueurs;
	}
	
	
	/**
	 * Get the correct translation for the key
	 * @return the translation you can use.
	 */
	private FileConfiguration translationsFile = null;
	private File configFile = null;
	public String language = null;
	public void loadTranslations(boolean force) {
		if (translationsFile == null || force) {
			configFile = new File(getDataFolder(), "translations."+language+".yml");
			if (!configFile.exists()) {
				try {
					Bukkit.getLogger().info("Language was not found, trying to get it from the plugin file...");
					saveResource("translations."+language+".yml", false);
				} catch (IllegalArgumentException e) {
					if (language.equals("en"))
					{	translationsFile = null;
						Bukkit.getLogger().severe("An error occured : impossible to find en language !"+e.getMessage());
					} else {
						Bukkit.getLogger().warning("An error occured : impossible to find "+language+" language !"+e.getMessage());
						Bukkit.getLogger().info("en language will be used in replacement.");
						language = "en";
						loadTranslations(true);
					}
				}
			}
		 	translationsFile = YamlConfiguration.loadConfiguration(configFile);
		 }
		 InputStream defConfigStream = this.getResource("translations.yml");
		 if (defConfigStream != null) {
		        YamlConfiguration defConfig = YamlConfiguration.loadConfiguration(defConfigStream);
		        translationsFile.setDefaults(defConfig);
		 }
	}
	
	public void loadTranslations() {
		loadTranslations(false);
	}
	
	
	public String localize(String key) {
		String tran = translationsFile.getString(key);
		if (tran == null) {
			this.getLogger().warning("An error occured : impossible to find translation for "+key+" in translations.yml file !");
			tran = ChatColor.DARK_RED+"Fatal error : Failed to get translation for key "+ChatColor.AQUA+key;
		}
		else 
			tran = ChatColor.translateAlternateColorCodes('&', tran); //Colors
		return tran;
	}
	
	public void onDisable() {
		if (c != null) c.end();
	}
}

