package net.zyuiop.openUHC.listeners;


import java.util.Map;

import net.zyuiop.openUHC.OpenUHC;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.ShapelessRecipe;
import org.bukkit.inventory.meta.SkullMeta;

public class InventoryEvents implements Listener {

	private OpenUHC pl;
	public InventoryEvents(OpenUHC pl) {
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
			Bukkit.getLogger().warning(ChatColor.RED+"An error occured when trying to analyse craft.");
			((Player) ev.getWhoClicked()).sendMessage(ChatColor.DARK_RED+"Fatal error : cannot analyse craft. "+ChatColor.AQUA+e.getLocalizedMessage());
			e.printStackTrace();
		}
	}
	
	@EventHandler
	public void onInventoryClick(InventoryClickEvent e) {
		if (e.getInventory().getName() == pl.getConfig().getString("players_inventory_name", "Joueurs en jeu")) {
			if (e.getWhoClicked() instanceof Player) {
				Player p = (Player) e.getWhoClicked();
				if (pl.getSpectatorManager().isSpectator(p)) {
					e.setCancelled(true);
					if (e.getCurrentItem().getType() == Material.IRON_DOOR) {
						p.closeInventory();
					} else if (e.getCurrentItem().getType() != null) {
						p.closeInventory();
						SkullMeta meta = (SkullMeta) e.getCurrentItem().getItemMeta();
						if (pl.isIngame(meta.getOwner())) {
							Player dest = Bukkit.getPlayer(meta.getOwner());
							if (dest == null) {
								p.sendMessage(pl.localize("player_not_online"));
							} else {
								p.teleport(dest);
							}
						} else {
							p.sendMessage(pl.localize("player_not_ingame"));
						}
					}
				}
			}
		}
	}
}
