package net.zyuiop.openUHC.spectators;

import java.util.ArrayList;
import java.util.List;

import net.zyuiop.openUHC.OpenUHC;

import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class SpectatorManager {
	protected final OpenUHC pl;
	protected List<Player> spectators = new ArrayList<Player>();
	
	/**
	 * 
	 * @param pl OpenUHC plugin
	 */
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
		p.setAllowFlight(true);
		p.setHealth(p.getMaxHealth());
		p.setFoodLevel(20);
		for (Player player : pl.getServer().getOnlinePlayers()) {
			if (!isSpectator(player)) {
				player.hidePlayer(p);
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
		Inventory inv = this.pl.getServer().createInventory(destination, 54, "- Joueurs -");
		Integer slot = 0;
		ItemStack is = null;
		for (Player p : pl.getPlayers()) {
			is = new ItemStack(Material.SKULL);
			ItemMeta im = is.getItemMeta();
			im.setDisplayName(p.getName());
			is.setItemMeta(im);
			inv.setItem(slot, is);
			slot++;
		}
	}
}
