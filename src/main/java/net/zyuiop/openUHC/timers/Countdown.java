package net.zyuiop.openUHC.timers;

import net.zyuiop.openUHC.OpenUHC;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.scheduler.BukkitRunnable;
     
    public class Countdown extends BukkitRunnable {
     
        private final OpenUHC plugin;
     
        private int counter;
        
        private String callback;
     
        public Countdown(OpenUHC openUHC, int counter) {
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
            	String evname = "Invincibilité";
            	String evnamechat = ChatColor.GOLD+"Fin d'invincibilité";
            	
            	plugin.getSbManager().setPhase(evname);
            	plugin.getSbManager().formatTime(counter);
            	plugin.getSbManager().refresh();
            	
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
            	plugin.getSbManager().formatTime(0);
            	plugin.getSbManager().refresh();
            	Bukkit.broadcastMessage(ChatColor.GOLD+"Fin de la période d'invincibilité !");
            	plugin.getGame().enableDegats();
            	plugin.getGame().runGame();
            	
                this.cancel();
            }
        }
     
    }