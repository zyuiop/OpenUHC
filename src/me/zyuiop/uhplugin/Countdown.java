package me.zyuiop.uhplugin;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
     
    public class Countdown extends BukkitRunnable {
     
        private final UHPlugin plugin;
     
        private int counter;
        
        private String callback;
     
        public Countdown(UHPlugin plugin, int counter, String callback) {
            this.plugin = plugin;
            this.callback = callback;
            if (counter < 1) {
                throw new IllegalArgumentException("counter must be greater than 1");
            } else {
                this.counter = counter;
            }
        }
     
        @Override
        public void run() {
            // What you want to schedule goes here
            if (counter > 0) { 
            	String evname = "";
            	String evnamechat = "";
            	if (callback == "degats") {
            		evname = "Invincibilité";
            		evnamechat = ChatColor.GOLD+"Fin d'invincibilité";
            	} else if (callback == "pvp") {
            		evname = "No-PvP";
            		evnamechat = ChatColor.RED+"Activation du PvP";
            	} 
            	plugin.phase = evname;
            	plugin.formatTime(counter);
            	plugin.scoreboard();
            	
                switch (counter) {
                case 120:
                	Bukkit.broadcastMessage(evnamechat+" dans 2 minutes");
                	break;
                case 60:
                	Bukkit.broadcastMessage(evnamechat+" dans 1 minute");
                	break;
                case 30:
                	Bukkit.broadcastMessage(evnamechat+" dans 30 secondes");
                	break;
                case 10:
                	Bukkit.broadcastMessage(evnamechat+" dans 10 secondes");
                	break;
                default:
                	if (counter <= 5)
                		Bukkit.broadcastMessage(evnamechat+" dans "+counter+" seconde"+((counter > 1) ? "s" : ""));
                	
                }
                
                counter--;
            } else {
            	String evname = "";
            	String evnamechat = "";
            	if (callback == "degats") {
            		evname = "Invincibilité";
            		Bukkit.broadcastMessage("Fin de la période d'invincibilité !");
            		plugin.enableDegats();
            	} else if (callback == "pvp") {
            		evname = "No-PvP";
            		Bukkit.broadcastMessage("Le PvP est activé !");
            		plugin.enablePVP();
            	}
            	plugin.hours = 0;
            	plugin.minutes = 0;
            	plugin.seconds = 0;
            	plugin.scoreboard();
            	
                this.cancel();
            }
        }
     
    }