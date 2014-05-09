package net.zyuiop.openUHC.events;


import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.inventory.ShapedRecipe;

public class CraftEvents implements Listener {

	@EventHandler
	public void onCraft(CraftItemEvent ev) {
		try {
			if (ev.getRecipe() instanceof ShapedRecipe) {
				ev.setCancelled(true);
			}
			if (ev.isCancelled()) ((Player) ev.getWhoClicked()).sendMessage(ChatColor.RED+"Vous ne pouvez pas crafter le melon comme ceci");
			
		} catch (Exception e) {
			Bukkit.getLogger().warning(ChatColor.RED+"Erreur dans le craft");
			e.printStackTrace();
		}
	}
}
