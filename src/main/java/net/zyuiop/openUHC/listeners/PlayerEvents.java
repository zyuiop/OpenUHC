package net.zyuiop.openUHC.listeners;

import net.zyuiop.openUHC.OpenUHC;

import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerLoginEvent.Result;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.ItemStack;

public class PlayerEvents implements Listener {
	private OpenUHC pl;
	public PlayerEvents(OpenUHC plugin) {
		this.pl = plugin;
	}
	
	@EventHandler
	public void joinEvent(PlayerLoginEvent e) {
		if (!pl.getGame().canJoin() && !e.getPlayer().hasPermission("uhpl.join") && !pl.isIngame(e.getPlayer())) {
			if(pl.getConfig().getBoolean("allow_spectators")) {
				pl.getSpectatorManager().addPlayer(e.getPlayer());
				e.getPlayer().sendMessage(pl.localize("join_as_spectator"));
				return;
			}
			e.disallow(Result.KICK_OTHER, pl.localize("game_already_started"));
			return;
		}
		
			
	}
	
	@EventHandler
	public void join(PlayerJoinEvent e) {
		e.getPlayer().setScoreboard(pl.getSbManager().getMainScoreboard());
		if (pl.isIngame(e.getPlayer())) {
			e.getPlayer().sendMessage(ChatColor.GOLD+"Re !");
			Location l = e.getPlayer().getLocation();
			if (!pl.isInLimits(l.getBlockX(), l.getBlockZ())) {
				e.getPlayer().teleport(pl.getRandLoc());
				e.getPlayer().sendMessage(pl.localize("out_of_limits"));
			}
		}
	}
	
	@EventHandler
	public void onMove(PlayerMoveEvent e) {
		Location l = e.getTo();
		if (pl.isIngame(e.getPlayer())) {
			if (!pl.isInLimits(l.getBlockX(), l.getBlockZ())) {
				Location p = e.getFrom();
				if (!pl.isInLimits(p.getBlockX(), p.getBlockZ())) {
					e.getPlayer().teleport(pl.getRandLoc());
					return;
				}
				e.setCancelled(true);
			}
		}
	}
	
	@EventHandler
	public void playerDeath(PlayerDeathEvent death) {
			Player p = (Player) death.getEntity();
			pl.deletePlayer(p);
			if (!pl.getConfig().getBoolean("allow_spectators"))
				p.kickPlayer("Vous êtes mort !");
				
		
	}
	@EventHandler
	public void respawn(PlayerRespawnEvent e) {
		Player p = e.getPlayer();
		if (!pl.isIngame(p) && !pl.getGame().isFinished()) {
			p.sendMessage(ChatColor.RED + "Vous êtes mort !");
			p.sendMessage(ChatColor.GREEN + "Vous êtes désormais spectateur.");
			pl.getSpectatorManager().addPlayer(p);
		}
	}
	@EventHandler
	public void interact(PlayerInteractEvent e) {
		if (pl.getSpectatorManager().isSpectator(e.getPlayer())) {
			if ((e.getAction() == Action.RIGHT_CLICK_AIR || e.getAction() == Action.RIGHT_CLICK_BLOCK) && e.getPlayer().getItemInHand().getType() == Material.COMPASS) {
				pl.getSpectatorManager().playersInventory(e.getPlayer());
			}
		}
		
		e.setCancelled(!pl.getGame().getStarted() || !pl.isIngame(e.getPlayer()));
	}
	
	@EventHandler
	public void itemPickup(PlayerPickupItemEvent e) {
		e.setCancelled(!pl.getGame().getStarted() || !pl.isIngame(e.getPlayer()));
	}
	
	@SuppressWarnings("deprecation")
	@EventHandler
	public void onPlayerInteract(PlayerInteractEvent ev) {
		if (pl.isIngame(ev.getPlayer()) && (ev.getAction() == Action.RIGHT_CLICK_AIR || ev.getAction() == Action.RIGHT_CLICK_BLOCK) && ev.getPlayer().getItemInHand().getType() == Material.COMPASS && pl.isCompassEnabled()) {
			Player p = ev.getPlayer();
			Boolean foundItem = false;
			Material mat = Material.ROTTEN_FLESH;
			String item = "rotten flesh";
			if (pl.isCompassHarder()) {
				mat = Material.SULPHUR;
				item = "gunpowder";
			}
			for (ItemStack is : p.getInventory().getContents()) {
				if (is != null && is.getType() == mat) {
					if (is.getAmount() != 1) is.setAmount(is.getAmount()-1);
					else { p.getInventory().removeItem(is); }
					p.updateInventory();
					foundItem = true;
					break;
				}
			}
			if (!foundItem) {
				p.sendMessage(ChatColor.GRAY+""+ChatColor.ITALIC+"Vous n'avez pas de "+item);
				return;
			}
			Player nearest = null;
			Double distance = 99999D;
			for (Player pl2 : p.getServer().getOnlinePlayers()) {
				try {	
					Double calc = p.getLocation().distance(pl2.getLocation());
					if (calc > 1 && calc < distance) {
							distance = calc;
							if (pl2 != p && pl.getTeam(pl2) != pl.getTeam(p)) nearest = pl2.getPlayer();
					}
				} catch (Exception e) {}
			}
			if (nearest == null) {
				p.sendMessage(ChatColor.RED+""+ChatColor.ITALIC+"Aucune joueur trouvé");
				return;
			}
				p.sendMessage(ChatColor.GREEN+"La boussole pointe sur le joueur le plus proche.");
				p.setCompassTarget(nearest.getLocation());
		}
	}
	@EventHandler
	public void itemDrop(PlayerDropItemEvent e) {
		e.setCancelled(!pl.isIngame(e.getPlayer()) || !pl.getGame().getStarted());
	}
}
