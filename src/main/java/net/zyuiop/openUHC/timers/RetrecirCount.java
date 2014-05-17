package net.zyuiop.openUHC.timers;

import java.util.ArrayList;

import net.zyuiop.openUHC.OpenUHC;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
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
            	
            	String msg = plugin.localize("shrink_timer_message");
            	String time = null;
                switch (counter) {
                case 120:
                	time = 2 + " " + plugin.localize("minutes");
                	break;
                case 90:
                	time = "1 " + plugin.localize("minutes") + " 30 " + plugin.localize("seconds");
                	break;
                case 60:
                	time = "1 " + plugin.localize("minutes");
                	break;
                case 30:
                	time = "30 " + plugin.localize("seconds");
                	break;
                case 10:
                	time = "10 " + plugin.localize("seconds");
                	break;
                case 5:
                	time = "5 " + plugin.localize("seconds");
                	break;
                case 4:
                	time = "4 " + plugin.localize("seconds");
                	break;
                case 3:
                	time = "3 " + plugin.localize("seconds");
                	break;
                case 2:
                	time = "2 " + plugin.localize("seconds");
                	break;
                case 1:
                	time = "1 " + plugin.localize("seconds");
                	break;
                	
                }
                if (time != null) {
                	Bukkit.broadcastMessage(msg.replace("{COORDINATES}", "x(" + nlimits.get(0)+","+nlimits.get(1)+") z("+nlimits.get(2)+","+nlimits.get(3)+")").replace("{TIME}", time));
                	for (Player p : Bukkit.getOnlinePlayers()) {
                		if (!plugin.isInLimits(p.getLocation().getBlockX(),p.getLocation().getBlockZ(), nlimits)) {
                			p.sendMessage(plugin.localize("out_of_new_limits"));
                		}
                	}
                }
                counter--;
            } else {
            	Bukkit.broadcastMessage(plugin.localize("shrink").replace("{COORDINATES}", "x("+nlimits.get(0)+","+nlimits.get(1)+") z("+nlimits.get(2)+","+nlimits.get(3)+")"));
                plugin.reduceSize(nlimits);
                this.cancel();
            }
        }
     
    }