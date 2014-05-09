package net.zyuiop.openUHC.timers;

import net.zyuiop.openUHC.OpenUHC;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.scheduler.BukkitRunnable;
     
    public class Countdown extends BukkitRunnable {
     
        private final OpenUHC plugin;
     
        private int counter;
        
        private String callback;
     
        public Countdown(OpenUHC openUHC, int counter, String callback) {
            this.plugin = openUHC;
            this.callback = callback;
            if (counter < 1) {
                throw new IllegalArgumentException("counter must be greater than 1");
            } else {
                this.counter = counter;
            }
        }
     
        
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
            	if (callback == "degats") {
            		Bukkit.broadcastMessage(ChatColor.GOLD+"Fin de la période d'invincibilité !");
            		plugin.enableDegats();
            	} else if (callback == "pvp") {
            		Bukkit.broadcastMessage(ChatColor.RED+"Le PvP est activé !");
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