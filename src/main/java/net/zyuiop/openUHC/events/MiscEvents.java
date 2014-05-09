package net.zyuiop.openUHC.events;


import net.zyuiop.openUHC.OpenUHC;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;

public class MiscEvents implements Listener {

	private OpenUHC pl;
	public MiscEvents(OpenUHC plugin) {
		this.pl = plugin;
	}
	
	@EventHandler
	public void foodLevelChange(FoodLevelChangeEvent e) {
		e.setCancelled(!pl.getStarted());
	}
	
	@EventHandler
	public void creatureSpawn(CreatureSpawnEvent e) {
		e.setCancelled(!pl.getStarted());
	}
	
}
