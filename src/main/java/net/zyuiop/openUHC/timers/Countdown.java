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
            	String evname = plugin.localize("damage_disabled_tag");
            	plugin.getSbManager().setPhase(evname);
            	plugin.getSbManager().formatTime(counter);
            	plugin.getSbManager().refresh();
            	
            	String countdownMsg = plugin.localize("damage_disabled_timer");
                switch (counter) {
                case 120:
                	Bukkit.broadcastMessage(countdownMsg.replace("{TIME}", "2 " + plugin.localize("minutes")));
                	break;
                case 60:
                	Bukkit.broadcastMessage(countdownMsg.replace("{TIME}", "1 " + plugin.localize("minutes")));
                	break;
                case 30:
                	Bukkit.broadcastMessage(countdownMsg.replace("{TIME}", "30 " + plugin.localize("seconds")));
                	break;
                case 10:
                	Bukkit.broadcastMessage(countdownMsg.replace("{TIME}", "10 " + plugin.localize("seconds")));
                	break;
                default:
                	if (counter <= 5)
                		Bukkit.broadcastMessage(countdownMsg.replace("{TIME}", counter + " " +plugin.localize("seconds")));
                	
                }
                
                counter--;
            } else {
            	plugin.getSbManager().formatTime(0);
            	plugin.getSbManager().refresh();
            	Bukkit.broadcastMessage(plugin.localize("damage_enable_message"));
            	plugin.getGame().enableDegats();
            	plugin.getGame().runGame();
                this.cancel();
            }
        }
     
    }