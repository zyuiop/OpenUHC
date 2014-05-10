package net.zyuiop.openUHC.events;


import java.util.Map;

import net.zyuiop.openUHC.OpenUHC;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.ShapelessRecipe;

public class CraftEvents implements Listener {

	private OpenUHC pl;
	public CraftEvents(OpenUHC pl) {
		this.pl = pl;
	}
	
	@EventHandler
	public void onCraft(CraftItemEvent ev) {
		try {
			if (ev.getRecipe() instanceof ShapedRecipe) {
				ShapedRecipe r = (ShapedRecipe)ev.getRecipe();
				if (r.getResult().getType() == Material.GOLDEN_APPLE && !((Recipe)r).equals((Recipe)pl.getMelonRecipe())) 
					ev.setCancelled(true);
				if (r.getResult().getType() == Material.COMPASS && pl.isCompassEnabled() && !r.equals(pl.getCompassRecipe())) 
					ev.setCancelled(true);
			} else if (ev.getRecipe() instanceof ShapelessRecipe) {
				ShapelessRecipe r = (ShapelessRecipe) ev.getRecipe();
				if (r.getResult().getType() == Material.GOLDEN_APPLE && !r.equals(pl.getMelonRecipe())) 
					ev.setCancelled(true);
			}
		} catch (Exception e) {
			Bukkit.getLogger().warning(ChatColor.RED+"Une erreur s'est produite durant l'analyse du craft");
			e.printStackTrace();
		}
	}
}
