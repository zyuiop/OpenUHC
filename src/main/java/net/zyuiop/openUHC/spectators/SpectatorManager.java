package net.zyuiop.openUHC.spectators;

import java.util.ArrayList;
import java.util.List;

import net.zyuiop.openUHC.OpenUHC;
import net.zyuiop.openUHC.utils.UHUtils;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.SkullType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.material.Skull;

public class SpectatorManager {
	protected final OpenUHC pl;
	protected List<Player> spectators = new ArrayList<Player>();
	
	public SpectatorManager(OpenUHC pl) {
		this.pl = pl;
	}
	/**
	 * Add a player to spectators
	 * @param p Player to be added
	 */
	public void addPlayer(Player p) {
		if (!isSpectator(p))
			spectators.add(p);
		p.setGameMode(GameMode.CREATIVE);
		p.getInventory().clear();
		ItemStack playersItem = new ItemStack(Material.COMPASS, 1);
		ItemMeta meta = playersItem.getItemMeta();
		meta.setDisplayName(pl.localize("compass_item_name"));
		playersItem.setItemMeta(meta);
		p.getInventory().setItemInHand(playersItem);
		for (Player player : pl.getServer().getOnlinePlayers()) {
			if (!isSpectator(player)) {
				player.hidePlayer(p);
			}
			else {
				p.showPlayer(player);
			}
		}
	}
	
	/**
	 * Check whether the player is a spectator
	 * @param p The player to check
	 * @return 
	 */
	public boolean isSpectator(Player p) {
		return spectators.contains(p);
	}
	
	public void playersInventory(Player destination) {
		Inventory inv = this.pl.getServer().createInventory(destination, UHUtils.getClosestChestSize(Bukkit.getServer().getMaxPlayers()+1), pl.localize("compass_inv_name"));
		Integer slot = 0;
		ItemStack is = null;
		for (Player p : pl.getPlayers()) {
			if (p == destination)
				continue;
			ItemStack skull = new ItemStack(Material.SKULL_ITEM, 1, (short) SkullType.PLAYER.ordinal());
	        SkullMeta skullMeta = (SkullMeta) skull.getItemMeta();
	        skullMeta.setOwner(p.getName());
	        if (pl.getGame().isSolo())
	        	skullMeta.setDisplayName(p.getName());
	        else
	        	skullMeta.setDisplayName(pl.getTeam(p).getColor()+p.getName());
	        skull.setItemMeta(skullMeta);
	        inv.addItem(skull);
			slot++;
		}
		ItemStack close = new ItemStack(Material.IRON_DOOR, 1);
		ItemMeta meta = close.getItemMeta();
		meta.setDisplayName(pl.localize("compass_close"));
		close.setItemMeta(meta);
		inv.setItem(inv.getSize()-1, close);
		destination.openInventory(inv);
		
	}
	
	
}
