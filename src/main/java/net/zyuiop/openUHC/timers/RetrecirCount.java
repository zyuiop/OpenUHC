package net.zyuiop.openUHC.timers;

import java.util.ArrayList;

import net.zyuiop.openUHC.OpenUHC;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
     
public class RetrecirCount extends BukkitRunnable {
     
        private final OpenUHC plugin;
     
        private int counter;
        
        
        private ArrayList<Integer> nlimits;
     
        public RetrecirCount(OpenUHC openUHC, int counter, ArrayList<Integer> nlimits) {
            this.plugin = openUHC;
            this.nlimits = nlimits;
            
            if (counter < 1) {
                throw new IllegalArgumentException("counter must be greater than 1");
            } else {
                this.counter = counter;
            }
        }
     
        public void run() {
            // What you want to schedule goes here
            if (counter > 0) { 
            	
            	String evnamechat = "Rétrecissement de la map";
            	String time = null;
            	boolean avert = false;
                switch (counter) {
                case 120:
                	time = "dans 2 minutes";
                	break;
                case 90:
                	time = "dans 1 minute 30";
                	break;
                case 60:
                	time = " dans 1 minute";
                	break;
                case 30:
                	time = " dans 30 secondes";
                	break;
                case 10:
                	time = " dans 10 secondes";
                	break;
                case 5:
                	time = " dans 5 secondes";
                	break;
                	
                }
                if (time != null) {
                	Bukkit.broadcastMessage(ChatColor.GOLD+evnamechat+time+". Nouvelles limites : x("+nlimits.get(0)+","+nlimits.get(1)+") z("+nlimits.get(2)+","+nlimits.get(3)+")");
                	for (Player p : Bukkit.getOnlinePlayers()) {
                		if (!plugin.isInLimits(p.getLocation().getBlockX(),p.getLocation().getBlockZ())) {
                			p.sendMessage(ChatColor.RED+"Vous êtes EN DEHORS des prochaines limites !");
                		}
                	}
                }
                counter--;
            } else {
            	Bukkit.broadcastMessage(ChatColor.GOLD+"La map est en cours de rétrécissement. Nouvelles limites : x("+nlimits.get(0)+","+nlimits.get(1)+") z("+nlimits.get(2)+","+nlimits.get(3)+")");
                plugin.reduceSize(nlimits);
                this.cancel();
            }
        }
     
    }