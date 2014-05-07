package me.zyuiop.uhplugin;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Difficulty;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapelessRecipe;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;


public class UHPlugin extends JavaPlugin {

	private boolean gameStarted = false;
	
	private HashMap<String, ArrayList<String>> teams = new HashMap<String, ArrayList<String>>();
	private HashMap<String, ChatColor> teamsColors = new HashMap<String, ChatColor>();
	private ArrayList<String> joueurs = new ArrayList<String>(); // Répertorie joueurs online
	
	private boolean solo = true;
	private boolean canJoin = true;
	
	private boolean pvp = false;
	private boolean degats = false;
	
	private Scoreboard sb;
	private Objective right;
	
	private ArrayList<Integer> limits = new ArrayList<Integer>();
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
		UHCommands c = new UHCommands(this);
		getCommand("teams").setExecutor(c);
		getCommand("players").setExecutor(c);
		getCommand("gamestart").setExecutor(c);
		getCommand("retrecir").setExecutor(c);
		getCommand("limites").setExecutor(c);
		
		getServer().getPluginManager().registerEvents(new UHEvents(this), this);
		
		this.saveDefaultConfig();
		
		
		
		// Set craft //
		ShapelessRecipe goldenMelon = new ShapelessRecipe(new ItemStack(Material.SPECKLED_MELON));
		goldenMelon.addIngredient(1, Material.GOLD_BLOCK);
		goldenMelon.addIngredient(1, Material.MELON);
		this.getServer().addRecipe(goldenMelon);
	}
	
	
	public void generateWalls() {
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
				showProgress(done,work);
				done++;
				showProgress(done,work);
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
				showProgress(done,work);
				done++;
				showProgress(done,work);
			}
			z++;
		}
		Bukkit.broadcastMessage(ChatColor.GRAY+""+ChatColor.ITALIC+"Génération des murs terminée !");

	}
	
	public void showProgress(double done, double work) {
		if ((done/work) == 0.1)
			Bukkit.broadcastMessage(ChatColor.GRAY+""+ChatColor.ITALIC+"Génération des murs : 10%");
		if ((done/work) == 0.2)
			Bukkit.broadcastMessage(ChatColor.GRAY+""+ChatColor.ITALIC+"Génération des murs : 20%");
		if ((done/work) == 0.3)
			Bukkit.broadcastMessage(ChatColor.GRAY+""+ChatColor.ITALIC+"Génération des murs : 30%");
		if ((done/work) == 0.4)
			Bukkit.broadcastMessage(ChatColor.GRAY+""+ChatColor.ITALIC+"Génération des murs : 40%");
		if ((done/work) == 0.5)
			Bukkit.broadcastMessage(ChatColor.GRAY+""+ChatColor.ITALIC+"Génération des murs : 50%");
		if ((done/work) == 0.6)
			Bukkit.broadcastMessage(ChatColor.GRAY+""+ChatColor.ITALIC+"Génération des murs : 60%");
		if ((done/work) == 0.7)
			Bukkit.broadcastMessage(ChatColor.GRAY+""+ChatColor.ITALIC+"Génération des murs : 70%");
		if ((done/work) == 0.8)
			Bukkit.broadcastMessage(ChatColor.GRAY+""+ChatColor.ITALIC+"Génération des murs : 80%");
		if ((done/work) == 0.9)
			Bukkit.broadcastMessage(ChatColor.GRAY+""+ChatColor.ITALIC+"Génération des murs : 90%");
	}
	
	public boolean isIngame(String pseudo) {
		return joueurs.contains(pseudo);
	}
	
	public ChatColor getCol(int num) {
		switch (num) {
		case 5:
			return ChatColor.DARK_BLUE;
		case 6:
			return ChatColor.DARK_GREEN;
		case 7:
			return ChatColor.DARK_RED;
		case 8:
			return ChatColor.DARK_AQUA;
		case 9:
			return ChatColor.DARK_PURPLE;
		case 1:
			return ChatColor.GOLD;
		case 2:
			return ChatColor.BLUE;
		case 3:
			return ChatColor.GREEN;
		case 0:
			return ChatColor.AQUA;
		case 4:
			return ChatColor.RED;
		case 10:
			return ChatColor.LIGHT_PURPLE;
		case 11:
			return ChatColor.GRAY;
		default:
			return ChatColor.WHITE;
		}
	}
	
	public ChatColor getTeamColor(String team) {
		return this.teamsColors.get(team);
	}
	
	public World getWorld() {
		return Bukkit.getWorld(this.getConfig().getString("world","world"));
	}
	
	public void setupScoreboards() {
		sb.registerNewObjective("vie", "health").setDisplaySlot(DisplaySlot.PLAYER_LIST);
		if (solo == false) {
			int c = 0;
			for (String t : teams.keySet()) {
				Team te = sb.registerNewTeam(t);
				for (String pl : teams.get(t)) {
					te.addPlayer(Bukkit.getOfflinePlayer(pl));
					this.teamsColors.put(t, getCol(c));
					try {
						Player p = Bukkit.getPlayer(pl);
						p.setDisplayName(getCol(c)+p.getName());
				        p.sendMessage("Vous avez rejoint l'équipe "+getCol(c)+t);
					} catch(Exception e) {
						
					}
					joueurs.add(pl);
				}
				te.setPrefix(getCol(c)+"");
				c++;
				if (c > 11)
					c=0;
			}
		}
		
		/*right.setDisplayName("UHC Games");
		right.setDisplaySlot(DisplaySlot.SIDEBAR);
		if (solo == false)
			right.getScore(Bukkit.getOfflinePlayer("Equipes : "+ChatColor.AQUA+teams.size())).setScore(2);
		right.getScore(Bukkit.getOfflinePlayer("Joueurs : "+ChatColor.AQUA+joueurs.size())).setScore(1);
		*/
		scoreboard();
		for (Player p : Bukkit.getOnlinePlayers()) {
			p.setScoreboard(sb);
		}
	}
	
	public void deletePlayer(String n) {
		int team = teams.size();
		int play = joueurs.size();
		
		if (joueurs.contains(n)) {
			joueurs.remove(n);
		}
		
		String remove = null;
		
		for (String t: teams.keySet()) {
			if (teams.get(t).contains(n))
			{
				teams.get(t).remove(n);
				if (teams.get(t).size() == 0)
					remove = t;
			}
		}
		if (remove != null) {
			teams.remove(remove);
		}
	}
	
	public String phase = "Invincibilité";
	public int hours = 0;
	public int minutes = 0;
	public int seconds = 0;
	
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
				Bukkit.getServer().broadcastMessage(ChatColor.GOLD+"L'équipe "+teams.keySet().toArray()[0]+" a gagné la partie !");
				finishGame(teams.keySet().toArray()[0]+"");
			}
		} else if (solo == true && joueurs.size() == 1) {
			if (c != null) {
				c.stop();
				Bukkit.getServer().broadcastMessage(ChatColor.GOLD+"Le joueur "+joueurs.get(0)+" a gagné la partie !");
				finishGame(joueurs.get(0));
			}
		}
	}
	
	public void finishGame(String winner) {
		this.winner = winner;
		this.isWon = true;
		for (Player p : Bukkit.getOnlinePlayers()) {
			p.getInventory().clear();
			p.setGameMode(GameMode.CREATIVE);
			p.getInventory().addItem(new ItemStack(Material.FIREWORK, 1));
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
	
	
	private boolean isWon = false;
	private String winner = null;
	
	public String getTeam(String player) {
		for (String t: teams.keySet()) {
			if (teams.get(t).contains(player))
			{
				return t;
			}
		}
		return null;
	}
	
	public boolean delFromTeam(String player, String team) {
		if (teams.get(team) == null)
			return false;
		if (!teams.get(team).contains(player))
			return false;
		teams.get(team).remove(player);
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
	
	public void startGame() {
		Bukkit.broadcastMessage(ChatColor.GRAY+""+ChatColor.ITALIC+"Préparation du jeu...");
		getWorld().setGameRuleValue("doDaylightCycle", this.getConfig().getString("daylight-cycle", "true"));
		getWorld().setTime(this.getConfig().getLong("begin-time", 6000L));
		getWorld().setStorm(this.getConfig().getBoolean("begin-storm", false));
		getWorld().setDifficulty(Difficulty.HARD);
		canJoin = false;
		if (teams.size() >= 2)
			solo = false;
		else {	
			solo = true;
			for (Player p : Bukkit.getOnlinePlayers())
				joueurs.add(p.getName());
		}
		setupScoreboards();
		mapSize = getConfig().getInt("map-size");
		setLimits();
		generateWalls();
		World w = getWorld();
		Bukkit.broadcastMessage(ChatColor.GRAY+""+ChatColor.ITALIC+"Génération des chunks de spawn... ");
		HashMap<String, Location> posTp = new HashMap<String, Location>();
		if (solo) {
			for (String p : joueurs) {
				Location l = getRandLoc();
				posTp.put(p, l);
				w.getChunkAt(l).load(true);
			}
		}
		else {
			for (String t : teams.keySet()) {
				Location l = getRandLoc();
				w.getChunkAt(l).load(true);
				for (String p : teams.get(t)) {
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
		BukkitTask task = new Countdown(this, this.getConfig().getInt("damage-disable", 30), "degats").runTaskTimer(this, 0, 20);
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
		BukkitTask task = new Countdown(this, this.getConfig().getInt("pvp-disable", 120), "pvp").runTaskTimer(this, 0, 20);
	}

	public void retrecirCount(int amount) {
		ArrayList<Integer> nc = getLimits(mapSize-amount);
		mapSize = mapSize-amount;
		
		
		BukkitTask task = new RetrecirCount(this, 120, nc).runTaskTimer(this, 0, 20);
	}
	
	public boolean canTakeDamage() {
		return degats;
	}
	
	public boolean canPvP() {
		return pvp;
	}
	
	public boolean addTeam(String teamName) {
		if (teams.containsKey(teamName))
			return false;
		teams.put(teamName, new ArrayList<String>());
		return true;
	}
	
	public boolean addPlayer(String teamName, String playerName) {
		if (getTeam(playerName) != null)
			return false;
		
		if (!teams.containsKey(teamName))
			return false;
		
		if (teams.get(playerName) != null && teams.get(playerName).contains(playerName))
			return false;
		
		teams.get(teamName).add(playerName);
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
			int tpx = randomInt(limits.get(XLIMITN),limits.get(XLIMITP));
			int tpz = randomInt(limits.get(ZLIMITN),limits.get(ZLIMITP));
			int tpy = 250;
			while (w.getBlockAt(tpx, tpy, tpz).getType().equals(Material.AIR))
				tpy--;
			if (!w.getBlockAt(tpx, tpy, tpz).isLiquid())
				return new Location(w, tpx, tpy+15, tpz);
		}
	}
	
	private static int randomInt(int aStart, int aEnd){
		 Random aRandom = new Random();
		 if (aStart > aEnd) {
		    throw new IllegalArgumentException("Start cannot exceed End.");
		 }
		 //get the range, casting to long to avoid overflow problems
		 long range = (long)aEnd - (long)aStart + 1;
		 // compute a fraction of the range, 0 <= frac < range
		 long fraction = (long)(range * aRandom.nextDouble());
		 int randomNumber =  (int)(fraction + aStart);    
		 return randomNumber;
	}
}

