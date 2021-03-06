package net.zyuiop.openUHC.listeners;

import java.util.Date;

import net.zyuiop.openUHC.OpenUHC;
import net.zyuiop.openUHC.teams.UHTeam;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
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
import org.bukkit.event.player.PlayerLoginEvent.Result;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.event.player.PlayerQuitEvent;
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
		if (pl.logout_times.containsKey(e.getPlayer().getUniqueId()) && pl.getGame().getStarted() && pl.isIngame(e.getPlayer())) {
			int max_minutes = pl.getConfig().getInt("logout_time", 15);
			long difference = new Date().getTime() - pl.logout_times.get(e.getPlayer().getUniqueId()).getTime();
			if (difference >= max_minutes * 60 * 1000) {
				pl.deletePlayer(e.getPlayer().getName());
				e.disallow(Result.KICK_OTHER, pl.localize("kick_logout_too_long"));
			} 
		}
		
		
			
	}
	
	@EventHandler
	public void join(PlayerJoinEvent e) {
		e.getPlayer().setScoreboard(pl.getSbManager().getMainScoreboard());
		if (pl.isIngame(e.getPlayer())) {
			e.getPlayer().sendMessage(ChatColor.GOLD+"Re !");
			Location l = e.getPlayer().getLocation();
			if (!pl.isInLimits(l.getBlockX(), l.getBlockZ())) {
				e.getPlayer().teleport(pl.getSafeRandLoc());
				e.getPlayer().sendMessage(pl.localize("out_of_limits"));
			}
		}
		
	}
	@EventHandler
	public void onQuit(PlayerQuitEvent e) {
		 if (pl.getGame().getStarted()) pl.logout_times.put(e.getPlayer().getUniqueId(), new Date());
		 Player p = e.getPlayer();
		 if (pl.isIngame(p) && pl.getPlayers().size() == 2) {
			 pl.deletePlayer(p.getName());
		 } else if (pl.isIngame(p) && pl.teamManager().getTeamsList().size() == 2) {
			 UHTeam playerTeam = pl.getTeam(p.getName());
			 if (playerTeam != null) {
				 // On vérifie si l'équipe est vide
				 for (String joueur : playerTeam.getPlayers()) {
					 if (Bukkit.getPlayer(joueur) != null && !joueur.equals(p.getName()))
						return;
				 }
				 // Equipe vide : on déclare la victoire
				 pl.localize("team_stumped").replace("{TEAM}", playerTeam.getColorizedName());
				 pl.teamManager().deleteTeam(playerTeam.getName());
				 pl.deletePlayer(p.getName());
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
					e.getPlayer().teleport(pl.getSafeRandLoc());
					return;
				}
				e.setCancelled(true);
			}
		}
	}
	
	@EventHandler
	public void playerDeath(PlayerDeathEvent death) {
			Player p = (Player) death.getEntity();
			pl.deletePlayer(p.getName());
			if (!pl.getConfig().getBoolean("allow_spectators"))
				p.kickPlayer(pl.localize("kick_dead"));
				
		
	}
	@EventHandler
	public void respawn(PlayerRespawnEvent e) {
		Player p = e.getPlayer();
		if (!pl.isIngame(p) && !pl.getGame().isFinished()) {
			p.sendMessage(pl.localize("kick_dead"));
			p.sendMessage(pl.localize("respawn_as_spectator"));
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
				p.sendMessage(pl.localize("missing_item").replace("{ITEM}", item));
				return;
			}
			Player nearest = null;
			Double distance = 99999D;
			for (Player pl2 : p.getServer().getOnlinePlayers()) {
				try {	
					Double calc = p.getLocation().distance(pl2.getLocation());
					if (calc > 1 && calc < distance) {
							distance = calc;
							if (pl2 != p && pl.getTeam(pl2.getName()) != pl.getTeam(p.getName())) nearest = pl2.getPlayer();
					}
				} catch (Exception e) {}
			}
			if (nearest == null) {
				p.sendMessage(pl.localize("no_player"));
				return;
			}
				p.sendMessage(pl.localize("point_at_player"));
				p.setCompassTarget(nearest.getLocation());
		}
	}
	@EventHandler
	public void itemDrop(PlayerDropItemEvent e) {
		e.setCancelled(!pl.isIngame(e.getPlayer()) || !pl.getGame().getStarted());
	}
}
